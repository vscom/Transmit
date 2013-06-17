package com.bvcom.transmit.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.handle.alarm.ReplyAlarmErrorTableHandle;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.util.XMLExt;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.alarm.ReplyAlarmErrorTableVO;

public class ReplyAlarmErrorTaskThread extends Thread {

    private static Logger log = Logger.getLogger(ReplyAlarmErrorTaskThread.class.getSimpleName());
    
    UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    SysInfoVO sysVO = coreData.getSysVO();
    
    public void run() {
    	if (sysVO.getIsAutoAlarmReply() != 0) {
    		recordTaskProcess();
    	} else {
    		log.info("�رձ��������Զ��ϱ�����, �����򿪴˹���, ������ IsAutoAlarmReply=\"1\"");
    	}
    	
    }
    
    private void recordTaskProcess() {

    	log.info("�������������Զ��ϱ��߳�");
    	while(true) {
    		
	    	ReplyAlarmErrorTableHandle replyAlarmErrorTableHandle = new ReplyAlarmErrorTableHandle();
	    	
	    	List alarmList = replyAlarmErrorTableHandle.getReplyAlarmErrorTaskList();
	    	
	    	for(int i=0; i<alarmList.size(); i++) {
	    		ReplyAlarmErrorTableVO vo = (ReplyAlarmErrorTableVO)alarmList.get(i);
	    		
	            try {
	            	//log.info("����������Ϣ"+vo.getReplyXML());
	                // ���·���Xml��������ȡͷ����Ϣ
	            	//������������ɹ��ͷ���true ����ɾ���������и�������  JI Long 2011-06-26
	                if(utilXML.SendUpXML(vo.getReplyXML(), sysVO.getCenterAlarmURL())){
	                	//1.�����ɹ� ɾ���ò����ɹ���¼��Ϣ
	                	replyAlarmErrorTableHandle.delReplyAlarmErrorTable(vo);
	                	//2.���±�����ʷ�����Ƿ�ɹ���isSuccess��״̬
	                	upAlarmSearchTableIsSuccess(vo);
	                }
	            } catch (Exception e) {
	            	e.printStackTrace();
	            	log.error("���������Զ�������ַ: " + sysVO.getCenterAlarmURL());
	                log.error("���������Զ������̳߳���: " + e.getMessage());
	            }
		    	try {
		    		// ÿ��2���ϱ�һ����������
					Thread.sleep(1000*2);
				} catch (InterruptedException e) {
				}
	    	}
	    	
	    	try {
				Thread.sleep(CommonUtility.REPLY_ALARM_ERROR_TIME);
			} catch (InterruptedException e) {
			}
    	}
    }
    
    /**
     * ���� ������ʷ�� isSuccess���ϱ��ɹ�״̬��
     * @param vo
     */
    private void upAlarmSearchTableIsSuccess(ReplyAlarmErrorTableVO vo){
    	/*
    	  <?xml version="1.0" encoding="GB2312" standalone="yes" ?> 
    	  <Msg Version="4" MsgID="2" Type="MonUp" DateTime="2002-08-17 15:30:00" SrcCode="110000M01" DstCode="110000G01" ReplyID="1000_ID">
    	  <Return Type="AlarmSearchPSet" Value="0" Desc="�ɹ�" /> 
    	  <ReturnInfo>
    	  <AlarmSearchPSet Index="0" Freq="259000" ServiceID="501" VideoPID="2060" AudioPID="2061">
    	  <AlarmSearchP Type="31" Desc="��֡" Value="1" Time="2002-08-17 15:30:00" /> 
    	  <AlarmSearchP Type="32" Desc="�ڳ�" Value="1" Time="2002-08-17 15:30:00" /> 
    	  <AlarmSearchP Type="33" Desc="�ް���" Value="1" Time="2002-08-17 15:30:00" /> 
    	  </AlarmSearchPSet>
    	  </ReturnInfo>
    	  </Msg>
    	*/
    	String xmlString=vo.getReplyXML();
    	UtilXML utilXML=new UtilXML();
    	List<String> alarmIdList=new ArrayList<String>();
    	
    	Document document=null;
    	try {
			document=utilXML.StringToXML(xmlString);
		} catch (CommonException e) {
		}
		Element root=document.getRootElement();
		if(root==null){
			return;
		}
		Element ReturnInfo=root.element("ReturnInfo");
		if(ReturnInfo==null){
			return;
		}
		for(Iterator<Element> iter=ReturnInfo.elementIterator();iter.hasNext();){
			Element AlarmSearch=iter.next();
			for(Iterator<Element> it=AlarmSearch.elementIterator();it.hasNext();){
				alarmIdList.add(it.next().attributeValue("AlarmID"));
			}
		}
		if(alarmIdList.size()!=0){
			Connection conn=null;
			Statement statement=null;
			try {
				conn=DaoSupport.getJDBCConnection();
				conn.setAutoCommit(false);
				String strSql=new String();
				for(int i=0;i<alarmIdList.size();i++){
					strSql="update alarmsearchtable set IsSuccess = 0 where id = "+alarmIdList.get(i)+";";
					try {
						statement=conn.createStatement();
						statement.executeUpdate(strSql);
					} catch (Exception e) {
						log.error("���±�����ʷ��isSuccessʧ�ܣ�"+e.getMessage());
						log.error("���±�����ʷ��isSuccessSQL��"+strSql);
					}finally{
						DaoSupport.close(statement);
						strSql="";
					}
				}
				conn.commit();
			} catch (Exception e) {
				// TODO: handle exception
				try {
					conn.rollback();
				} catch (SQLException e1) {
				}
				log.error("���±�����ʷ��isSuccessʧ��"+e.getMessage());
			}finally{
				try {
					DaoSupport.close(conn);
				} catch (DaoException e) {
					log.error("�رո��±�����ʷ�����Ӵ���"+e.getMessage());
				}
			}
		}
    }
}





















