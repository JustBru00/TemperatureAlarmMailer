package com.rbrubaker.temperature_alarm_mailer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import com.rbrubaker.e2e4j.E2e;
import com.rbrubaker.e2e4j.beans.ExpandedStatus;
import com.rbrubaker.e2e4j.beans.MultiExpandedStatus;

import io.prometheus.client.exporter.HTTPServer;
import kong.unirest.UnirestException;

/**
* TemperatureAlarmMailer - A small program that monitors temperatures and sends alarm emails.
*   Copyright (C) 2021 Rufus Brubaker Refrigeration
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>
*   
*   You can contact us at rbr@rbrubaker.com.
* 
* @author Justin Brubaker
*
*/
public class TemperatureAlarmMailer {
	
	public static boolean running = true;
	public static final String VERSION = "Version 0.2.1";	
	private static Instant lastUpdate;
	
	private static HTTPServer server;
	
	public static void main(String[] args) {
		System.out.println("TemperatureAlarmMailer " + VERSION);
		System.out.println("TemperatureAlarmMailer is Copyright 2021 Rufus Brubaker Refrigeration. Software created by Justin Brubaker.");
		System.out.println("TemperatureAlarmMailer - A small program that monitors temperatures and sends alarm emails.\r\n"
				+ "*   Copyright (C) 2021 Rufus Brubaker Refrigeration\r\n"
				+ "*\r\n"
				+ "*   This program is free software: you can redistribute it and/or modify\r\n"
				+ "*   it under the terms of the GNU General Public License as published by\r\n"
				+ "*   the Free Software Foundation, either version 3 of the License, or\r\n"
				+ "*   (at your option) any later version.\r\n"
				+ "*\r\n"
				+ "*   This program is distributed in the hope that it will be useful,\r\n"
				+ "*   but WITHOUT ANY WARRANTY; without even the implied warranty of\r\n"
				+ "*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\r\n"
				+ "*   GNU General Public License for more details.\r\n"
				+ "*\r\n"
				+ "*   You should have received a copy of the GNU General Public License\r\n"
				+ "*   along with this program.  If not, see <https://www.gnu.org/licenses/>\r\n"
				+ "*   \r\n"
				+ "*   You can contact us at rbr@rbrubaker.com.");
		System.out.println("* https://github.com/JustBru00/TemperatureAlarmMailer");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	            try {	            	
	                Thread.sleep(200);
	                server.stop();
	                System.out.println("\nReceived shutdown request from system. (CTRL-C)");
	                
	                running = false;	                
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    });
		
		if (!ConfigReader.loadConfig()) {
			return;
		}		
		
		try {
			server = new HTTPServer(9998);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		while (running) {
			
			// Wait a while. -> Poll E2e -> Check temperatures against alarm list -> Send email alarms.
			if (lastUpdate == null) {
				lastUpdate = Instant.now();
			}
			// Every 5 minutes
			if (Duration.between(lastUpdate, Instant.now()).getSeconds() >= 300) {
				//System.out.println(TemperatureAlarmMailer.getLogTimeStamp() + " Polling E2e...");
				// Poll E2e
				ArrayList<String> pointers = new ArrayList<String>();
				ArrayList<AlarmZone> zones = ConfigReader.getAlarmZones();
				
				// Get a list of pointers
				for (AlarmZone alarmZone : zones) {
					pointers.add(alarmZone.getControllerPointer());
					//System.out.println(TemperatureAlarmMailer.getLogTimeStamp() + " Read Pointer: " + alarmZone.getControllerPointer());
				}
				
				try {
					// Connect to the E2e
					E2e e2e = new E2e(ConfigReader.getE2eIpAddress());
					
					Optional<MultiExpandedStatus> multiStatusOptional = e2e.getMultiExpandedStatus(pointers);
					
					if (multiStatusOptional.isPresent()) {
						// Compare the temperature data with the alarm setpoints.
						MultiExpandedStatus multiStatus = multiStatusOptional.get();
						ArrayList<ExpandedStatus> expandedStatuses = multiStatus.getExpandedStatuses();
						
						if (expandedStatuses.size() == pointers.size()) {
							// We have the same amount of statuses returned as we gave in pointers.
							for (int i = 0; i < pointers.size(); i++) {
								ExpandedStatus thisStatus = expandedStatuses.get(i);
								AlarmZone thisZone = zones.get(i);
								
								// Add Prometheus Endpoint
								thisZone.getRoomTemperatureGauge().set(thisStatus.getValueAsDouble());
								
								try {
									// High Temperature
									if (thisZone.getHighTempAlarm() < thisStatus.getValueAsDouble()) {
										// Temperature is above high temp alarm point
										if (thisZone.getHighTempAlarmTripStart() == null) {											
											thisZone.setHighTempAlarmTripStart(Instant.now());
										} else {											
											if (Duration.between(thisZone.getHighTempAlarmTripStart(), Instant.now()).getSeconds() / 60 >= thisZone.getHighTempAlarmDelay()) {
												System.out.println("Zone: " + thisZone.getName() + " is above " + thisZone.getHighTempAlarm() + " for " + thisZone.getHighTempAlarmDelay() + " Value: " + thisStatus.getValue());
												
												new Thread(new Runnable() {
													public void run() {
														MailManager.sendHighTempAlarm(thisZone, thisStatus);													
													}
												}).start();
												
												// Enforce single email per alarm delay.
												thisZone.setHighTempAlarmTripStart(null);
											}											
										}										
									} else {
										// Temperature dropped below high alarm setpoint.
										thisZone.setHighTempAlarmTripStart(null);
									}
									
									// Low Temperature
									if (thisZone.getLowTempAlarm() > thisStatus.getValueAsDouble()) {
										// Temperatures is below low temp alarm setpoint.
										if (thisZone.getLowTempAlarmTripStart() == null) {
											thisZone.setLowTempAlarmTripStart(Instant.now());
										} else {
											if (Duration.between(thisZone.getLowTempAlarmTripStart(), Instant.now()).getSeconds() / 60 >= thisZone.getLowTempAlarmDelay()) {
												System.out.println("Zone: " + thisZone.getName() + " is below " + thisZone.getHighTempAlarm() + " for " + thisZone.getHighTempAlarmDelay() + " Value: " + thisStatus.getValue());
												
												new Thread(new Runnable() {
													public void run() {
														MailManager.sendLowTempAlarm(thisZone, thisStatus);														
													}
												}).start();
												
												// Enforce single email per alarm delay
												thisZone.setLowTempAlarmTripStart(null);
											}
										}
 									} else {
										// Temperature rose above low alarm setpoint
										thisZone.setLowTempAlarmTripStart(null);
									}									
								} catch (NullPointerException e) {
									System.out.println("ERROR: NullPointerException. Skipping " + thisZone.getName());
									e.printStackTrace();
									continue;
								} catch (NumberFormatException e2) {
									System.out.println("ERROR: NumberFormatException. Skipping " + thisZone.getName());
									e2.printStackTrace();
									continue;
								}
							}
						} else {
							System.out.println("ERROR: The amount of expanded statuses doesn't match the amount of pointers.");
						}						
					} else {
						System.out.println("ERROR: MultiExpandedStatus was empty. Is something wrong with your pointers or the E2e?");
					}					
				} catch (UnirestException e) {
					System.out.println("ERROR: UnirestException - Something went wrong.");
					e.printStackTrace();
				}		
				lastUpdate = Instant.now();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
		}
		
	}
	
	public static String getLogTimeStamp() {
		return "[" + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SS").format(new Date()) + "]";
	}
}
