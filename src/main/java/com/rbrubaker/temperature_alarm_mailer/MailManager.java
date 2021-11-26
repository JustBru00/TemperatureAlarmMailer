package com.rbrubaker.temperature_alarm_mailer;

import java.util.ArrayList;

import javax.mail.Message.RecipientType;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;

import com.rbrubaker.e2e4j.beans.ExpandedStatus;

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
public class MailManager {
	
	public static void sendHighTempAlarm(AlarmZone zone, ExpandedStatus status) {
		String mailHtml = ConfigReader.getEmailHighTempAlarmHtml();
		mailHtml = mailHtml.replace("{CURRENT_TIME}", TemperatureAlarmMailer.getLogTimeStamp())
				.replace("{ZONE_NAME}", zone.getName())
				.replace("{ZONE_CURRENT_TEMPERATURE}", status.getValue())
				.replace("{ZONE_HIGH_ALARM}", String.valueOf(zone.getHighTempAlarm()))
				.replace("{ZONE_HIGH_ALARM_DELAY}", String.valueOf(zone.getHighTempAlarmDelay()));
		
		if (zone.isUseGlobalReceiverAddresses()) {
			// Use the global address list
			send("[HIGH TEMP ALARM] " + zone.getName(), ConfigReader.getGlobalReceiverAddresses(), mailHtml);
		} else {
			// Use the list for this zone specifically
			send("[HIGH TEMP ALARM] " + zone.getName(), zone.getReceiverAddresses(), mailHtml);
		}
	}
	
	public static void sendLowTempAlarm(AlarmZone zone, ExpandedStatus status) {
		String mailHtml = ConfigReader.getEmailLowTempAlarmHtml();
		mailHtml = mailHtml.replace("{CURRENT_TIME}", TemperatureAlarmMailer.getLogTimeStamp())
				.replace("{ZONE_NAME}", zone.getName())
				.replace("{ZONE_CURRENT_TEMPERATURE}", status.getValue())
				.replace("{ZONE_LOW_ALARM}", String.valueOf(zone.getLowTempAlarm()))
				.replace("{ZONE_LOW_ALARM_DELAY}", String.valueOf(zone.getLowTempAlarmDelay()));
		
		if (zone.isUseGlobalReceiverAddresses()) {
			// Use the global address list
			send("[LOW TEMP ALARM] " + zone.getName(), ConfigReader.getGlobalReceiverAddresses(), mailHtml);
		} else {
			// Use the list for this zone specifically
			send("[LOW TEMP ALARM] " + zone.getName(), zone.getReceiverAddresses(), mailHtml);
		}
	}

	/**
	 * This will block the thread until the mail is sent or it fails all 20 tries.
	 * @param subject
	 * @param addresses
	 * @param messageHtml
	 */
	public static void send(String subject, ArrayList<String> addresses, String messageHtml) {
		for (String toAddress : addresses) {
			sendEmailHTML(toAddress, subject, messageHtml);
		}
	}

	/**
	 * Attempts to send an email. Will retry 20 times before failing completely. Retries every 30 seconds.
	 * This will block the thread until the mail is sent or it fails all 20 tries.
	 * @param toEmailName
	 * @param toEmailAddress
	 * @param subject
	 * @param emailHTML
	 * @return
	 */
	public static boolean sendEmailHTML(String toEmailAddress, String subject, String emailHTML) {
		final String emailUserId = ConfigReader.getEmailUsername();
		final String password = ConfigReader.getEmailPassword();
		final String fromEmailId = ConfigReader.getEmailAddress();

		boolean mailFailed = true;
		int tries = 1;

		while (mailFailed && tries < 20) {
			Email email = new Email();
			email.setFromAddress("RBRAlarmMailer", fromEmailId);
			email.addRecipient("AlarmReceiver", toEmailAddress, RecipientType.TO);
			email.setTextHTML(emailHTML.replace("{DEVICE}", ConfigReader.getDeviceName()).replace("{LOCATION}", ConfigReader.getDeviceLocation())
					.replace("{VERSION}", TemperatureAlarmMailer.VERSION));
			email.setSubject(subject);

			try {
				new Mailer(ConfigReader.getEmailServerHost(), ConfigReader.getEmailPortNumber(), emailUserId, password,
						TransportStrategy.SMTP_TLS).sendMail(email);
				System.out.println("Sent " + emailHTML + " to " + toEmailAddress);
				mailFailed = false;				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Failed to send mail.");
				tries++;
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}

		return false;
	}

}
