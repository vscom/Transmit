package com.bvcom.transmit.handle.si;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.index.GetIndexSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;

public class GetIndexSetHandle {
	   private static Logger log = Logger.getLogger(GetIndexSetHandle.class.getSimpleName());
	    
	    private MSGHeadVO bsData = new MSGHeadVO();
	    
	    private String downString = new String();
	    
	    private UtilXML utilXML = new UtilXML();
	    
	    public GetIndexSetHandle(String centerDownStr, MSGHeadVO bsData) {
	        this.downString = centerDownStr;
	        this.bsData = bsData;
	    }
	    
	    /**
	     * ����ָ���ѯ
	     *
	     */
	    @SuppressWarnings("unchecked")
		public void downXML() {
	    	List SMGSendList = new ArrayList();
	    	// 3:GetIndexSet(����ָ�������ͼ)
	    	SMGSendList = CommonUtility.checkSMGChannelType("GetIndexSet", SMGSendList);
	        
	        String upString = "";
	        for (int i=0; i< SMGSendList.size(); i++) {
	            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
	            try {
	                // ����ָ���ѯ�·�
	                upString = utilXML.SendDownXML(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
	                // ֻ��һ��ͨ����Ƶ��ɨ��
	                break;
	            } catch (CommonException e) {
	                log.error("��SMG�·�����ָ���ѯ����" + smg.getURL());
	            }
	        }
	        
	        //�������
	        if (bsData.getStatusQueryType().equals("GetIndexSet")) {
	            try {
	                if (upString != null && !upString.trim().equals("")) {
	                    this.upIndexSetTable(upString);
	                } else {
	                    // ȡ�÷���ʧ�ܵĴ�����Ϣ
	                	GetIndexSetParse getindex = new GetIndexSetParse();
	                    upString = getindex.ReturnXMLByURL(bsData, null, 1);
	                }
	            } catch (DaoException e) {
	                log.error("��������ָ���ѯ���ʧ��: " + e.getMessage());
	            } 
	        }
	        
	        //���ؽ��
	        try {
	        	
	            utilXML.SendUpXML(utilXML.replaceXMLMsgHeader(upString, bsData), bsData);
	        } catch (CommonException e) {
	            log.error("�Ϸ�Ƶ��ɨ����Ϣʧ��: " + e.getMessage());
	        }
	        
	        bsData = null;
	        downString = null;
	        SMGSendList = null;
	        utilXML = null;
	        
	    }

	    public void upIndexSetTable(String xml)throws DaoException{
	    	
	    	StringBuffer strBuff = new StringBuffer();
	        
	        Statement statement = null;
	        
            xml = utilXML.utf8Togb2312(xml);
            
	        Connection conn = DaoSupport.getJDBCConnection();
	        
	        strBuff.append("insert into indexstatus (xml, lasttime) values (");
	        strBuff.append("'" + xml + "', ");
	        strBuff.append("'" + CommonUtility.getDateTime() + "'");
	        strBuff.append(")");
	        
	        //log.info("����ָ���ѯ�������ݿ⣺" + strBuff.toString());
	        
	        try {
	            statement = conn.createStatement();
	            
	            statement.executeUpdate(strBuff.toString());
	            
	        } catch (Exception e) {
	        	log.error("����SQL: " + strBuff.toString());
	            log.error("����ָ���ѯ�������ݿ����: " + e.getMessage());
	        } finally {
	            DaoSupport.close(statement);
	            DaoSupport.close(conn);
	        }
	        log.info("����ָ���ѯ�������ݿ�ɹ�!");
	    	
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
