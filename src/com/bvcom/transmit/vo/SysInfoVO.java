package com.bvcom.transmit.vo;

public class SysInfoVO {
    
//    private String RealVideoURL;
	//ǰ������
	private String AgentName;
	//ǰ������
	private String AgentType;
	//���ĵ�ǰ�˵�URL
	private String CenterToAgentURL;
	//ǰ�˵����ĵ�URL
	private String AgentToCenterURL;
	//TSGrab������Ϣ��ַ
	private String TSGrabURL;
	
    private String CenterAlarmURL;
    
    private String SrcCode;
    
    private String DstCode;
    
    private String receFilePath;
    
    private String sendFilePath;
    
    //BY TQY �����������  �ֲ��ϱ������ĵ�URL��ַ:�������ֲ�·��
    private String CenterRoundChannelURL;
    
    //¼���ļ����·��
    private String RecordFilePath;
    
    //��־�ļ����·��
    private String LogFilePath;
    
    /**
     * �������ݱ�����־���
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
     * ����ҵ�����ʱ��
     */
    private String AutoAnalysisStartTime;
    
//    private int MAXProgram;
    
    private int MaxAutoRecordNum;
    
    /**
     * EPG��Ϣ�Ƿ����1M���ΪXML
     */
    private int IsEPGZip = 1;
    
    /**
     * EPG��Ϣ�Ƿ�����ݿ�ȡ�� 0:�������ݿ�ȡ������ 1:�����ݿ�ȡ��
     */
    private int IsEPGFromDataBase = 0;
    
    /**
     * IsHasAlarmID �Ƿ����AlarmID, ���������Ƿ���⣬ 0:����� 1:���
     */
    private int IsHasAlarmID = 0;
    /**
     * ������Ϣ�Ƿ��������� 0:������������Ϣ 1:����������Ϣ
     */
    private int IsAutoAlarmReply = 1;
    
    /**
     * ���� �Ӵ����ʺ�¼��ĸ���
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
	 * ������Ϣ�Ƿ��������� 0:������������Ϣ 1:����������Ϣ
	 * @return
	 */
	public int getIsAutoAlarmReply() {
		return IsAutoAlarmReply;
	}

	/**
	 * ������Ϣ�Ƿ��������� 0:������������Ϣ 1:����������Ϣ
	 * @param isAutoAlarmReply
	 */
	public void setIsAutoAlarmReply(int isAutoAlarmReply) {
		IsAutoAlarmReply = isAutoAlarmReply;
	}

    /**
     * ����ҵ�����ʱ��
     */
	public String getAutoAnalysisStartTime() {
		return AutoAnalysisStartTime;
	}

    /**
     * ����ҵ�����ʱ��
     */
	public void setAutoAnalysisStartTime(String autoAnalysisStartTime) {
		AutoAnalysisStartTime = autoAnalysisStartTime;
	}
	
    /**
     * �������ݱ�����־ʹ�ܱ��
     */
	public int getIsAlarmLogEnable() {
		return IsAlarmLogEnable;
	}
	
    /**
     * �������ݱ�����־ʹ�ܱ��
     */
	public void setIsAlarmLogEnable(int isAlarmLogEnable) {
		IsAlarmLogEnable = isAlarmLogEnable;
	}

    /**
     * IsHasAlarmID �Ƿ����AlarmID, ���������Ƿ���⣬ 0:����� 1:���
     */
	public int getIsHasAlarmID() {
		return IsHasAlarmID;
	}
	
    /**
     * IsHasAlarmID �Ƿ����AlarmID, ���������Ƿ���⣬ 0:����� 1:���
     */
	public void setIsHasAlarmID(int isHasAlarmID) {
		IsHasAlarmID = isHasAlarmID;
	}

    /**
     * EPG��Ϣ�Ƿ�����ݿ�ȡ�� 0:�������ݿ�ȡ������ 1:�����ݿ�ȡ��
     */
	public int getIsEPGFromDataBase() {
		return IsEPGFromDataBase;
	}
    /**
     * EPG��Ϣ�Ƿ�����ݿ�ȡ�� 0:�������ݿ�ȡ������ 1:�����ݿ�ȡ��
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
