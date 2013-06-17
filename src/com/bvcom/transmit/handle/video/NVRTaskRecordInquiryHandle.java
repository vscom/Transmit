package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.rec.NVRTaskRecordInquiryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

/**
 * ����¼��鿴
 * FIXME Ŀǰֻ����һ̨TSC
 * @author Bian Jiang
 *
 */
public class NVRTaskRecordInquiryHandle {

    private static Logger log = Logger.getLogger(NVRTaskRecordInquiryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private static MemCoreData coreData = MemCoreData.getInstance();
    
    List TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRTaskRecordInquiryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    public NVRTaskRecordInquiryHandle() {
    }
    
    /**
     * 1. �·���TSC
     * 2. ����TSC������Ϣ
     * 3. �ϱ���Ϣ������
     *
     */
    public void downXML() {
        
        // ��������
        String upString = "";
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("����¼��鿴StringToXML Error: " + e.getMessage());
        };
        
        NVRTaskRecordInquiryParse nvrTaskRecordInquiry = new NVRTaskRecordInquiryParse();
        
        List<ProvisionalRecordTaskSetVO> NVRTaskRecordInquiryList = nvrTaskRecordInquiry.getIndexByDownXml(document);
        
        // ȡ��TSC�����ļ���Ϣ
//        List TSCList = new ArrayList();
        
        // ȡ���·�TSC URL�б���Ϣ
        for(int i= 0; i<NVRTaskRecordInquiryList.size(); i++) {
            ProvisionalRecordTaskSetVO vo = NVRTaskRecordInquiryList.get(i);
            
            vo = this.selectTaskIndex(vo);
//            CommonUtility.checkTSCChannelIndex(vo.getIndex(), TSCList);
        }
        
        // TSC �·�ָ��,  
        // FIXME ���ͬʱ����̨TSC���ͣ�ֻ�������һ�εķ�������
        String url = "";
        for (int i=0; i< TSCSendList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(i);
            try {
                if (!url.equals(tsc.getURL())) {
					// ����¼����Ϣ�·� timeout 1000*30 ��ʮ��
					upString = utilXML.SendDownXML(this.downString, tsc
							.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
					url = tsc.getURL();
                    if(upString.equals("")) {
                    	log.info("������ϢΪ��: " + tsc.getURL());
                    	continue;
                    } else {
                    	break;
                    }
				}
            } catch (CommonException e) {
                log.error("����¼��鿴�� TSC �·�����¼�����" + tsc.getURL());
                upString = "";
                continue;
            }
        } // TSC �·�ָ�� END
        
        try {

        	if(upString == null || upString.equals("")) {
        		upString = utilXML.getReturnXML(bsData, 1);
        	} else {
//        		Thread.sleep(CommonUtility.VIDEO_RETURN_WAIT_TIME);	
        	}
        	
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("����¼��鿴��Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
//        TSCList = null;
        utilXML = null;
    }
    
    public ProvisionalRecordTaskSetVO selectTaskIndex(ProvisionalRecordTaskSetVO vo) {
		StringBuffer strBuff = new StringBuffer();

		Statement statement = null;
		ResultSet rs = null;
		Connection conn;
		
		try {
			conn = DaoSupport.getJDBCConnection();
		
			try {
				strBuff.append("select tr_index from taskrecord where Taskid = '" + vo.getTaskID() + "'");
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				int channelindex = 1;;
				while(rs.next()){
					channelindex = Integer.parseInt(rs.getString("tr_index"));
				}
				
				vo.setIndex(channelindex);
			} catch (Exception e) {
				log.error("����¼���ѯ���ݿ����: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
		} catch (DaoException e1) {
			log.error("����¼���ѯ���ݿ����: " + e1.getMessage());
		}
		return vo;
    }
}
