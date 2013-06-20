package com.bvcom.transmit.vo.si;

import java.util.ArrayList;
import java.util.List;

import com.bvcom.transmit.vo.MSGHeadVO;
//频道扫描
public class ChannelScanQueryVO {

    private MSGHeadVO MSGHead = new MSGHeadVO();
    
    private List elementryPIDList = new ArrayList();

    private String ScanTime;

    private int SymbolRate;

    private String QAM;

    private int Freq;

    private int OrgNetID;

    private int TsID;

    private String Program;

    private int ProgramID;

    private int ServiceID;

    private int Pcr_PID;

    private int VideoPID;

    private int AudioPID;
    
    private int Encrypt;
    
    private int  HDTV;
    
    //added by tqy 节目类型
    
    private String ServiceType;

    private int ReutnValue;
    
    private int ScanType;
    
    private boolean isNewProgram = false;
    
    private String Comment;

    public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public int getFreq() {
        return Freq;
    }

    public void setFreq(int freq) {
        Freq = freq;
    }

    public MSGHeadVO getMSGHead() {
        return MSGHead;
    }

    public void setMSGHead(MSGHeadVO head) {
        MSGHead = head;
    }

    public int getOrgNetID() {
        return OrgNetID;
    }

    public void setOrgNetID(int orgNetID) {
        OrgNetID = orgNetID;
    }

    public String getProgram() {
		return Program;
	}

	public void setProgram(String program) {
		Program = program;
	}

	public int getProgramID() {
		return ProgramID;
	}

	public void setProgramID(int programID) {
		ProgramID = programID;
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

	public String getQAM() {
        return QAM;
    }

    public void setQAM(String qam) {
        QAM = qam;
    }

    public int getReutnValue() {
        return ReutnValue;
    }

    public void setReutnValue(int reutnValue) {
        ReutnValue = reutnValue;
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

	public int getTsID() {
        return TsID;
    }

    public void setTsID(int tsID) {
        TsID = tsID;
    }

	public int getEncrypt() {
		return Encrypt;
	}

	public void setEncrypt(int encrypt) {
		Encrypt = encrypt;
	}

	public int getHDTV() {
		return HDTV;
	}

	public void setHDTV(int hdtv) {
		HDTV = hdtv;
	}

	public boolean getIsNewProgram() {
		return isNewProgram;
	}

	public void setIsNewProgram(boolean isNewProgram) {
		this.isNewProgram = isNewProgram;
	}

	public List getElementryPIDList() {
		return elementryPIDList;
	}

	public void addElementryPIDList(elementryPIDVO vo) {
		this.elementryPIDList.add(vo);
	}
	public void removeElementryPIDList(elementryPIDVO vo) {
		this.elementryPIDList.remove(vo);
	}

	/**
	 * ScanType=0为简单
	 * 当ScanType=1时为详细扫描
	 * @return
	 */
	public int getScanType() {
		return ScanType;
	}
	
	
	//added by tqy
	public String getServiceType() {
		return ServiceType;
	}

	public void setServiceType(String serviceType) {
		ServiceType = serviceType;
	}

	/**
	 * ScanType=0为简单
	 * 当ScanType=1时为详细扫描
	 * @return
	 */
	public void setScanType(int scanType) {
		ScanType = scanType;
	}

	
}
