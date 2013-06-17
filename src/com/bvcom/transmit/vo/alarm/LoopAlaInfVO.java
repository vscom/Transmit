package com.bvcom.transmit.vo.alarm;

import java.util.List;

import com.bvcom.transmit.vo.MSGHeadVO;

//循切报警设置
public class LoopAlaInfVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private int Index;
	
	private int Switch;
	
	private int DelayTime;
	
	private List<Integer> Freq;
	
	private List<Integer> SymbolRate;
	
	private List<String> QAM;
	
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

	public int getSwitch() {
		return Switch;
	}

	public void setSwitch(int switch1) {
		Switch = switch1;
	}

	public int getDelayTime() {
		return DelayTime;
	}

	public void setDelayTime(int delayTime) {
		DelayTime = delayTime;
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

	public int getReturnValue() {
		return ReturnValue;
	}

	public void setReturnValue(int returnValue) {
		ReturnValue = returnValue;
	}
    
}
