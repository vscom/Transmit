package com.bvcom.transmit.handle.alarm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.handle.alarm.domain.Temp;
import com.bvcom.transmit.parse.alarm.AlarmSwitchSetParse;
import com.bvcom.transmit.parse.alarm.AlarmThresholdSetParse;
import com.bvcom.transmit.parse.alarm.AlarmTypeSetParse;
import com.bvcom.transmit.parse.alarm.ClearAlarmStateParse;
import com.bvcom.transmit.parse.video.ProgramPatrolParse;
import com.bvcom.transmit.parse.video.StreamRoundInfoQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;

/**
 * ��������, ���� ,��ʽ �ͱ���״̬���
 * @author Bian Jiang
 *
 */
public class AlarmSetHandle {
    
    private static Logger log = Logger.getLogger(AlarmSetHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public AlarmSetHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. �·������е�SMG��IPM
     * 2. �ϱ��ɹ���Ϣ������
     *
     */
    @SuppressWarnings({ "deprecation", "deprecation", "deprecation" })
	public void downXML() {
    	//�¼� StringToXMLת�� 
    	//JI LONG 2011-5-12
    	 Document document = null;
         try {
             document = utilXML.StringToXML(this.downString);
         } catch (CommonException e) {
             log.error("Ƶ�㱨������StringToXML Error: " + e.getMessage());
         };
         
         if(bsData.getStatusQueryType().equals("AlarmThresholdSet")) {
        	Element root =document.getRootElement();
        	for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
        		Element AlarmThresholdSet = iter.next();
        		for(Iterator<Element> ite=AlarmThresholdSet.elementIterator();ite.hasNext();){
        			Element AlarmThreshold  = ite.next();
        			String type=AlarmThreshold.attribute("Type").getValue();
        			if(type.equals("42")){
        				String DownThreshold=AlarmThreshold.attribute("DownThreshold").getValue();
        				String UpThreshold=AlarmThreshold.attribute("UpThreshold").getValue();
        				
        				Double Down=new Double(DownThreshold.trim());
        				Double Up=new Double(UpThreshold.trim());
        				
        				DecimalFormat a = new DecimalFormat("#,##0.000000000");
        				String strDown = a.format(Down);
        				String strUp = a.format(Up); 
        				
        				AlarmThreshold.setAttributeValue("DownThreshold", strDown);
        				AlarmThreshold.setAttributeValue("UpThreshold", strUp);
        			}
        		}
        	}
         }
         this.downString=document.asXML();
//         try {
//			System.out.println("ת����"+utilXML.XMLToString(document));
//		} catch (CommonException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        // ��������
        String upString = "";
        
        MemCoreData coreData = MemCoreData.getInstance();
        // ȡ��SMG�����ļ���Ϣ
        List SMGCardList = coreData.getSMGCardList();
        
        // ȡ��IPM�����ļ���Ϣ
        List IPMList = coreData.getIPMList();
        
        if(bsData.getStatusQueryType().equals("AlarmThresholdSet")) {
            // ��������
            AlarmThresholdSetParse AlarmThreshold = new AlarmThresholdSetParse();
            upString = AlarmThreshold.ReturnXMLByURL(this.bsData, 0);
            
            AlarmThreshold = null;
        } else if(bsData.getStatusQueryType().equals("AlarmSwitchSet")) {
            // ��������
            AlarmSwitchSetParse AlarmSwitch = new AlarmSwitchSetParse();
            upString = AlarmSwitch.ReturnXMLByURL(this.bsData, 0);
            AlarmSwitch.parseDB(document);
            AlarmSwitch = null;
        } else if(bsData.getStatusQueryType().equals("AlarmTypeSet")) {
            // ������ʽ
            AlarmTypeSetParse AlarmType = new AlarmTypeSetParse();
            upString = AlarmType.ReturnXMLByURL(this.bsData, 0);
            
            AlarmType = null;
        } else if(bsData.getStatusQueryType().equals("ClearAlarmState")) {
            ClearAlarmStateParse ClearAlarmStat = new ClearAlarmStateParse();
            upString = ClearAlarmStat.ReturnXMLByURL(this.bsData, 0);
            
            ClearAlarmStat = null;
        }
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("����¼��ѡ̨��Ϣʧ��: " + e.getMessage());
        }
        
//        // IPM �·�ָ��,  
//        for (int i=0; i< IPMList.size(); i++) {
//            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
//            try {
//                // ����ͼ��Ϣ�·� timeout 1000*30 ��ʮ��
//                utilXML.SendDownNoneReturn(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
//            } catch (CommonException e) {
//                log.error("����ͼ��IPM�·�����¼�����" + ipm.getURL());
//            }
//        } // IPM �·�ָ�� END
        int count=0;
        /**
         * if ת��Э��Ϊ ���޿��� ���������� 
         * JI LONG 2011-5-11 
         */
        if(bsData.getStatusQueryType().equals("AlarmThresholdSet")||bsData.getStatusQueryType().equals("AlarmSwitchSet")){
        	
        	if(CommonUtility.AlarmSwitch){
        		try {
					Thread.sleep(8000);
				} catch (Exception e) {
				}
        	}else{
        		CommonUtility.AlarmSwitch=true;
        	}
        	
        	// SMG �·�ָ��,  
        	List<Temp> msgUrl=returnMsgURlList();
        	log.info("Ƶ��ļ��ϣ�"+msgUrl);
        	for (int i=0; i< msgUrl.size(); i++) {
        		Temp t=msgUrl.get(i);
        		//���Ƶ��Ϊall�������msg�·� Ji  Long 2011-06-27 
        		if(downString.indexOf("Freq=\"ALL\">")!=-1||downString.indexOf("Freq=\"all\">")!=-1){
        			try {
        				utilXML.SendDownNoneReturn(this.downString, t.getMsgURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
        				count++;
					} catch (CommonException e) {
						count++;
	        			log.error("���޿���/ֵ��SMG�·�����¼�����" + t.getMsgURL());
	        		}
        		}
        		if(downString.indexOf("Freq")==-1){
        			try {
        				utilXML.SendDownNoneReturn(this.downString, t.getMsgURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
        				count++;
					} catch (CommonException e) {
						count++;
	        			log.error("���޿���/ֵ��SMG�·�����¼�����" + t.getMsgURL());
	        		}
        		}
        		//if ��Ƶ��  ��Э���������� ��õ�ַ���� Э��
        		if(downString.indexOf(t.getFreq())!=-1){
        			try {
        				utilXML.SendDownNoneReturn(this.downString, t.getMsgURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
        				count++;
					} catch (CommonException e) {
						count++;
	        			log.error("���޿���/ֵ��SMG�·�����¼�����" + t.getMsgURL());
	        		}
        		}
        		
        	} // SMG �·�ָ�� END
        	try {
				Thread.sleep(8000);
			} catch (Exception e) {
			}
        	CommonUtility.AlarmSwitch=false;
        }else{
        	// SMG �·�ָ��,  
        	for (int i=0; i< SMGCardList.size(); i++) {
        		SMGCardInfoVO smg = (SMGCardInfoVO) SMGCardList.get(i);
        		try {
        			// ����ͼ��Ϣ�·� timeout 1000*30 ��ʮ��
        			utilXML.SendDownNoneReturn(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
        		} catch (CommonException e) {
        			log.error("����ͼ��SMG�·�����¼�����" + smg.getURL());
        		}
        	} // SMG �·�ָ�� END
        }
        log.info("һ�����ˣ�"+count+"�Σ�");
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
    }
    /**
     * ��ȡMsg��URl����
     * 
     * JI LONG 2011-5-11
     */
    public List returnMsgURlList(){
    	Statement statement = null;
        ResultSet rs = null;
        Connection conn = null;
        //һ��һmsgURL
        //String sqlStr="SELECT Devindex,Freq,smgURl FROM channelremapping c where Freq != 0 and smgURl is not null group by Freq;";
        String sqlStr="SELECT Devindex,Freq,smgURl FROM channelremapping c where Freq != 0 and smgURl is not null ;";
        List<Temp> msgUrl=new ArrayList<Temp>();
        List<Temp> msgs=new ArrayList<Temp>();
        try {
        	conn = DaoSupport.getJDBCConnection();
        	statement = conn.createStatement();
            rs = statement.executeQuery(sqlStr);
            while(rs.next()){
            	if (rs.getString("Freq") == null || rs.getString("Freq").equals("") || rs.getString("Freq").equals("0")) {
                	continue;
                }else{
                	Temp t=new Temp(rs.getString("Freq"), rs.getString("smgURL"));
//                	if(!isMsgURL(msgUrl,t)){
                	msgs.add(t);
//                	}
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
			log.error("��ȡһ��һMsgURL��ַ���ϴ���: " + e.getMessage());
		} finally {
            try {
            	DaoSupport.close(rs);
            	DaoSupport.close(statement);
    			DaoSupport.close(conn);
    		} catch (DaoException e) {
    			log.error("�ر����ݿ�ʧ��: " + e.getMessage());
    		}
        }
		
		
		/*
		//��ѵmsgURL
		sqlStr="SELECT xml,smgURL,statusFlag,Freq FROM monitorprogramquery m where statusFlag in(2,3,4) and smgURl is not null ;";
		
		
        try {
        	conn = DaoSupport.getJDBCConnection();
        	statement = conn.createStatement();
            rs = statement.executeQuery(sqlStr);
            while(rs.next()){
            	if (rs.getString("statusFlag").equals("3")) {
            		Temp t=new Temp(rs.getString("Freq"), rs.getString("smgURL"));
//            		if(!isMsgURL(msgUrl,t)){
            			msgUrl.add(t);
//            		}
                }else if(rs.getString("statusFlag").equals("2")){
                	String strXml=rs.getString("xml");
                	Document document=utilXML.StringToXML(strXml);
                	
                	ProgramPatrolParse ProgramPatrol=new ProgramPatrolParse();
                	List<String> freqList=(List<String>)ProgramPatrol.getFreqXml(document);
                	for(String s:freqList){
                		Temp t=new Temp(s, rs.getString("smgURL"));
//                		if(!isMsgURL(msgUrl,t)){
                			msgUrl.add(t);
//                		}
                	}
                }else if(rs.getString("statusFlag").equals("4")){
                	String strXml=rs.getString("xml");
                	Document document=utilXML.StringToXML(strXml);
                	StreamRoundInfoQueryParse streamRoundInfoQueryParse=new StreamRoundInfoQueryParse();
                	List<String> freqList=(List<String>)streamRoundInfoQueryParse.getFreqXml(document);
                	for(String s:freqList){
                		Temp t=new Temp(s, rs.getString("smgURL"));
//                		if(!isMsgURL(msgUrl,t)){
                			msgUrl.add(t);
//                		}
                	}
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
			 log.error("��ȡ��ѯMsgURL��ַ���ϴ���: " + e.getMessage());
		} finally {
            try {
            	DaoSupport.close(rs);
            	DaoSupport.close(statement);
    			DaoSupport.close(conn);
    		} catch (DaoException e) {
    			log.error("�ر����ݿ�ʧ��: " + e.getMessage());
    		}
        }
		*/
		for (int i = 0; i < msgs.size(); i++) {
			Temp t=msgs.get(i);
			String ip=t.getMsgURL().split("/")[2].split(":")[0];
			//modifide by tqy
			ip=t.getMsgURL();
			
			if(msgUrl.size()==0){
				msgUrl.add(t);
				continue;
			}
			boolean flag=false;
			for(int j=0;j<msgUrl.size();j++){
				Temp tt=msgUrl.get(j);
				String ipt=tt.getMsgURL().split("/")[2].split(":")[0];
				//modifide by tqy
				ipt=tt.getMsgURL();
				
				if(ip.equals(ipt)){
					flag=true;
					break;
				}
			}
			if(!flag){
				msgUrl.add(t);
			}
		}
    	return msgUrl;
    }
    //����õ�ַ�Ѿ������򷵻�true �����ڷ���false
    public  boolean isMsgURL(List<Temp> msgUrl,Temp t) {
    	boolean flag =false;
		for(int i=0 ;i<msgUrl.size();i++){
			Temp temp=msgUrl.get(i);
			if(temp.getMsgURL().equals(t.getMsgURL())){
				flag=true;
			}
		}
		return flag;
	}
}
