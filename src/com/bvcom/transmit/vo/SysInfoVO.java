package com.bvcom.transmit.vo;

public class SysInfoVO {
    
//    private String RealVideoURL;
	//前端名称
	private String AgentName;
	//前端类型
	private String AgentType;
	//中心到前端的URL
	private String CenterToAgentURL;
	//前端到中心的URL
	private String AgentToCenterURL;
	//TSGrab接收消息地址
	private String TSGrabURL;
	
    private String CenterAlarmURL;
    
    private String SrcCode;
    
    private String DstCode;
    
    private String receFilePath;
    
    private String sendFilePath;
    
    //BY TQY 监测中心四期  轮播上报给中心的URL地址:马赛克轮播路径
    private String CenterRoundChannelURL;
    
    //录像文件存放路径
    private String RecordFilePath;
    
    //日志文件存放路径
    private String LogFilePath;
    
    /**
     * 报警数据保存日志标记
     */
    private int IsAlarmLogEnable;
    
    private String alarmFilePath;
    
    private String SendErrorFilePath;

    private String MHPInfoFilePath;

    private String EPGInfoFilePath;

    private String PSIInfoFilePath;

    private String TomcatHome;

    private String TomcatPort;

    private String LocalRedirectIp;
    
    /**
     * 数据业务分析时间
     */
    private String AutoAnalysisStartTime;
    
//    private int MAXProgram;
    
    private int MaxAutoRecordNum;
    
    /**
     * EPG信息是否大于1M打包为XML
     */
    private int IsEPGZip = 1;
    
    /**
     * EPG信息是否从数据库取得 0:不从数据库取得数据 1:从数据库取得
     */
    private int IsEPGFromDataBase = 0;
    
    /**
     * IsHasAlarmID 是否存在AlarmID, 报警数据是否入库， 0:不入库 1:入库
     */
    private int IsHasAlarmID = 0;
    /**
     * 报警信息是否主动不报 0:不补报报警信息 1:补报报警信息
     */
    private int IsAutoAlarmReply = 1;
    
    /**
     * 允许 加大码率后录相的个数
     * JI LONG 2011-5-13
     */
    private int MaxRecordMbpsFlag=0;
    
    public int getMaxRecordMbpsFlag() {
		return MaxRecordMbpsFlag;
	}

	public void setMaxRecordMbpsFlag(int maxRecordMbpsFlag) {
		MaxRecordMbpsFlag = maxRecordMbpsFlag;
	}

	public String getCenterAlarmURL() {
        return CenterAlarmURL;
    }

    public void setCenterAlarmURL(String centerAlarmURL) {
        CenterAlarmURL = centerAlarmURL;
    }

    public String getDstCode() {
        return DstCode;
    }

    public void setDstCode(String dstCode) {
        DstCode = dstCode;
    }

//    public String getRealVideoURL() {
//        return RealVideoURL;
//    }
//
//    public void setRealVideoURL(String realVideoURL) {
//        RealVideoURL = realVideoURL;
//    }

    public String getReceFilePath() {
        return receFilePath;
    }

    public void setReceFilePath(String receFilePath) {
        this.receFilePath = receFilePath;
    }

    public String getSendFilePath() {
        return sendFilePath;
    }

    public void setSendFilePath(String sendFilePath) {
        this.sendFilePath = sendFilePath;
    }

    public String getSrcCode() {
        return SrcCode;
    }

    public void setSrcCode(String srcCode) {
        SrcCode = srcCode;
    }

    public String getEPGInfoFilePath() {
        return EPGInfoFilePath;
    }

    public void setEPGInfoFilePath(String infoFilePath) {
        EPGInfoFilePath = infoFilePath;
    }

    public String getLocalRedirectIp() {
        return LocalRedirectIp;
    }

    public void setLocalRedirectIp(String localRedirectIp) {
        LocalRedirectIp = localRedirectIp;
    }

    public String getMHPInfoFilePath() {
        return MHPInfoFilePath;
    }

    public void setMHPInfoFilePath(String infoFilePath) {
        MHPInfoFilePath = infoFilePath;
    }

    public String getPSIInfoFilePath() {
        return PSIInfoFilePath;
    }

    public void setPSIInfoFilePath(String infoFilePath) {
        PSIInfoFilePath = infoFilePath;
    }

    public String getTomcatHome() {
        return TomcatHome;
    }

    public void setTomcatHome(String tomcatHome) {
        TomcatHome = tomcatHome;
    }

    public String getTomcatPort() {
        return TomcatPort;
    }

    public void setTomcatPort(String tomcatPort) {
        TomcatPort = tomcatPort;
    }

//	public int getMAXProgram() {
//		return MAXProgram;
//	}
//
//	public void setMAXProgram(int program) {
//		MAXProgram = program;
//	}

	public int getMaxAutoRecordNum() {
		return MaxAutoRecordNum;
	}

	public void setMaxAutoRecordNum(int maxAutoRecordNum) {
		MaxAutoRecordNum = maxAutoRecordNum;
	}

	public String getAlarmFilePath() {
		return alarmFilePath;
	}

	public void setAlarmFilePath(String alarmFilePath) {
		this.alarmFilePath = alarmFilePath;
	}

	public String getSendErrorFilePath() {
		return SendErrorFilePath;
	}

	public void setSendErrorFilePath(String sendErrorFilePath) {
		SendErrorFilePath = sendErrorFilePath;
	}

	public int getIsEPGZip() {
		return IsEPGZip;
	}

	public void setIsEPGZip(int isEPGZip) {
		IsEPGZip = isEPGZip;
	}

	/**
	 * 报警信息是否主动不报 0:不补报报警信息 1:补报报警信息
	 * @return
	 */
	public int getIsAutoAlarmReply() {
		return IsAutoAlarmReply;
	}

	/**
	 * 报警信息是否主动不报 0:不补报报警信息 1:补报报警信息
	 * @param isAutoAlarmReply
	 */
	public void setIsAutoAlarmReply(int isAutoAlarmReply) {
		IsAutoAlarmReply = isAutoAlarmReply;
	}

    /**
     * 数据业务分析时间
     */
	public String getAutoAnalysisStartTime() {
		return AutoAnalysisStartTime;
	}

    /**
     * 数据业务分析时间
     */
	public void setAutoAnalysisStartTime(String autoAnalysisStartTime) {
		AutoAnalysisStartTime = autoAnalysisStartTime;
	}
	
    /**
     * 报警数据保存日志使能标记
     */
	public int getIsAlarmLogEnable() {
		return IsAlarmLogEnable;
	}
	
    /**
     * 报警数据保存日志使能标记
     */
	public void setIsAlarmLogEnable(int isAlarmLogEnable) {
		IsAlarmLogEnable = isAlarmLogEnable;
	}

    /**
     * IsHasAlarmID 是否存在AlarmID, 报警数据是否入库， 0:不入库 1:入库
     */
	public int getIsHasAlarmID() {
		return IsHasAlarmID;
	}
	
    /**
     * IsHasAlarmID 是否存在AlarmID, 报警数据是否入库， 0:不入库 1:入库
     */
	public void setIsHasAlarmID(int isHasAlarmID) {
		IsHasAlarmID = isHasAlarmID;
	}

    /**
     * EPG信息是否从数据库取得 0:不从数据库取得数据 1:从数据库取得
     */
	public int getIsEPGFromDataBase() {
		return IsEPGFromDataBase;
	}
    /**
     * EPG信息是否从数据库取得 0:不从数据库取得数据 1:从数据库取得
     */
	public void setIsEPGFromDataBase(int isEPGFromDataBase) {
		IsEPGFromDataBase = isEPGFromDataBase;
	}

	public String getTSGrabURL() {
		return TSGrabURL;
	}

	public void setTSGrabURL(String tSGrabURL) {
		TSGrabURL = tSGrabURL;
	}

	public String getAgentName() {
		return AgentName;
	}

	public void setAgentName(String agentName) {
		AgentName = agentName;
	}

	public String getAgentType() {
		return AgentType;
	}

	public void setAgentType(String agentType) {
		AgentType = agentType;
	}

	public String getCenterToAgentURL() {
		return CenterToAgentURL;
	}

	public void setCenterToAgentURL(String centerToAgentURL) {
		CenterToAgentURL = centerToAgentURL;
	}

	public String getAgentToCenterURL() {
		return AgentToCenterURL;
	}

	public void setAgentToCenterURL(String agentToCenterURL) {
		AgentToCenterURL = agentToCenterURL;
	}

	public String getCenterRoundChannelURL() {
		return CenterRoundChannelURL;
	}

	public void setCenterRoundChannelURL(String centerRoundChannelURL) {
		CenterRoundChannelURL = centerRoundChannelURL;
	}

	public String getRecordFilePath() {
		return RecordFilePath;
	}

	public void setRecordFilePath(String recordFilePath) {
		RecordFilePath = recordFilePath;
	}

	public String getLogFilePath() {
		return LogFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		LogFilePath = logFilePath;
	}

}
