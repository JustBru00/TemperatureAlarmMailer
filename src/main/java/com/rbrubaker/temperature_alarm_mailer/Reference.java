package com.rbrubaker.temperature_alarm_mailer;

public class Reference {

	public static final String HIGH_ALARM_EMAIL_HTML = "<h1><strong style=\"font-size: 14px;\"><img src=\"http://epicview.rbrsystem.com/images/E2_Alarm.gif\" alt=\"E2e Alarm Gif\" width=\"65\" height=\"20\" /></strong></h1>\r\n"
			+ "<h3><strong>A high temperature alarm has occurred at {CURRENT_TIME}.</strong></h3>\r\n"
			+ "<p>&nbsp;</p>\r\n"
			+ "<p>Alarm Details:</p>\r\n"
			+ "<p>Zone Name: {ZONE_NAME}</p>\r\n"
			+ "<p>Current Temperature: {ZONE_CURRENT_TEMPERATURE}</p>\r\n"
			+ "<p>Alarm Limit Exceeded: {ZONE_HIGH_ALARM}</p>\r\n"
			+ "<p>Alarm Delay: {ZONE_HIGH_ALARM_DELAY}</p>\r\n"
			+ "<p>&nbsp;</p>\r\n"
			+ "<p>Please investigate or call Rufus Brubaker Refrigeration.</p>\r\n"
			+ "<p>&nbsp;<br /><br /></p>\r\n"
			+ "<p>&nbsp;</p>\r\n"
			+ "<p>Rufus Brubaker Refrigeration</p>\r\n"
			+ "<p>Phone Toll Free: 1-866-665-3525</p>\r\n"
			+ "<p>Device: {DEVICE} Location: {LOCATION}</p>\r\n"
			+ "<p>Sent by TemperatureAlarmMailer {VERSION}</p>";
	public static final String LOW_ALARM_EMAIL_HTML = "<h1><strong style=\"font-size: 14px;\"><img src=\"http://epicview.rbrsystem.com/images/E2_Alarm.gif\" alt=\"E2e Alarm Gif\" width=\"65\" height=\"20\" /></strong></h1>\r\n"
			+ "<h3><strong>A low temperature alarm has occurred at {CURRENT_TIME}.</strong></h3>\r\n"
			+ "<p>&nbsp;</p>\r\n"
			+ "<p>Alarm Details:</p>\r\n"
			+ "<p>Zone Name: {ZONE_NAME}</p>\r\n"
			+ "<p>Current Temperature: {ZONE_CURRENT_TEMPERATURE}</p>\r\n"
			+ "<p>Alarm Limit Exceeded: {ZONE_LOW_ALARM}</p>\r\n"
			+ "<p>Alarm Delay: {ZONE_LOW_ALARM_DELAY}</p>\r\n"
			+ "<p>&nbsp;</p>\r\n"
			+ "<p>Please investigate or call Rufus Brubaker Refrigeration.</p>\r\n"
			+ "<p>&nbsp;<br /><br /></p>\r\n"
			+ "<p>&nbsp;</p>\r\n"
			+ "<p>Rufus Brubaker Refrigeration</p>\r\n"
			+ "<p>Phone Toll Free: 1-866-665-3525</p>\r\n"
			+ "<p>Device: {DEVICE} Location: {LOCATION}</p>\r\n"
			+ "<p>Sent by TemperatureAlarmMailer {VERSION}</p>";
	
}
