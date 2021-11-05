package com.rbrubaker.temperature_alarm_mailer;

import java.time.Instant;
import java.util.ArrayList;

public class AlarmZone {

	private String name;
	private String controllerType;
	private String controllerPointer;
	private boolean useGlobalReceiverAddresses;
	
	private ArrayList<String> receiverAddresses = new ArrayList<String>();
	
	private double highTempAlarm;
	private int highTempAlarmDelay;
	private Instant highTempAlarmTripStart;	
	
	private double lowTempAlarm;
	private int lowTempAlarmDelay;
	private Instant lowTempAlarmTripStart;	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getControllerType() {
		return controllerType;
	}
	
	public void setControllerType(String controllerType) {
		this.controllerType = controllerType;
	}
	
	public String getControllerPointer() {
		return controllerPointer;
	}
	
	public void setControllerPointer(String controllerPointer) {
		this.controllerPointer = controllerPointer;
	}
	
	public boolean isUseGlobalReceiverAddresses() {
		return useGlobalReceiverAddresses;
	}
	
	public void setUseGlobalReceiverAddresses(boolean useGlobalReceiverAddresses) {
		this.useGlobalReceiverAddresses = useGlobalReceiverAddresses;
	}
	
	public ArrayList<String> getReceiverAddresses() {
		return receiverAddresses;
	}
	
	public void setReceiverAddresses(ArrayList<String> receiverAddresses) {
		this.receiverAddresses = receiverAddresses;
	}
	
	public double getHighTempAlarm() {
		return highTempAlarm;
	}
	
	public void setHighTempAlarm(double highTempAlarm) {
		this.highTempAlarm = highTempAlarm;
	}
	
	public int getHighTempAlarmDelay() {
		return highTempAlarmDelay;
	}
	
	public void setHighTempAlarmDelay(int highTempAlarmDelay) {
		this.highTempAlarmDelay = highTempAlarmDelay;
	}
	
	public double getLowTempAlarm() {
		return lowTempAlarm;
	}
	
	public void setLowTempAlarm(double lowTempAlarm) {
		this.lowTempAlarm = lowTempAlarm;
	}
	
	public int getLowTempAlarmDelay() {
		return lowTempAlarmDelay;
	}
	
	public void setLowTempAlarmDelay(int lowTempAlarmDelay) {
		this.lowTempAlarmDelay = lowTempAlarmDelay;
	}

	public Instant getHighTempAlarmTripStart() {
		return highTempAlarmTripStart;
	}

	public void setHighTempAlarmTripStart(Instant highTempAlarmTripStart) {
		this.highTempAlarmTripStart = highTempAlarmTripStart;
	}

	public Instant getLowTempAlarmTripStart() {
		return lowTempAlarmTripStart;
	}

	public void setLowTempAlarmTripStart(Instant lowTempAlarmTripStart) {
		this.lowTempAlarmTripStart = lowTempAlarmTripStart;
	}

	@Override
	public String toString() {
		return "AlarmZone [name=" + name + ", controllerType=" + controllerType + ", controllerPointer="
				+ controllerPointer + ", useGlobalReceiverAddresses=" + useGlobalReceiverAddresses
				+ ", receiverAddresses=" + receiverAddresses + ", highTempAlarm=" + highTempAlarm
				+ ", highTempAlarmDelay=" + highTempAlarmDelay + ", highTempAlarmTripStart=" + highTempAlarmTripStart
				+ ", lowTempAlarm=" + lowTempAlarm + ", lowTempAlarmDelay=" + lowTempAlarmDelay
				+ ", lowTempAlarmTripStart=" + lowTempAlarmTripStart + "]";
	}	
	
	
}
