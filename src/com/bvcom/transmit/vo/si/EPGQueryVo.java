package com.bvcom.transmit.vo.si;

import com.bvcom.transmit.vo.MSGHeadVO;

/**
 * 
 * @author Bian Jiang
 * @date 2011.03.29
 *
 */
public class EPGQueryVo {
	
    private MSGHeadVO MSGHead = new MSGHeadVO();

    private String ScanTime;
    
    private String Freq;
    
    private String ProgramID;
    
    private String Program;
    
    private String ProgramType;
    
    private String StartTime;
    
    private String ProgramLen;
    
    private String State;
    
    private String Encryption;

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

	public String getFreq() {
		return Freq;
	}

	public void setFreq(String freq) {
		Freq = freq;
	}

	public String getProgramID() {
		return ProgramID;
	}

	public void setProgramID(String programID) {
		ProgramID = programID;
	}

	public String getProgram() {
		return Program;
	}

	public void setProgram(String program) {
		Program = program;
	}

	public String getProgramType() {
		return ProgramType;
	}

	public void setProgramType(String programType) {
		ProgramType = programType;
	}

	public String getStartTime() {
		return StartTime;
	}

	public void setStartTime(String startTime) {
		StartTime = startTime;
	}

	public String getProgramLen() {
		return ProgramLen;
	}

	public void setProgramLen(String programLen) {
		ProgramLen = programLen;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getEncryption() {
		return Encryption;
	}

	public void setEncryption(String encryption) {
		Encryption = encryption;
	}
    
}
