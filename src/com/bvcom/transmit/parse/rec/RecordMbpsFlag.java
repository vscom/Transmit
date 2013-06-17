package com.bvcom.transmit.parse.rec;

import java.io.Serializable;

public class RecordMbpsFlag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	   //<RecordParamSetEx Freq="714000" ServiceID="101" Index="2" Width="352" Height="288" Fps="25" Bps="700000"/>
	private int Freq,ServiceID,Index,Width,Height,Fps,Bps;

	public int getFreq() {
		return Freq;
	}

	public void setFreq(int freq) {
		Freq = freq;
	}

	public int getServiceID() {
		return ServiceID;
	}

	public void setServiceID(int serviceID) {
		ServiceID = serviceID;
	}

	public int getIndex() {
		return Index;
	}

	public void setIndex(int index) {
		Index = index;
	}

	public int getWidth() {
		return Width;
	}

	public void setWidth(int width) {
		Width = width;
	}

	public int getHeight() {
		return Height;
	}

	public void setHeight(int height) {
		Height = height;
	}

	public int getFps() {
		return Fps;
	}

	public void setFps(int fps) {
		Fps = fps;
	}

	public int getBps() {
		return Bps;
	}

	public void setBps(int bps) {
		Bps = bps;
	}
	@Override
	public String toString() {
		return Freq+","+ServiceID+","+Index+","+Width+","+Height+","+Fps+","+Bps;
	}
}
