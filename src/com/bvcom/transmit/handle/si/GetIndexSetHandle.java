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
	     * 性能指标查询
	     *
	     */
	    @SuppressWarnings("unchecked")
		public void downXML() {
	    	List SMGSendList = new ArrayList();
	    	// 3:GetIndexSet(性能指标和星座图)
	    	SMGSendList = CommonUtility.checkSMGChannelType("GetIndexSet", SMGSendList);
	        
	        String upString = "";
	        for (int i=0; i< SMGSendList.size(); i++) {
	            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
	            try {
	                // 性能指标查询下发
	                upString = utilXML.SendDownXML(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
	                // 只有一个通道做频道扫描
	                break;
	            } catch (CommonException e) {
	                log.error("向SMG下发性能指标查询出错：" + smg.getURL());
	            }
	        }
	        
	        //保存入库
	        if (bsData.getStatusQueryType().equals("GetIndexSet")) {
	            try {
	                if (upString != null && !upString.trim().equals("")) {
	                    this.upIndexSetTable(upString);
	                } else {
	                    // 取得返回失败的错误信息
	                	GetIndexSetParse getindex = new GetIndexSetParse();
	                    upString = getindex.ReturnXMLByURL(bsData, null, 1);
	                }
	            } catch (DaoException e) {
	                log.error("插入性能指标查询结果失败: " + e.getMessage());
	            } 
	        }
	        
	        //返回结果
	        try {
	        	
	            utilXML.SendUpXML(utilXML.replaceXMLMsgHeader(upString, bsData), bsData);
	        } catch (CommonException e) {
	            log.error("上发频道扫描信息失败: " + e.getMessage());
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
	        
	        //log.info("性能指标查询插入数据库：" + strBuff.toString());
	        
	        try {
	            statement = conn.createStatement();
	            
	            statement.executeUpdate(strBuff.toString());
	            
	        } catch (Exception e) {
	        	log.error("错误SQL: " + strBuff.toString());
	            log.error("性能指标查询插入数据库错误: " + e.getMessage());
	        } finally {
	            DaoSupport.close(statement);
	            DaoSupport.close(conn);
	        }
	        log.info("性能指标查询插入数据库成功!");
	    	
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
