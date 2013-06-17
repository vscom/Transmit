package com.bvcom.transmit.vo.video;

import com.bvcom.transmit.vo.MSGHeadVO;

public class ChangeProgramQueryVO {

    private MSGHeadVO MSGHead = new MSGHeadVO();

    private int RunTime;
    private int Index;
    
    private int Freq;
    
    private int SymbolRate;

    private String QAM;

    private int ServiceID;

    private int VideoPID;

    private int AudioPID;

    private int ReutnValue;
    
    private String ReturnURL;
    
    private String Comment;
    
    
    //v2.5协议新增
    //CodingFormat="cbr" Width="960" Height="544" Fps="25" Bps="1500000"
    
    private String CodingFormat;
    
    private int width;
    
    private int height;
    
    private int fps;
    
    private int bps;
    

    public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public int getAudioPID() {
        return AudioPID;
    }

    public void setAudioPID(int audioPID) {
        AudioPID = audioPID;
    }

    public int getFreq() {
        return Freq;
    }

    public void setFreq(int freq) {
        Freq = freq;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public MSGHeadVO getMSGHead() {
        return MSGHead;
    }

    public void setMSGHead(MSGHeadVO head) {
        MSGHead = head;
    }

    public String getQAM() {
        return QAM;
    }

    public void setQAM(String qam) {
        QAM = qam;
    }

    public String getReturnURL() {
        return ReturnURL;
    }

    public void setReturnURL(String returnURL) {
        ReturnURL = returnURL;
    }

    public int getReutnValue() {
        return ReutnValue;
    }

    public void setReutnValue(int reutnValue) {
        ReutnValue = reutnValue;
    }

    public int getServiceID() {
        return ServiceID;
    }

    public void setServiceID(int serviceID) {
        ServiceID = serviceID;
    }

    public int getSymbolRate() {
        return SymbolRate;
    }

    public void setSymbolRate(int symbolRate) {
        SymbolRate = symbolRate;
    }

    public int getVideoPID() {
        return VideoPID;
    }

    public void setVideoPID(int videoPID) {
        VideoPID = videoPID;
    }

	public String getCodingFormat() {
		return CodingFormat;
	}

	public void setCodingFormat(String codingFormat) {
		CodingFormat = codingFormat;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getBps() {
		return bps;
	}

	public void setBps(int bps) {
		this.bps = bps;
	}
    
	public int getRunTime() {
		return RunTime;
	}

	public void setRunTime(int runTime) {
		RunTime = runTime;
	}
    
}
