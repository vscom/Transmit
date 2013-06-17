package com.bvcom.transmit.vo.si;

import com.bvcom.transmit.vo.MSGHeadVO;

//频谱扫描
public class SpectrumScanQueryVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private String ScanTime;
	
    private int SymbolRate;

	private String QAM;
	
	private String StartFreq;
	
	private String EndFreq;
	
	private String StepFreq;
	
	private int Freq;//一个频点对应一个level（返回参数）
	
	private String level; 
	
	private int ReuturnValue;
    
    private String Comment;//失败原因

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public String getScanTime() {
		return ScanTime;
	}

	public void setScanTime(String scanTime) {
		ScanTime = scanTime;
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

	public String getStartFreq() {
		return StartFreq;
	}

	public void setStartFreq(String startFreq) {
		StartFreq = startFreq;
	}

	public String getEndFreq() {
		return EndFreq;
	}

	public void setEndFreq(String endFreq) {
		EndFreq = endFreq;
	}

	public String getStepFreq() {
		return StepFreq;
	}

	public void setStepFreq(String stepFreq) {
		StepFreq = stepFreq;
	}

	public int getFreq() {
		return Freq;
	}

	public void setFreq(int freq) {
		Freq = freq;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
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
