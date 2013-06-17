package com.bvcom.transmit.vo.video;

import com.bvcom.transmit.vo.MSGHeadVO;

//运行图
public class AlarmTimeSetVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Index; 
	
	private int Freq;
    
    private int SymbolRate;

    private String QAM;

    private int ServiceID;

    private int VideoPID;

    private int AudioPID;
    

    private int Month;
    
    private int Day;
    
    private String MonthStartTime;
    
    private String MonthEndTime;
    
    private int MonthType;
    
    private String MonthAlarmEndTime;
    
    
    private int DayofWeek;
    
    private String WeekStartTime;
    
    private String WeekEndTime;
    
    private int WeekType;
    
    private String WeekAlarmEndTime;
    
    
    private String StartDateTime;
    
    private String EndDateTime;
    
    private int DayType;
    
    private String DayAlarmEndTime;
    
    private int ReuturnValue;
    
    private String Comment;//失败原因

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

	public int getMonth() {
		return Month;
	}

	public void setMonth(int month) {
		Month = month;
	}

	public int getDay() {
		return Day;
	}

	public void setDay(int day) {
		Day = day;
	}

	public String getMonthStartTime() {
		return MonthStartTime;
	}

	public void setMonthStartTime(String monthStartTime) {
		MonthStartTime = monthStartTime;
	}

	public String getMonthEndTime() {
		return MonthEndTime;
	}

	public void setMonthEndTime(String monthEndTime) {
		MonthEndTime = monthEndTime;
	}

	public int getMonthType() {
		return MonthType;
	}

	public void setMonthType(int monthType) {
		MonthType = monthType;
	}

	public String getMonthAlarmEndTime() {
		return MonthAlarmEndTime;
	}

	public void setMonthAlarmEndTime(String monthAlarmEndTime) {
		MonthAlarmEndTime = monthAlarmEndTime;
	}

	public int getDayofWeek() {
		return DayofWeek;
	}

	public void setDayofWeek(int dayofWeek) {
		DayofWeek = dayofWeek;
	}

	public String getWeekStartTime() {
		return WeekStartTime;
	}

	public void setWeekStartTime(String weekStartTime) {
		WeekStartTime = weekStartTime;
	}

	public String getWeekEndTime() {
		return WeekEndTime;
	}

	public void setWeekEndTime(String weekEndTime) {
		WeekEndTime = weekEndTime;
	}

	public int getWeekType() {
		return WeekType;
	}

	public void setWeekType(int weekType) {
		WeekType = weekType;
	}

	public String getWeekAlarmEndTime() {
		return WeekAlarmEndTime;
	}

	public void setWeekAlarmEndTime(String weekAlarmEndTime) {
		WeekAlarmEndTime = weekAlarmEndTime;
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

	public int getDayType() {
		return DayType;
	}

	public void setDayType(int dayType) {
		DayType = dayType;
	}

	public String getDayAlarmEndTime() {
		return DayAlarmEndTime;
	}

	public void setDayAlarmEndTime(String dayAlarmEndTime) {
		DayAlarmEndTime = dayAlarmEndTime;
	}

	public int getReuturnValue() {
		return ReuturnValue;
	}

	public void setReuturnValue(int reuturnValue) {
		ReuturnValue = reuturnValue;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

}
