package com.rbrubaker.temperature_alarm_mailer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

/**
* TemperatureAlarmMailer - A small program that monitors temperatures and sends alarm emails.
*   Copyright (C) 2021 Rufus Brubaker Refrigeration LLC
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
public class ConfigReader {
	
	private static YamlFile config;
	
	private static String deviceName;
	private static String deviceLocation;
	
	private static String emailUsername;
	private static String emailPassword;
	private static String emailAddress;
	private static String emailServerHost;
	private static int emailPortNumber;
	private static String emailHighTempAlarmHtml;
	private static String emailLowTempAlarmHtml;
	
	private static ArrayList<String> globalReceiverAddresses = new ArrayList<String>();
	
	private static String e2eIpAddress;
	
	private static ArrayList<AlarmZone> alarmZones = new ArrayList<AlarmZone>();
	
	public static boolean loadConfig() {
		String path = "@TemperatureAlarmMailer@config.yml";
		path = path.replace("@", File.separator);
		config = new YamlFile(path);
		
		try {
			if (config.exists()) {
				System.out.println("File already exists, loading configurations...\nFile located at: " + config.getFilePath() + "\n");
				config.load(); // Loads the entire file
			} else {
				System.out.println("Copying default config file: " + config.getFilePath() + "\n");
				copy(ConfigReader.class.getResourceAsStream("/default_files/config.yml"), path);
				config.load();
			}
		} catch(FileNotFoundException e) {		
			e.printStackTrace();
			System.out.println("Couldn't load the config file. FileNotFound");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		deviceName = config.getString("device.name");
		deviceLocation = config.getString("device.location");
		
		emailUsername = config.getString("email.username");
		emailPassword = config.getString("email.password");
		emailAddress = config.getString("email.address");
		emailServerHost = config.getString("email.server_host");
		emailPortNumber = config.getInt("email.port");
		emailHighTempAlarmHtml = config.getString("email.high_temp_alarm_html");
		emailLowTempAlarmHtml = config.getString("email.low_temp_alarm_html");
		
		globalReceiverAddresses = (ArrayList<String>) config.getStringList("global_receiver_addresses");
		
		e2eIpAddress = config.getString("supported_controllers.e2e.ip");
		
		ConfigurationSection az = config.getConfigurationSection("alarm_zones");
		
		if (az != null) {
			for (String key : az.getKeys(false)) {				
				AlarmZone zone = new AlarmZone();
				zone.setName(config.getString("alarm_zones." + key + ".zone_name"));
				zone.setControllerType(config.getString("alarm_zones." + key + ".controller_type"));
				zone.setControllerPointer(config.getString("alarm_zones." + key + ".controller_pointer"));
				zone.setUseGlobalReceiverAddresses(config.getBoolean("alarm_zones." + key + ".use_global_receiver_addresses"));
				zone.setReceiverAddresses((ArrayList<String>) config.getStringList("alarm_zones." + key + ".receiver_addresses"));
				zone.setHighTempAlarm(config.getDouble("alarm_zones." + key + ".alarms.high_temp"));
				zone.setHighTempAlarmDelay(config.getInt("alarm_zones." + key + ".alarms.high_temp_delay"));
				zone.setLowTempAlarm(config.getDouble("alarm_zones." + key + ".alarms.low_temp"));
				zone.setLowTempAlarmDelay(config.getInt("alarm_zones." + key + ".alarms.low_temp_delay"));
				System.out.println(zone.toString());
				alarmZones.add(zone);
			}
		}
		return true;
	}
	
   /**
     * Copy a file from source to destination.
     *
     * @param source
     *        the source
     * @param destination
     *        the destination
     * @return True if succeeded , False if not
     */
    public static boolean copy(InputStream source , String destination) {
        boolean succeess = true;

        System.out.println("Copying ->" + source + "\n\tto ->" + destination);       		
        
        try {
        	 File file = new File(destination);
        	 if (file.exists()) {
        		 // Nothing
        	 } else {
        		 file.mkdirs();
        		 file.createNewFile();
        	 }
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
        	System.out.println("Failed to copy default config file.");
        	ex.printStackTrace();
          succeess = false;
        }

        return succeess;

    }

	public static YamlFile getConfig() {
		return config;
	}

	public static void setConfig(YamlFile config) {
		ConfigReader.config = config;
	}

	public static String getDeviceName() {
		return deviceName;
	}

	public static void setDeviceName(String deviceName) {
		ConfigReader.deviceName = deviceName;
	}

	public static String getDeviceLocation() {
		return deviceLocation;
	}

	public static void setDeviceLocation(String deviceLocation) {
		ConfigReader.deviceLocation = deviceLocation;
	}

	public static String getEmailUsername() {
		return emailUsername;
	}

	public static void setEmailUsername(String emailUsername) {
		ConfigReader.emailUsername = emailUsername;
	}

	public static String getEmailPassword() {
		return emailPassword;
	}

	public static void setEmailPassword(String emailPassword) {
		ConfigReader.emailPassword = emailPassword;
	}

	public static String getEmailAddress() {
		return emailAddress;
	}

	public static void setEmailAddress(String emailAddress) {
		ConfigReader.emailAddress = emailAddress;
	}

	public static String getEmailServerHost() {
		return emailServerHost;
	}

	public static void setEmailServerHost(String emailServerHost) {
		ConfigReader.emailServerHost = emailServerHost;
	}

	public static int getEmailPortNumber() {
		return emailPortNumber;
	}

	public static void setEmailPortNumber(int emailPortNumber) {
		ConfigReader.emailPortNumber = emailPortNumber;
	}

	public static ArrayList<String> getGlobalReceiverAddresses() {
		return globalReceiverAddresses;
	}

	public static void setGlobalReceiverAddresses(ArrayList<String> globalReceiverAddresses) {
		ConfigReader.globalReceiverAddresses = globalReceiverAddresses;
	}

	public static String getE2eIpAddress() {
		return e2eIpAddress;
	}

	public static void setE2eIpAddress(String e2eIpAddress) {
		ConfigReader.e2eIpAddress = e2eIpAddress;
	}

	public static ArrayList<AlarmZone> getAlarmZones() {
		return alarmZones;
	}

	public static void setAlarmZones(ArrayList<AlarmZone> alarmZones) {
		ConfigReader.alarmZones = alarmZones;
	}

	public static String getEmailHighTempAlarmHtml() {
		return emailHighTempAlarmHtml;
	}

	public static String getEmailLowTempAlarmHtml() {
		return emailLowTempAlarmHtml;
	}    	
	
	
}
