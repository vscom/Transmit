package com.bvcom.transmit.vo.alarm;

import java.util.List;

import com.bvcom.transmit.vo.MSGHeadVO;

//Ñ­ÇÐ±¨¾¯²éÑ¯
public class AlarmSearchLSetVO {

	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Index;
	
	private int DelayTime;
	private int AlaType;
	private int Freq;
	
	private List<Integer> type;
    private List<String> Desc;
    private List<Integer> Value;
    private List<String> Time;
    
    private int ReturnValue;

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

	public int getDelayTime() {
		return DelayTime;
	}

	public void setDelayTime(int delayTime) {
		DelayTime = delayTime;
	}

	public int getAlaType() {
		return AlaType;
	}

	public void setAlaType(int alaType) {
		AlaType = alaType;
	}

	public int getFreq() {
		return Freq;
	}

	public void setFreq(int freq) {
		Freq = freq;
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

	public List<Integer> getValue() {
		return Value;
	}

	public void setValue(List<Integer> value) {
		Value = value;
	}

	public List<String> getTime() {
		return Time;
	}

	public void setTime(List<String> time) {
		Time = time;
	}

	public int getReturnValue() {
		return ReturnValue;
	}

	public void setReturnValue(int returnValue) {
		ReturnValue = returnValue;
	}
}
