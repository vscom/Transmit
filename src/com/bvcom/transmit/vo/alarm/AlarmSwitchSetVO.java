package com.bvcom.transmit.vo.alarm;

import java.util.List;

import com.bvcom.transmit.vo.MSGHeadVO;

//报警开关（频率）
public class AlarmSwitchSetVO {

	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Index;

    private int Freq;
    
    private List<Integer> type;
    
    private List<String> Desc;

    private List<Integer> Switch;
    
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

	public List<Integer> getSwitch() {
		return Switch;
	}

	public void setSwitch(List<Integer> switch1) {
		Switch = switch1;
	}

	public int getReturnValue() {
		return ReturnValue;
	}

	public void setReturnValue(int returnValue) {
		ReturnValue = returnValue;
	}
    
    
}
