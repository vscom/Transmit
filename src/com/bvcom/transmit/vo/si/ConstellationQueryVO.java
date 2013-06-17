package com.bvcom.transmit.vo.si;

import com.bvcom.transmit.vo.MSGHeadVO;

public class ConstellationQueryVO {
	
    private MSGHeadVO MSGHead = new MSGHeadVO();
    
    private int Freq;
    
    private String QAM;
    
    private int SymbolRate;
    
    private double valueI;
    
    private double valueQ;
    
    private int MER;

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public int getFreq() {
		return Freq;
	}

	public void setFreq(int freq) {
		Freq = freq;
	}

	public String getQAM() {
		return QAM;
	}

	public void setQAM(String qam) {
		QAM = qam;
	}

	public int getSymbolRate() {
		return SymbolRate;
	}

	public void setSymbolRate(int symbolRate) {
		SymbolRate = symbolRate;
	}

	public double getValueI() {
		return valueI;
	}

	public void setValueI(double valueI) {
		this.valueI = valueI;
	}

	public double getValueQ() {
		return valueQ;
	}

	public void setValueQ(double valueQ) {
		this.valueQ = valueQ;
	}

	public int getMER() {
		return MER;
	}

	public void setMER(int mer) {
		MER = mer;
	}
    
}
