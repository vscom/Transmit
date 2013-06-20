package com.bvcom.transmit.vo.video;

import java.util.List;

import com.bvcom.transmit.vo.MSGHeadVO;
//自动轮播
public class StreamRoundInfoQueryVO {

	private MSGHeadVO MSGHead = new MSGHeadVO();

    private int Index;
    
    private String RoundTime;
    
    private int Switch;
    
    private List<Integer> Freq;
    
    private List<Integer> SymbolRate;

    private List<String> QAM;

    private List<Integer> ServiceID;

    private List<Integer> VideoPID;

    private List<Integer> AudioPID;

    private int ReutnValue;
    
    private String ReturnURL;
    
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

	public String getRoundTime() {
		return RoundTime;
	}

	public void setRoundTime(String roundTime) {
		RoundTime = roundTime;
	}

	public int getSwitch() {
		return Switch;
	}

	public void setSwitch(int switch1) {
		Switch = switch1;
	}

	public List<Integer> getFreq() {
		return Freq;
	}

	public void setFreq(List<Integer> freq) {
		Freq = freq;
	}

	public List<Integer> getSymbolRate() {
		return SymbolRate;
	}

	public void setSymbolRate(List<Integer> symbolRate) {
		SymbolRate = symbolRate;
	}

	public List<String> getQAM() {
		return QAM;
	}

	public void setQAM(List<String> qam) {
		QAM = qam;
	}

	public List<Integer> getServiceID() {
		return ServiceID;
	}

	public void setServiceID(List<Integer> serviceID) {
		ServiceID = serviceID;
	}

	public List<Integer> getVideoPID() {
		return VideoPID;
	}

	public void setVideoPID(List<Integer> videoPID) {
		VideoPID = videoPID;
	}

	public List<Integer> getAudioPID() {
		return AudioPID;
	}

	public void setAudioPID(List<Integer> audioPID) {
		AudioPID = audioPID;
	}

	public int getReutnValue() {
		return ReutnValue;
	}

	public void setReutnValue(int reutnValue) {
		ReutnValue = reutnValue;
	}

	public String getReturnURL() {
		return ReturnURL;
	}

	public void setReturnURL(String returnURL) {
		ReturnURL = returnURL;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}
}
