package com.bvcom.transmit.handle.alarm.domain;

import java.io.Serializable;

/**
 * 封装msgURl
 * @author JL
 *
 */
public class Temp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String freq;
	private String msgURL;
	public Temp(String freq, String msgURL) {
		super();
		this.freq = freq;
		this.msgURL = msgURL;
	}
	public String getFreq() {
		return freq;
	}
	public void setFreq(String freq) {
		this.freq = freq;
	}
	public String getMsgURL() {
		return msgURL;
	}
	public void setMsgURL(String msgURL) {
		this.msgURL = msgURL;
	}
	@Override
	public String toString() {
		return this.freq+","+this.msgURL;
	}
}
