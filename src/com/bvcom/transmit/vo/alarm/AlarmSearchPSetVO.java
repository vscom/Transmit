package com.bvcom.transmit.vo.alarm;

import com.bvcom.transmit.vo.MSGHeadVO;

//报警上报（节目相关）
public class AlarmSearchPSetVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();
	
	private String AlarmType = "";
	
	private int Index = 0;

    private int Freq = 0;
    
    private int ServiceID = 0;
    
    private int VideoPID = 0;
    
    private int AudioPID = 0;
    
    /**
     * 广州项目 报警ID
     */
    private String AlarmID = "";
    
    private int ReturnValue = 0;
    
    private String Comment = "";//失败原因

    private int Type = 0;
    
    private String Desc = "";
    
    /**
     * 0:没有发生. 1:正在发生. 2:恢复了.
     */
    private int Value = 0;
    
    private String Time = "";
    
    
    public int getAudioPID() {
        return AudioPID;
    }

    public void setAudioPID(int audioPID) {
        AudioPID = audioPID;
    }

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

    public int getReturnValue() {
        return ReturnValue;
    }

    public void setReturnValue(int returnValue) {
        ReturnValue = returnValue;
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

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}
    /**
     * 0:没有发生. 1:正在发生. 2:恢复了.
     */
	public int getValue() {
		return Value;
	}
    /**
     * 0:没有发生. 1:正在发生. 2:恢复了.
     */
	public void setValue(int value) {
		Value = value;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getAlarmType() {
		return AlarmType;
	}

	public void setAlarmType(String alarmType) {
		AlarmType = alarmType;
	}

    /**
     * 广州项目 报警ID
     */
	public String getAlarmID() {
		return AlarmID;
	}

    /**
     * 广州项目 报警ID
     */
	public void setAlarmID(String alarmID) {
		AlarmID = alarmID;
	}
	
}
