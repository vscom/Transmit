package com.bvcom.transmit.vo.video;

import com.bvcom.transmit.vo.MSGHeadVO;

public class MonitorProgramQueryVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int RunTime;
	private int Index;
	
	private int Freq;
	
	private int SymbolRate;
	
	private int QAM;
	
	private int ServiceID;
	
	private String ProgramName;
	
	private int VideoPID;
	
	private int AudioPID;
	
	private int rtvsIndex;
	
//	private String rtvsURL;
	
	private String rtvsIP;
	
	private int rtvsPort;
	
	private String smgURL;
	
	private int smgIndex;

	private int statusFlag;
	
	private String RTVSResetURL;
	
	private int patrolGroupIndex;
	
	private int isReStartRTVS = 1;
	
	
	 //v2.5协议新增
    //CodingFormat="cbr" Width="960" Height="544" Fps="25" Bps="1500000"
    
    private String CodingFormat;
    
    private int width;
    
    private int height;
    
    private int fps;
    
    private int bps;
    
    
	
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

	public int getFreq() {
		return Freq;
	}

	public void setFreq(int freq) {
		Freq = freq;
	}

	public int getSymbolRate() {
		return SymbolRate;
	}

	public void setSymbolRate(int symbolRate) {
		SymbolRate = symbolRate;
	}

	public int getQAM() {
		return QAM;
	}

	public void setQAM(int qam) {
		QAM = qam;
	}

	public int getServiceID() {
		return ServiceID;
	}

	public void setServiceID(int serviceID) {
		ServiceID = serviceID;
	}

	public int getVideoPID() {
		return VideoPID;
	}

	public void setVideoPID(int videoPID) {
		VideoPID = videoPID;
	}

	public int getAudioPID() {
		return AudioPID;
	}

	public void setAudioPID(int audioPID) {
		AudioPID = audioPID;
	}

	public int getRtvsIndex() {
		return rtvsIndex;
	}

	public void setRtvsIndex(int rtvsIndex) {
		this.rtvsIndex = rtvsIndex;
	}

//	public String getRtvsURL() {
//		return rtvsURL;
//	}
//
//	public void setRtvsURL(String rtvsURL) {
//		this.rtvsURL = rtvsURL;
//	}

	public String getRtvsIP() {
		return rtvsIP;
	}

	public void setRtvsIP(String rtvsIP) {
		this.rtvsIP = rtvsIP;
	}

	public int getRtvsPort() {
		return rtvsPort;
	}

	public void setRtvsPort(int rtvsPort) {
		this.rtvsPort = rtvsPort;
	}

	public String getSmgURL() {
		return smgURL;
	}

	public void setSmgURL(String smgURL) {
		this.smgURL = smgURL;
	}

	public int getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(int statusFlag) {
		this.statusFlag = statusFlag;
	}

	public int getSmgIndex() {
		return smgIndex;
	}

	public void setSmgIndex(int smgIndex) {
		this.smgIndex = smgIndex;
	}

	public String getRTVSResetURL() {
		return RTVSResetURL;
	}

	public void setRTVSResetURL(String resetURL) {
		RTVSResetURL = resetURL;
	}

	public int getPatrolGroupIndex() {
		return patrolGroupIndex;
	}

	public void setPatrolGroupIndex(int patrolGroupIndex) {
		this.patrolGroupIndex = patrolGroupIndex;
	}

	/*
	 * 是否需求重启RTVS软件 1:需求 0:不需求
	 */
	public int getIsReStartRTVS() {
		return isReStartRTVS;
	}

	/*
	 * 是否需求重启RTVS软件 1:需求 0:不需求
	 */
	public void setIsReStartRTVS(int isReStartRTVS) {
		this.isReStartRTVS = isReStartRTVS;
	}

	public String getProgramName() {
		return ProgramName;
	}

	public void setProgramName(String programName) {
		ProgramName = programName;
	}

	public String getCodingFormat() {
		return CodingFormat;
	}

	public void setCodingFormat(String codingFormat) {
		CodingFormat = codingFormat;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getBps() {
		return bps;
	}

	public void setBps(int bps) {
		this.bps = bps;
	}
	public int getRunTime() {
		return RunTime;
	}

	public void setRunTime(int runTime) {
		RunTime = runTime;
	}
    
	
}
