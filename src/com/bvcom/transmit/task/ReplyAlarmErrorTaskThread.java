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
    		log.info("关闭报警故障自动上报功能, 如果想打开此功能, 请配置 IsAutoAlarmReply=\"1\"");
    	}
    	
    }
    
    private void recordTaskProcess() {

    	log.info("启动报警故障自动上报线程");
    	while(true) {
    		
	    	ReplyAlarmErrorTableHandle replyAlarmErrorTableHandle = new ReplyAlarmErrorTableHandle();
	    	
	    	List alarmList = replyAlarmErrorTableHandle.getReplyAlarmErrorTaskList();
	    	
	    	for(int i=0; i<alarmList.size(); i++) {
	    		ReplyAlarmErrorTableVO vo = (ReplyAlarmErrorTableVO)alarmList.get(i);
	    		
	            try {
	            	//log.info("报警补报信息"+vo.getReplyXML());
	                // 从下发的Xml数据中提取头部信息
	            	//如果报警补报成功就返回true 并且删除补报表中该条报警  JI Long 2011-06-26
	                if(utilXML.SendUpXML(vo.getReplyXML(), sysVO.getCenterAlarmURL())){
	                	//1.补报成功 删除该补报成功记录信息
	                	replyAlarmErrorTableHandle.delReplyAlarmErrorTable(vo);
	                	//2.更新报警历史表中是否成功（isSuccess）状态
	                	upAlarmSearchTableIsSuccess(vo);
	                }
	            } catch (Exception e) {
	            	e.printStackTrace();
	            	log.error("报警故障自动补报地址: " + sysVO.getCenterAlarmURL());
	                log.error("报警故障自动补报线程出错: " + e.getMessage());
	            }
		    	try {
		    		// 每隔2秒上报一个报警数据
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
     * 更新 报警历史表 isSuccess（上报成功状态）
     * @param vo
     */
    private void upAlarmSearchTableIsSuccess(ReplyAlarmErrorTableVO vo){
    	/*
    	  <?xml version="1.0" encoding="GB2312" standalone="yes" ?> 
    	  <Msg Version="4" MsgID="2" Type="MonUp" DateTime="2002-08-17 15:30:00" SrcCode="110000M01" DstCode="110000G01" ReplyID="1000_ID">
    	  <Return Type="AlarmSearchPSet" Value="0" Desc="成功" /> 
    	  <ReturnInfo>
    	  <AlarmSearchPSet Index="0" Freq="259000" ServiceID="501" VideoPID="2060" AudioPID="2061">
    	  <AlarmSearchP Type="31" Desc="静帧" Value="1" Time="2002-08-17 15:30:00" /> 
    	  <AlarmSearchP Type="32" Desc="黑场" Value="1" Time="2002-08-17 15:30:00" /> 
    	  <AlarmSearchP Type="33" Desc="无伴音" Value="1" Time="2002-08-17 15:30:00" /> 
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
						log.error("更新报警历史表isSuccess失败："+e.getMessage());
						log.error("更新报警历史表isSuccessSQL："+strSql);
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
				log.error("更新报警历史表isSuccess失败"+e.getMessage());
			}finally{
				try {
					DaoSupport.close(conn);
				} catch (DaoException e) {
					log.error("关闭更新报警历史表连接错误"+e.getMessage());
				}
			}
		}
    }
}





















