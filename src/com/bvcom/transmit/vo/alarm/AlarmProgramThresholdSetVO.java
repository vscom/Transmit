package com.bvcom.transmit.vo.alarm;

import java.util.List;

import com.bvcom.transmit.vo.MSGHeadVO;

//报警门限（节目相关）
public class AlarmProgramThresholdSetVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Index;

    private int Freq;
    
    private int ServiceID;

    private int VideoPID;

    private int AudioPID;
    
    private List<Integer> type;
    
    private List<String> Desc;

    private List<Integer> Duration;
    
    private int ReturnValue;
    
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

	public List<Integer> getType() {
		return type;
	}

	public void setType(List<Integer> type) {
		this.type = type;
	}

	public List<String> getDesc() {
		return Desc;
	}

	public void setDesc(List<String> desc) {
		Desc = desc;
	}

	public List<Integer> getDuration() {
		return Duration;
	}

	public void setDuration(List<Integer> duration) {
		Duration = duration;
	}

	public int getReturnValue() {
		return ReturnValue;
	}

	public void setReturnValue(int returnValue) {
		ReturnValue = returnValue;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

}
