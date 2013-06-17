package com.bvcom.transmit.vo;

public class OSDSetVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();

    private int Index;
    
    private String InfoOSD;
    
    private int InfoOSDX;
    
    private int InfoOSDY;
    
    private int TimeOSDType;
    
    private int TimeOSDX;
    
    private int TimeOSDY;

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public int getIndex() {
		return Index;
	}

	public void setIndex(int index) {
		Index = index;
	}

	public String getInfoOSD() {
		return InfoOSD;
	}

	public void setInfoOSD(String infoOSD) {
		InfoOSD = infoOSD;
	}

	public int getInfoOSDX() {
		return InfoOSDX;
	}

	public void setInfoOSDX(int infoOSDX) {
		InfoOSDX = infoOSDX;
	}

	public int getInfoOSDY() {
		return InfoOSDY;
	}

	public void setInfoOSDY(int infoOSDY) {
		InfoOSDY = infoOSDY;
	}

	public int getTimeOSDType() {
		return TimeOSDType;
	}

	public void setTimeOSDType(int timeOSDType) {
		TimeOSDType = timeOSDType;
	}

	public int getTimeOSDX() {
		return TimeOSDX;
	}

	public void setTimeOSDX(int timeOSDX) {
		TimeOSDX = timeOSDX;
	}

	public int getTimeOSDY() {
		return TimeOSDY;
	}

	public void setTimeOSDY(int timeOSDY) {
		TimeOSDY = timeOSDY;
	}
}
