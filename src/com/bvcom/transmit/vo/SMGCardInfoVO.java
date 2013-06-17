/**
 * transmit (javaת��)
 * 
 * SMGInfoVO.java    2009.11.12
 * 
 * Copyright 2009 BVCOM. All Rights Reserved.
 * 
 */
package com.bvcom.transmit.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *  SMG �豸���忨����Ҫ�������ļ���Ϣ
 * 
 * @version  V1.0
 * @author Bian Jiang
 * @Date 2009.11.12
 * @update 2010.05.25 ���ӶԸ���ת���֧��
 */
public class SMGCardInfoVO {
    
    private int Index;
    
    private String IP;
    
    private String URL;
    
    private int HDFlag;
    
    private String HDURL;
    
    private String IndexType;
    
    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public String getIndexType() {
        return IndexType;
    }

    public void setIndexType(String indexType) {
        IndexType = indexType;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String ip) {
        IP = ip;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        URL = url;
    }
    
    public int getHDFlag() {
		return HDFlag;
	}

	public void setHDFlag(int flag) {
		HDFlag = flag;
	}

	public String getHDURL() {
		return HDURL;
	}

	public void setHDURL(String hdurl) {
		HDURL = hdurl;
	}

	public void printSMGInfoList() {
        System.out.println("--------------- SMGInfo List Start----------------");
        System.out.println("Index: " + Index);
        System.out.println("IndexType: " + IndexType);
        System.out.println("IP: " + IP);
        System.out.println("URL: " + URL);
        System.out.println("--------------- SMGInfo List End----------------");
    }
	
	//ͨ���忨��ϸ��Ϣ
	//С��������Ϣ
	private String smgCamCard;
	
	private String smgCamPostion;
	
	private String smgCamDesc;

	public String getSmgCamCard() {
		return smgCamCard;
	}

	public void setSmgCamCard(String smgCamCard) {
		this.smgCamCard = smgCamCard;
	}

	public String getSmgCamPostion() {
		return smgCamPostion;
	}

	public void setSmgCamPostion(String smgCamPostion) {
		this.smgCamPostion = smgCamPostion;
	}

	public String getSmgCamDesc() {
		return smgCamDesc;
	}

	public void setSmgCamDesc(String smgCamDesc) {
		this.smgCamDesc = smgCamDesc;
	}
	
	
	//ͨ���忨�Ľ�Ŀӳ����Ϣ���Զ�¼�ơ�����¼��
	//Status="0" IndexType=��4�� Desc="�Զ�¼�� CCTV-3 ��CCTV-4"
	//<Channel Program="CCTV-3 " ProgramID="457" Freq = "45700"  ServiceID="457" /> 
	//<Channel TaskID= " 100" />
	//ͨ��״̬
	private int Status;

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}
	
	//�Զ�¼�ƽ�Ŀ����
	private String Desc;

	@SuppressWarnings("unchecked")
	public List getAutorecordList() {
		return autorecordList;
	}

	@SuppressWarnings("unchecked")
	public void setAutorecordList(List autorecordList) {
		this.autorecordList = autorecordList;
	}

	@SuppressWarnings("unchecked")
	public List getTaskrecordList() {
		return taskrecordList;
	}

	@SuppressWarnings("unchecked")
	public void setTaskrecordList(List taskrecordList) {
		this.taskrecordList = taskrecordList;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}
	//�Զ�¼���Ŀ��Ϣ
    @SuppressWarnings("unchecked")
	private List autorecordList = new ArrayList();
    //����¼���Ŀ
    @SuppressWarnings("unchecked")
	private List taskrecordList = new ArrayList();
    
    @SuppressWarnings("unchecked")
	private List realvideoList = new ArrayList();

	@SuppressWarnings("unchecked")
	public List getRealvideoList() {
		return realvideoList;
	}

	@SuppressWarnings("unchecked")
	public void setRealvideoList(List realvideoList) {
		this.realvideoList = realvideoList;
	}
}

