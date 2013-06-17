package com.bvcom.transmit.handle.smginfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.handle.video.MonitorProgramQueryHandle;
import com.bvcom.transmit.handle.video.SetAutoRecordChannelHandle;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.index.GetNvrStatusVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class GetNvrStatus {
	
    private static Logger log = Logger.getLogger(GetNvrStatus.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public GetNvrStatus(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    /**
     * 1. 将协议依次发送给TSC、RTVM
     * 2. 等待接收所有TSC、RTVM的返回信息
     * 3. 查询epg.xml是否生成
     * 4. 打包板卡状态信息的协议
     * 5. 把状态协议返回给中心
     */
    @SuppressWarnings("unchecked")
	public void downXML() {
    	Document document=null;
    	boolean isErr=true;
    	
    	/*
    	List<TSCInfoVO> tscs=(List<TSCInfoVO>)coreData.getTSCList();
		for(int j=0;j<tscs.size();j++){
			TSCInfoVO tsc=tscs.get(j);
			isErr=utilXML.SendUpXML(downString, tsc.getURL());
		}
		//发送到Rtvm
		MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 1);
			if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
				rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 2);
				if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
					rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 3);
					if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 4);
						if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
    						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 5);
    						if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
        						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 0);
    						}
						}	
					}
				}	
			}
		} catch (DaoException e1) {
			log.error("取得RTVM的URL错误: " + e1.getMessage());
			isErr=false;
		}
		isErr=utilXML.SendUpXML(downString, rtvsVO.getRTVSResetURL());	
	*/
	    //表：SMG_CARD_INFO
	    
	    //步骤1：从通道状态表中获取通道总数TOTAL和通道值INDEX
		//步骤2：从通道映射表中找到通道对应的自动录制节目信息，根据SERVICEID,FREQ获取节目名
	    //步骤3：从任务录像表中找到通道对应的任务节目、根据SERVICEID,FREQ获取节目名
	    //步骤4：从一对一监视表中找到手动选择的节目信息、根据SERVICEID,FREQ获取节目名
    
    	List<SMGCardInfoVO> NvrStatusList = new ArrayList();
    	@SuppressWarnings("unused")
		Statement statement = null;
		@SuppressWarnings("unused")
		ResultSet rs = null;
		try
		{
			@SuppressWarnings("unused")
			Connection conn = DaoSupport.getJDBCConnection();
			StringBuffer strBuff1 = new StringBuffer();
			strBuff1.append("select * from smg_card_info order by smgIndex");
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff1.toString());
				while(rs.next()){
					SMGCardInfoVO smginfo = new SMGCardInfoVO();
					int index = rs.getInt("smgIndex");
					smginfo.setIndex(index);
					String ip = rs.getString("smgIp");
					smginfo.setIP(ip);
					int inputtype = rs.getInt("smgInputtype");
					smginfo.setIndexType(String.valueOf(inputtype));
					String url = rs.getString("smgURL");
					smginfo.setURL(url);
					int status = rs.getInt("smgStatus");
					smginfo.setStatus(status);
					//根据通道号获取所有自动录制的节目信息
					smginfo.setAutorecordList(GetAutoRecordVOByIndex(index));
					//根据通道号获取所有任务录制的节目信息
					smginfo.setTaskrecordList(GetTaskRecordVOByIndex(index));
					//根据通道号获取所有视频的节目信息
					smginfo.setRealvideoList(GetRealVideoVOByIndex(index));
					NvrStatusList.add(smginfo);
				}
			} catch (Exception e) {
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		//上报信息给平台
		String upString = "";
		// 上报回复的xml给中心,自己返回成功
		upString = ReturnXMLByURL(this.bsData, NvrStatusList);
		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("通道状态回复失败: " + e.getMessage());
		}

		bsData = null;
		downString = null;
		utilXML = null;
		
    }
    
    
	// 通道状态查询回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, List voList) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">\r\n";
		xml += "<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n";
		xml +="<ReturnInfo>\r\n";
		xml +="<GetNvrIndexTotal Total=\""+voList.size()+"\">\r\n";
		
		for(int i=0;i<voList.size();i++){
			SMGCardInfoVO vo =  (SMGCardInfoVO)voList.get(i);
			//东软协议存在问题：类型不匹配
			//通道查询的业务类型：IndexType（0 代表 停用，1 代表实时视频，2 轮播辅助，3 轮循测量，4 自动录像 ，5任务录像 ，6数据采集，7空闲）
			//通道设置业务类型：0 代表 停用，1 代表实时视频，2 轮播辅助，3 轮循测量，4录像 、5空闲
			int type = Integer.valueOf(vo.getIndexType());
			//停用：IndexType=\"0\" Desc=\"此通道停用\"/
			if(type==0){
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\" >\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"0\" OldDesc =\"此通道停用\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
			//实时视频
			if(type==1)
			{
				for(int j=0;j<vo.getRealvideoList().size();j++)
				{
					MonitorProgramQueryVO obj = (MonitorProgramQueryVO)vo.getRealvideoList().get(j);
					if((obj.getProgramName()=="")||(obj.getProgramName()==null))
					{
						// IndexType=\"1\" Desc=\"实时视频:播放[节目名为空]"+"\"/
						xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\">\r\n";
						xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"1\" OldDesc =\"实时视频:播放[节目名为空]\"/>\r\n";
						xml+="</GetNvrIndexStatus>\r\n";
					}
					else
					{
						//IndexType=\"1\" Desc=\"实时视频:播放"+obj.getProgramName() +"\"/
						xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\" >\r\n";
						xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"1\" OldDesc =\"实时视频:播放"+obj.getProgramName() +"\"/>\r\n";
						xml+="</GetNvrIndexStatus>\r\n";
					}
				}
			}
			//轮播辅助:IndexType=\"2\" Desc=\"此通道为轮播辅助\"/
			if(type ==2){
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\" >\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"2\" OldDesc =\"此通道为轮播辅助\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
			//轮循测量: IndexType=\"3\" Desc=\"此通道为轮循测量\"/
			if(type ==3){
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\">\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"3\" OldDesc =\"此通道为轮循测量\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
//			 <GetNvrIndexStatus  TaskState =”1”  Index="1" Status="1" >
//			<OldTask  Index="1" IndexType=“4” OldDesc="自动录像 CCTV-3 ，CCTV-4"> 
//			<AutoVideo  Index="1" VideoStatus="0"  Program="CCTV-3 "  ProgramID="457"
//			 Freq = "45700"  ServiceID="457" />    
//			<AutoVideo  Index="1"  VideoStatus="1"  Program="CCTV-4 " ProgramID="459" 
//			Freq = "45900"  ServiceID="459" />
//			</OldTask >
//			<NewTask  Index="1" IndexType=“1” NewDesc="实时视频，播放CCTV-1 " />
//			</GetNvrIndexStatus>
			//自动录制
			if(type ==4){
				String desc ="自动录像:";
				String remark="";
				for(int j=0;j<vo.getAutorecordList().size();j++)
				{
					SetAutoRecordChannelVO obj = (SetAutoRecordChannelVO)vo.getAutorecordList().get(j);
					desc+=" ";
					remark+=" ";
					if((obj.getProgramName()=="")||(obj.getProgramName()==null))
					{
						desc+="";
						remark+="";
					}
					else
					{
						desc+=obj.getProgramName();
						remark+=obj.getProgramName();
					}
				}
				if(remark.trim().equals("")){
					remark="自动录像:无录制的节目信息";
					desc=remark;
				}

				//IndexType=\"4\" Desc=\""+desc+"\"
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\" Status=\""+vo.getStatus()+"\" >\r\n";
				//<OldTask  Index="1" IndexType=“4” OldDesc="自动录像 CCTV-3 ，CCTV-4"> 
				xml+="<OldTask  Index=\""+vo.getIndex()+"\" IndexType=\"4\" OldDesc =\""+desc+"\">";
				for(int j=0;j<vo.getAutorecordList().size();j++)
				{
					SetAutoRecordChannelVO obj = (SetAutoRecordChannelVO)vo.getAutorecordList().get(j);
					//xml+="<Channel Program=\""+obj.getProgramName()+"\" ProgramID=\""+obj.getServiceID()+"\" Freq = \""+obj.getFreq()+"\"  ServiceID=\""+obj.getServiceID()+"\"/>\r\n";
					xml+="<AutoVideo Program=\""+obj.getProgramName()+"\" ProgramID=\""+obj.getServiceID()+"\" Freq = \""+obj.getFreq()+"\"  ServiceID=\""+obj.getServiceID()+"\"/>\r\n";
				}
				xml+="</OldTask>";
				xml+="</GetNvrIndexStatus>\r\n";
			}
			//任务录像
//			<GetNvrIndexStatus  TaskState =”1” Index="2" Status="1" >
//			<OldTask  Index="2" IndexType=“5” OldDesc ="任务录像，CCTV -5 "> 
//			<TaskVideo  Index="2" VideoStatus="0" TaskID= " 100" />
//			</OldTask>
//			</GetNvrIndexStatus>
			if(type==5){
				String desc ="任务录像：";
				String remark ="";
				for(int j=0;j<vo.getTaskrecordList().size();j++)
				{
					ProvisionalRecordTaskSetVO obj = (ProvisionalRecordTaskSetVO)vo.getTaskrecordList().get(j);
					desc+=" ";
					remark+=" ";
					if((obj.getProgramname()=="")||(obj.getProgramname()==null))
					{
						desc+="";
						remark+="";
					}
					else
					{
						desc+=obj.getProgramname();
						remark+=obj.getProgramname();
					}
				}
				if(remark.trim().equals("")){
					remark="任务录像：无录制的节目信息";
					desc=remark;
				}
				
				//IndexType=\"5\" Desc=\""+desc+"\"
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\">";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\" IndexType=\"5\" OldDesc =\""+desc+"\">";
				for(int j=0;j<vo.getTaskrecordList().size();j++)
				{
					ProvisionalRecordTaskSetVO obj = (ProvisionalRecordTaskSetVO)vo.getTaskrecordList().get(j);
						//xml+="<Channel TaskID= \""+ obj.getTaskID()+"\" />";
					xml+="<TaskVideo VideoStatus=\"0\" Index=\""+vo.getIndex()+"\" TaskID= \""+ obj.getTaskID()+"\" />";
				}
				xml+="</OldTask>";
				xml+="</GetNvrIndexStatus>\r\n";
			}
			//数据采集 IndexType=\"6\" Desc=\"此通道数据采集\"/
			if(type ==6){
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\" Status=\""+vo.getStatus()+"\" >\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"6\" OldDesc =\"此通道数据采集\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
			//空闲
//			 <GetNvrIndexStatus TaskState =”1”  Index="2"  Status="0" >
//			  <OldTask  Index="2"  IndexType=“1” OldDesc ="实时视频，播放CCTV-1 "/> IndexType=\"7\" Desc=\"此通道空闲\"/>
//			</GetNvrIndexStatus>
			if(type ==7){
				xml+="<GetNvrIndexStatus TaskState=\"1\" Index=\""+vo.getIndex()+"\" Status=\""+vo.getStatus()+"\"　>\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"7\" OldDesc =\"此通道空闲\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
		}
		xml += "</GetNvrIndexTotal>\r\n</ReturnInfo>\r\n</Msg>\r\n";
		return xml;

	}

//===================V2.4协议=================
	
//	<GetNvrIndexTotal  Total="16" >
//	 <GetNvrIndexStatus  TaskState =”1”  Index="1" Status="1" >
//	<OldTask  Index="1" IndexType=“4” OldDesc="自动录像 CCTV-3 ，CCTV-4"> 
//	<AutoVideo  Index="1" VideoStatus="0"  Program="CCTV-3 "  ProgramID="457"
//	 Freq = "45700"  ServiceID="457" />    
//	<AutoVideo  Index="1"  VideoStatus="1"  Program="CCTV-4 " ProgramID="459" 
//	Freq = "45900"  ServiceID="459" />
//	</OldTask >
//	<NewTask  Index="1" IndexType=“1” NewDesc="实时视频，播放CCTV-1 " />
//	</GetNvrIndexStatus>
//	<GetNvrIndexStatus  TaskState =”1” Index="2" Status="1" >
//	<OldTask  Index="2" IndexType=“5” OldDesc ="任务录像，CCTV -5 "> 
//	<TaskVideo  Index="2" VideoStatus="0" TaskID= " 100" />
//	</OldTask>
//	</GetNvrIndexStatus>
//	 <GetNvrIndexStatus TaskState =”1”  Index="2"  Status="0" >
//	  <OldTask  Index="2"  IndexType=“1” OldDesc ="实时视频，播放CCTV-1 "/>
//	</GetNvrIndexStatus>
//	< /GetNvrIndexTotal  >

	
	
	
    //根据通道号获取自动录制的节目信息
    @SuppressWarnings("unchecked")
	private List<SetAutoRecordChannelVO> GetAutoRecordVOByIndex(int index) throws DaoException
    {
    	List<SetAutoRecordChannelVO> autorecord = new ArrayList();
    	StringBuffer strBuff = new StringBuffer();
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		ResultSet rs = null;
		// 取得相关节目频点信息
		strBuff.append("select * from channelremapping where StatusFlag != 0 and devIndex = " + index );
		strBuff.append("  order by devIndex");
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(strBuff.toString());
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				SetAutoRecordChannelVO autorecordVo= new SetAutoRecordChannelVO();
				int freq = Integer.parseInt(rs.getString("freq"));
				int serverID = Integer.parseInt(rs.getString("serviceID"));
				if (freq == 0 || serverID == 0) {
					continue;
				}
				int lastflag = 0;
				try {
					lastflag = Integer.parseInt(rs.getString("StatusFlag"));
					if (lastflag == 0) {
						continue;
					}
				} catch (Exception e) {
					
				}
				String programname = rs.getString("ProgramName");
				autorecordVo.setFreq(freq);
				autorecordVo.setServiceID(serverID);
				autorecordVo.setProgramName(programname);
				autorecord.add(autorecordVo);
			}
		} catch (Exception e) {
			log.error("自动录像 取得节目相关信息错误1: " + e.getMessage());
			log.error("自动录像 取得节目相关信息错误1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
    	return autorecord;
    }
    
   //根据通道号获取自动录制的节目信息
    @SuppressWarnings({ "unused", "unchecked", "static-access" })
	private List<ProvisionalRecordTaskSetVO> GetTaskRecordVOByIndex(int index) throws DaoException
    {
    	List<ProvisionalRecordTaskSetVO> taskrecord = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
		Connection conn;
		conn = DaoSupport.getJDBCConnection();
		try {
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("select * from taskrecord where statusFlag = 1 and tr_index="+index);
			statement = conn.createStatement();
			rs = statement.executeQuery(strBuff.toString());
			while(rs.next()){
				ProvisionalRecordTaskSetVO vo = new ProvisionalRecordTaskSetVO();
				vo.setTaskID(rs.getString("Taskid"));
				vo.setFreq(Integer.parseInt(rs.getString("Freq")));
				vo.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
				
				//查询节目名
				SetAutoRecordChannelVO obj = new SetAutoRecordChannelVO();
				obj.setFreq(Integer.parseInt(rs.getString("Freq")));
				obj.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
				
				SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
				handle.getHDFlagByProgram(obj);
				vo.setProgramname(obj.getProgramName());
				taskrecord.add(vo);
			}
		} catch (Exception e) {
			log.error("任务录像查询数据库错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
    	return taskrecord;
    }
    
	@SuppressWarnings({ "unused", "unchecked", "static-access" })
	private List<MonitorProgramQueryVO> GetRealVideoVOByIndex(int index) throws DaoException
    {
    	List<MonitorProgramQueryVO> realvideo = new ArrayList();
    	MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 3);
		} catch (DaoException e1) {
			log.error("取得实时视频节目映射信息: " + e1.getMessage());
		}
		//查询节目名
		if(rtvsVO==null){
			
		}
		else
		{
			SetAutoRecordChannelVO obj = new SetAutoRecordChannelVO();
			obj.setFreq(rtvsVO.getFreq());
			obj.setServiceID(rtvsVO.getServiceID());
			
			SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
			handle.getHDFlagByProgram(obj);
			rtvsVO.setProgramName(obj.getProgramName());
	    	realvideo.add(rtvsVO);
		}
    	return realvideo;
    }
}
