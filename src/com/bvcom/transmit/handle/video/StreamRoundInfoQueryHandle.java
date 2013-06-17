package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.video.RTVSResetConfigParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class StreamRoundInfoQueryHandle {
	
	private static Logger log = Logger.getLogger(StreamRoundInfoQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public StreamRoundInfoQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * �Զ��ֲ�
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		
		// ��������
		@SuppressWarnings("unused")
		String upString = "";
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("�Զ��ֲ�StringToXML Error: " + e.getMessage());
        }
        	
        MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ�
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 4);
		} catch (DaoException e1) {
			log.error("ȡ��ʵʱ��ƵURL����: " + e1.getMessage());
		}
		try {
            // �Զ��ֲ��·� timeout 1000*30 ��ʮ��
        	this.downString = this.downString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
        	this.downString = this.downString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
        	
            if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_5)) {
            	this.downString = this.downString.replaceAll("Index=\"0\"", "Index=\"" + rtvsVO.getSmgIndex() + "\"");
            	this.downString = this.downString.replaceAll("Version=\"2.5\"", "Version=\"2.4\"");
            }
            
            utilXML.SendDownNoneReturn(this.downString, rtvsVO.getSmgURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            // �ȴ�һ���ӣ���SMG���������ֹRTVS��û�нӵ�����������
        	Thread.sleep(CommonUtility.VIDEO_CHANGE_SLEEPTIME);
            //break;
        } catch (Exception e) {
            log.error("�·��Զ��ֲ���SMG����" + rtvsVO.getSmgURL());
            upString = "";
        }
		
        // RTVS�޸���������IP�Ͷ˿�
        RTVSResetConfigParse RTVSReset = new RTVSResetConfigParse();
        rtvsVO.setIndex(0);
        String rtvsString = RTVSReset.createForDownXML(bsData, rtvsVO);
        
        try {
        	upString = utilXML.SendDownXML(rtvsString, rtvsVO.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);
        } catch (CommonException e) {
            log.error("�·�RTVS�޸���������IP�Ͷ˿ڳ���" + rtvsVO.getRTVSResetURL());
        }
        
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysVO = coreData.getSysVO();
        String url = "";
        
        try {
            document = utilXML.StringToXML(upString);
            url = RTVSReset.getReturnURL(document);
        } catch (CommonException e) {
            log.error("��ƵURL StringToXML Error: " + e.getMessage());
        }
        
        if(url.equals("")) {
        	upString = ReturnXMLByURL(bsData, url, 1, rtvsVO.getPatrolGroupIndex());
        } else {
        	upString = ReturnXMLByURL(bsData, url, 0, rtvsVO.getPatrolGroupIndex());
        }
        
        
        //�ϱ��ظ���xml������,�Լ����سɹ�
        try {
        	if(upString == null || upString.equals("")) {
        		upString = utilXML.getReturnXML(bsData, 1);
        	}
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("�Զ��ֲ��ظ�ʧ��: " + e.getMessage());
        }
        
		Statement statement = null;
		Connection conn =null;
        StringBuffer strBuff = new StringBuffer();
		int runType = 0;
		
		if(rtvsVO.getIndex() > 0) {
			runType = 3;
		} else {
			runType = 2;
		}
		strBuff.append("update monitorprogramquery c set ");
		strBuff.append(" xml = '" + downString + "', ");
		// RunType 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
		strBuff.append(" RunType = " + runType + ", ");
		strBuff.append(" Freq = " + rtvsVO.getFreq() + ", ");
		strBuff.append(" ServiceID = " + rtvsVO.getServiceID() + ", ");
		strBuff.append(" rtvsIP = '" + rtvsVO.getRtvsIP() + "', ");
		strBuff.append(" rtvsPort = " + rtvsVO.getRtvsPort() + ", ");
		strBuff.append(" lastDatatime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where statusFlag = 4 ;");
		
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			statement.execute(strBuff.toString());
		} catch (Exception e) {
			log.error("ʵʱ��Ƶ�࿴ ����ʵʱ��Ƶ���״̬����: " + e.getMessage());
			log.error("����SQL: " + strBuff.toString());
		} finally {
			try {
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (Exception e) {
				log.error("�ر����ݿ���Դ����: " + e.getMessage());
			}
		}
        
        bsData = null;
        downString = null;
        utilXML = null;
	}

	// �ֶ�ѡ̨�ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, String url , int value, int channelIndex) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\"" + head.getCenterMsgID() +"\">";
		if(0==value){
			xml += "<Return Type=\"StreamRoundInfoQuery\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"StreamRoundInfoQuery\" Value=\"1\" Desc=\"ʧ��\"/>";
		}
		xml += "<ReturnInfo><StreamRoundInfoQuery><RoundStream Index=\"" + channelIndex + "\" URL=\"" + url
				+ "\"/></StreamRoundInfoQuery></ReturnInfo>";
		xml += "</Msg>";
		return xml;
	}
	
	public MSGHeadVO getBsData() {
		return bsData;
	}

	public void setBsData(MSGHeadVO bsData) {
		this.bsData = bsData;
	}

	public String getDownString() {
		return downString;
	}

	public void setDownString(String downString) {
		this.downString = downString;
	}

	public UtilXML getUtilXML() {
		return utilXML;
	}

	public void setUtilXML(UtilXML utilXML) {
		this.utilXML = utilXML;
	}

}
