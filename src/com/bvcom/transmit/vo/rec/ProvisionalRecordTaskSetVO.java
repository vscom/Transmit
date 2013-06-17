package com.bvcom.transmit.vo.rec;

import com.bvcom.transmit.vo.MSGHeadVO;
//任务录像设置
public class ProvisionalRecordTaskSetVO {

	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int devIndex;
	
	private String TaskID; 

    private String Action;
    
    private int Index;
    
    private int Width;
    
    private int Height;
    
    private int Fps;
    
    private int Bps;
    
    private int DayofWeek;
    
    private String StartTime;
    
    private String EndTime;
    
    private String StartDateTime;
    
    private String EndDateTime;
    
    private int ExpireDays;//截止日期
    
    private int Freq;

    private int SymbolRate;

    private String QAM;

    private int ServiceID;

    private int Pcr_PID;

    private int VideoPID;

    private int AudioPID;

    private String URL;
    
    private int ReutnValue;
    
    private String Comment;//失败原因
    
    private String lasttime;
    
    private String programname;//节目名

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public String getTaskID() {
		return TaskID;
	}

	public void setTaskID(String taskID) {
		TaskID = taskID;
	}

	public String getAction() {
		return Action;
	}

	public void setAction(String action) {
		Action = action;
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

	public int getDayofWeek() {
		return DayofWeek;
	}

	public void setDayofWeek(int dayofWeek) {
		DayofWeek = dayofWeek;
	}

	public String getStartTime() {
		return StartTime;
	}

	public void setStartTime(String startTime) {
		StartTime = startTime;
	}

	public String getEndTime() {
		return EndTime;
	}

	public void setEndTime(String endTime) {
		EndTime = endTime;
	}

	/**
	 * 截止日期
	 * @return
	 */
	public int getExpireDays() {
		return ExpireDays;
	}

	/**
	 * 截止日期
	 * @param expireDays
	 */
	public void setExpireDays(int expireDays) {
		ExpireDays = expireDays;
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

	public String getQAM() {
		return QAM;
	}

	public void setQAM(String qam) {
		QAM = qam;
	}

	public int getServiceID() {
		return ServiceID;
	}

	public void setServiceID(int serviceID) {
		ServiceID = serviceID;
	}

	public int getPcr_PID() {
		return Pcr_PID;
	}

	public void setPcr_PID(int pcr_PID) {
		Pcr_PID = pcr_PID;
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

	public int getReutnValue() {
		return ReutnValue;
	}

	public void setReutnValue(int reutnValue) {
		ReutnValue = reutnValue;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String url) {
		URL = url;
	}

	public int getDevIndex() {
		return devIndex;
	}

	public void setDevIndex(int devIndex) {
		this.devIndex = devIndex;
	}

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

	public String getStartDateTime() {
		return StartDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		StartDateTime = startDateTime;
	}

	public String getEndDateTime() {
		return EndDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		EndDateTime = endDateTime;
	}

	public String getProgramname() {
		return programname;
	}

	public void setProgramname(String programname) {
		this.programname = programname;
	}

	
}
