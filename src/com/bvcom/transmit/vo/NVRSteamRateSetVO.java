package com.bvcom.transmit.vo;
//实时视频流率
public class NVRSteamRateSetVO {

	private MSGHeadVO MSGHead = new MSGHeadVO();

    private int Index;
    
    private int Width;
    
    private int Height;
    
    private int Fps;
    
    private int Bps;

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
}
