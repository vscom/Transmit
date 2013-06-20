package com.bvcom.transmit.parse.alarm.domain;

import java.io.Serializable;

public class AlarmSwitch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String freq;
	private String serviceID;
	private int switchValue;
	private int switchType;
	private int alarmType;

	public String getFreq() {
		return freq;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public int getSwitchValue() {
		return switchValue;
	}

	public void setSwitchValue(int switchValue) {
		this.switchValue = switchValue;
	}

	public int getSwitchType() {
		return switchType;
	}

	public void setSwitchType(int switchType) {
		this.switchType = switchType;
	}

	public int getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}
	@Override
	public String toString() {
		return 	freq+","+serviceID+","+switchValue+","+switchType+","+alarmType;
	}
}
