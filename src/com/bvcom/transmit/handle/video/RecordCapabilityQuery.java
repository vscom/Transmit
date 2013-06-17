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
import com.bvcom.transmit.parse.video.RecordCapabilityQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.video.RecordCapabilityQueryVO;

public class RecordCapabilityQuery {
    
    private static Logger log = Logger.getLogger(RecordCapabilityQuery.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public RecordCapabilityQuery(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. �·���TSC
     * 2. �·��ɹ������سɹ�
     * 3. �������ϱ��ɹ���Ϣ
     *
     */
    public void downXML() {
        // ��������
        String upString = "";
        
        Document document = null;
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("¼��·����ѯStringToXML Error: " + e.getMessage());
        };
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        SysInfoVO sysVO = coreData.getSysVO();
        
        int autoRecordCount = 0;
        
        int msgIndex=0;
        
        int NewIndexCount = 0;
        
        RecordCapabilityQueryParse recordCapabilityQuery = new RecordCapabilityQueryParse();
        
        List<RecordCapabilityQueryVO> recordQueryList = recordCapabilityQuery.getDownXml(document);
        
        try {
			autoRecordCount = GetAutoRecordCount();
			msgIndex = GetMsgIndexCount();
			NewIndexCount = sysVO.getMaxAutoRecordNum() - autoRecordCount;
			
			if((autoRecordCount + recordQueryList.size()) > sysVO.getMaxAutoRecordNum()) {
//				for(int i=0; i<recordQueryList.size(); i++) {
//					RecordCapabilityQueryVO vo = (RecordCapabilityQueryVO)recordQueryList.get(i);
//					vo.setIsRecord(0);
//				}
//			} else {
				recordQueryList = GetRecordCapabilityList(recordQueryList);
			}
			
			upString = recordCapabilityQuery.createForUpXML(bsData, recordQueryList, msgIndex,NewIndexCount);
			
		} catch (DaoException e1) {

		}
        
        
        /*
         * ¼��·����Ҫת����ͳһ����
         * Del By Bian Jiang
         * 
        // ȡ��TSC�����ļ���Ϣ
        List TSCList = coreData.getTSCList();
        
        // TSC �·�ָ��,  
        // FIXME Ŀǰֻ����һ��TSC�����
        String url = "";
        for (int i=0; i< TSCList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCList.get(i);
            try {
                if(!url.equals(tsc.getURL())) {
                    // ¼��·����ѯ�·� timeout 1000*30 ��ʮ��
                	upString = utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
                    url = tsc.getURL();
                    if(upString != null && !upString.equals("")) {
                    	break;
                    }
                }
                
            } catch (CommonException e) {
                log.error("¼��·����ѯ��TSC�·�����¼�����" + tsc.getURL());
                upString = "";
            }
        } // TSC �·�ָ�� END
        */
        
        try {
        	if (upString == null || upString.equals("")) {
        		upString = utilXML.getReturnXML(bsData, 1);
        	}
        	
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("¼��·����ѯ��Ϣʧ��: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
//        TSCList = null;
        utilXML = null;
        
    }
    /*
     * �쿨ʣ��ͨ����ѯ Ji  Long 
     */
    
    private  int GetMsgIndexCount() throws DaoException {
    	MemCoreData coreDate=MemCoreData.getInstance();
    	Statement statement = null;
    	Connection conn = DaoSupport.getJDBCConnection();
    	int count = 0;
    	ResultSet rs = null;
    	
    	StringBuffer strBuff = new StringBuffer();
    	
    	// ȡ����ؽ�ĿƵ����Ϣ
    	//strBuff.append("select count(*)  from channelremapping where RecordType=2 ");
    	
    	//�޸� ����¼�ƽ�Ŀ�������㷨  һ�������� 5������    Ji Long  2011-08-11 pm
    	strBuff.append("select smgURL  from channelremapping where RecordType=2 group by smgURL");
    	try {
    		statement = conn.createStatement();
    		
    		rs = statement.executeQuery(strBuff.toString());
    		
    		while(rs.next()){
    			count++;
    		}
    		
    	} catch (Exception e) {
    		log.error("ȡ���Ѿ����ڵ��Զ�¼��·������: " + e.getMessage());
    	} finally {
    		DaoSupport.close(rs);
    		DaoSupport.close(statement);
    		
    	}
    	DaoSupport.close(conn);
    	
    	List list=coreDate.getSMGCardList();
    	
    	int temp =0;
    	for (int i = 0; i < list.size(); i++) {
			SMGCardInfoVO smg=(SMGCardInfoVO)list.get(i);
			if(smg.getIndexType().equals("AutoRecord")){
				temp++;
			}
    		
		}
    	if(temp-count <=0){
    		return 0;
    	}else{
    		return temp-count;
    	}
    	
    }
    private  int GetAutoRecordCount() throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		int count = 0;
		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// ȡ����ؽ�ĿƵ����Ϣ
		//strBuff.append("select count(*)  from channelremapping where RecordType=2 ");
		
		//�޸� ����¼�ƽ�Ŀ�������㷨  һ�������� 5������    Ji Long  2011-08-11 pm
		strBuff.append("select HDflag  from channelremapping where RecordType=2 ");
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				String temp =rs.getString("HDflag");
				if(temp != null){
					if(Integer.parseInt(temp)==0){
						count++;
					}else if(Integer.parseInt(temp)==1){
						count+=5;
					}
				}
				//count = Integer.parseInt(rs.getString("count(*)"));
			}
			
		} catch (Exception e) {
			log.error("ȡ���Ѿ����ڵ��Զ�¼��·������: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
	
		}
			
		DaoSupport.close(conn);
		
		return count;
	}

    private  List<RecordCapabilityQueryVO> GetRecordCapabilityList(List recordQueryList) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		List freqList = new ArrayList();
		
		for(int i=0; i<recordQueryList.size(); i++) {
			StringBuffer strBuff = new StringBuffer();
			RecordCapabilityQueryVO vo = (RecordCapabilityQueryVO)recordQueryList.get(i);
			
			int isSeccess = 0;
			
			// ȡ����ؽ�ĿƵ����Ϣ
			strBuff.append("select count(*)  from channelremapping where Freq=\"" + vo.getFreq() + "\" and ServiceID=\"" + vo.getServiceID() + "\" ");
			
			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					isSeccess = Integer.parseInt(rs.getString("count(*)"));
					
					if(isSeccess > 0) {
						vo.setIsRecord(0);
						break;
					} else {
						isSeccess = 0;
					}
					
				}
				
			} catch (Exception e) {
				log.error("¼��·����ѯ����: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
		
			}
			
			for(int j=0; j<freqList.size(); j++) {
				int freq = (Integer)freqList.get(j);
				if(freq == vo.getFreq()) {
					vo.setIsRecord(0);
					isSeccess = 1;
					break;
				}
			}
			strBuff = null;
			if (isSeccess == 0) {
				
				strBuff = new StringBuffer();
				// ȡ����ؽ�ĿƵ����Ϣ
				strBuff.append("select count(*)  from channelremapping where RecordType=0 ");
				
				try {
					statement = conn.createStatement();
					
					rs = statement.executeQuery(strBuff.toString());
					
					while(rs.next()){
						int count = Integer.parseInt(rs.getString("count(*)"));
						if(count > 0) {
							vo.setIsRecord(0);
							freqList.add((Integer)vo.getFreq());
							isSeccess = 1;
							break;
						} else {
							vo.setIsRecord(2);
							break;
						}
					}
					
				} catch (Exception e) {
					log.error("ȡ���Ѿ����ڵ��Զ�¼��·������: " + e.getMessage());
				} finally {
					DaoSupport.close(rs);
					DaoSupport.close(statement);
			
				}
			
			}
			
		}
		DaoSupport.close(conn);
		
		return recordQueryList;
	}
    
}
