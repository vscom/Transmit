package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.video.ChangeProgramQueryParse;
import com.bvcom.transmit.parse.video.RTVSResetConfigParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.ChangeProgramQueryVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class ChangeProgramQueryHandle {
    
    private static Logger log = Logger.getLogger(ChangeProgramQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public ChangeProgramQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. SMG �·��ֶ�ѡ̨
     * 2. �ɹ����·�ָ�����(��¼��ǰ��Ŀ��Ϣ)
     * 3. ��ָ���ط�(���ݿ�������ļ�)ȡ��ʵʱ��ƵURL
     * 4. URLд��XML���ϱ�XML������
     *
     */
    public void downXML() {
        // ��������
        String upString = "";
        //List SMGSendList = new ArrayList();
        
        boolean isErr = false;
        
        MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 3);
		} catch (DaoException e1) {
			log.error("ȡ��ʵʱ��ƵURL����: " + e1.getMessage());
			isErr = true;
		}
        
        ChangeProgramQueryParse ChangeProgram = new ChangeProgramQueryParse();
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("�ֶ�ѡ̨StringToXML Error: " + e.getMessage());
        }
        // ȡ���·�XML��������
        ChangeProgramQueryVO vo = ChangeProgram.getDownObject(document);
//        System.out.println(vo);
        // �鿴ʵʱ��Ƶ�Ƿ���һ��һ���Ľ�Ŀ����
        SetAutoRecordChannelVO SetAutoRecordChannelVO = new SetAutoRecordChannelVO();
        SetAutoRecordChannelVO.setFreq(vo.getFreq());
        SetAutoRecordChannelVO.setServiceID(vo.getServiceID());
        
        int isRemapping = 0;
        
        try {
        	isRemapping = SetAutoRecordChannelHandle.isHaveProgramInRemapping(SetAutoRecordChannelVO);
        } catch (Exception ex) {
        }
        
        if (isRemapping == 0) {
        	// û��ȡ��һ��һ����Ŀ
            try {
            	this.downString = this.downString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
            	this.downString = this.downString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
            	
                if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_3)) {
                	this.downString = this.downString.replaceAll("Index=\"0\"", "Index=\"" + rtvsVO.getSmgIndex() + "\"");
                }
                // ѡ̨��Ϣ�·� timeout 1000*3 ����
                utilXML.SendDownNoneReturn(this.downString, rtvsVO.getSmgURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                // ֻ��һ��ͨ�����ֶ�ѡ̨
                //break;

            } catch (Exception e) {
                log.error("�·��ֶ�ѡ̨����" + rtvsVO.getSmgURL());
                isErr = true;
            }
        } else {
        	// ȡ��һ��һ����Ŀ
        	rtvsVO.setRtvsIP(SetAutoRecordChannelVO.getUdp());
        	rtvsVO.setRtvsPort(SetAutoRecordChannelVO.getPort());
        } 

        // RTVS�޸���������IP�Ͷ˿�
        RTVSResetConfigParse RTVSReset = new RTVSResetConfigParse();
        rtvsVO.setRunTime(vo.getRunTime());
        rtvsVO.setFreq(vo.getFreq());
        rtvsVO.setServiceID(vo.getServiceID());
        rtvsVO.setIndex(0);
        
        
        //2012-07-16 ��һͨ����VPID:102  APID:103 �ڶ�ͨ��������VPID  vpid:502 apid:503
        try
        {
	    	if(rtvsVO.getSmgURL().contains("Setup1")){
	    		rtvsVO.setVideoPID(102);
	    		rtvsVO.setAudioPID(103);
	    	}else if(rtvsVO.getSmgURL().contains("Setup2")){
	    		rtvsVO.setVideoPID(502);
	    		rtvsVO.setAudioPID(503);
	    	}
        }
        catch(Exception ex){
        	
        }
        
        //CodingFormat="cbr" Width="960"  Height="544" Fps="25" Bps="1500000" 
        try{
        	if(vo.getCodingFormat().equals(null)){
        		
        	}
        	else
        	{
        		rtvsVO.setCodingFormat(vo.getCodingFormat());
        		rtvsVO.setWidth(vo.getWidth());
        		rtvsVO.setHeight(vo.getHeight());
        		rtvsVO.setBps(vo.getBps());
        		rtvsVO.setFps(vo.getFps());
        	}
        	
        }catch(Exception ex){
        	System.out.println(ex.getMessage());
        }
    	
        String rtvsString = RTVSReset.createForDownXML(bsData, rtvsVO);
        
        
        try {
          
        	// ѡ̨��Ϣ�·� timeout 1000*10ʮ��
        	upString = utilXML.SendDownXML(rtvsString, rtvsVO.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);//
        } catch (CommonException e) {
            log.error("�·�RTVS�޸���������IP�Ͷ˿ڳ���" + rtvsVO.getRTVSResetURL());
            isErr = true;
        }
        
		String url = "";
		
        try {
            document = utilXML.StringToXML(upString);
            url = RTVSReset.getReturnURL(document);
        } catch (CommonException e) {
        	isErr = true;
            log.error("��ƵURL StringToXML Error: " + e.getMessage());
        }
        
        if (isErr) {
            // ʧ��
            upString = utilXML.getReturnXML(bsData, 1);
        } else if(url.equals("")){
        	upString = utilXML.getReturnXML(bsData, 1);
        }else{
            // �ɹ�
            // �ֶ�ѡ̨���
            try {
                upChangeProgramTable(vo, this.downString);
            } catch (DaoException e) {
                log.error("�ֶ�ѡ̨�������ݿ����: " + e.getMessage());
            }
            // ʵʱ��ƵURL �������ļ���ȡ
            //MemCoreData coreData = MemCoreData.getInstance();
            upString = ChangeProgram.ReturnXMLByURL(bsData, url, 0, vo.getIndex());
        }
        
        try {
            // �ȴ�һ���ӣ���SMG���������ֹRTVS��û�нӵ�����������
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("�Ϸ��ֶ�ѡ̨��Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        utilXML = null;
        ChangeProgram = null;
    }
    
    
    
    /**
     * �������Ƶ��ɨ���
     * @throws DaoException 
     */
    private static void upChangeProgramTable(ChangeProgramQueryVO vo, String downXML) throws DaoException {

        StringBuffer strBuff = new StringBuffer();
        
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = DaoSupport.getJDBCConnection();
        
        // update channelstatus c set freq = 6000000, qam = 'QAM128' where channelindex = 1
        
		strBuff.append("update monitorprogramquery c set ");
		// statusFlag: 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ�
		strBuff.append("statusFlag = 3, ");
		strBuff.append(" xml = '" + downXML + "', ");
		// RunType 1:�ֶ�ѡ̨ 2:һ��һ��� 3:��ѯ��� 4:�ֲ�
		strBuff.append(" RunType = 1, ");
		strBuff.append(" Freq = " + vo.getFreq() + ", ");
		strBuff.append(" ServiceID = " + vo.getServiceID() + ", ");
		strBuff.append(" lastDatatime = '" + CommonUtility.getDateTime() + "' ");
		strBuff.append(" where statusFlag = 3 ");
        
        //log.info("�ֶ�ѡ̨�������ݿ⣺" + strBuff.toString());
        
        try {
            statement = conn.createStatement();
            
            statement.executeUpdate(strBuff.toString());
            
        } catch (Exception e) {
            log.error("�ֶ�ѡ̨�������ݿ����: " + e.getMessage());
        } finally {
            DaoSupport.close(rs);
            DaoSupport.close(statement);
            DaoSupport.close(conn);
        }
        //log.info("�ֶ�ѡ̨�������ݿ�ɹ�!");
    }
    
    public String getdownXML(ChangeProgramQueryVO vo) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"4\" MsgID=\"2\" Type=\"MonDown\" DateTime=\"2002-08-17 15:30:00\" SrcCode=\"110000X01\" DstCode=\"110000N01\" SrcURL=\"http://10.24.32.28:8089\"  Priority=\"1\">");
        strBuf.append("<ChangeProgramQuery>");
        strBuf.append("<ChangeProgram  Index=\" "+vo.getIndex() +" \" Freq=\""+vo.getFreq()+"\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\""+vo.getServiceID()+"\" VideoPID=\"1032\" AudioPID=\"1033\"/>");
        strBuf.append("</ChangeProgramQuery> </Msg>");
        return strBuf.toString();
    }
    
    
    
    
    
    
    
    
}
