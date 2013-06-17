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
     * 1. ��Э�����η��͸�TSC��RTVM
     * 2. �ȴ���������TSC��RTVM�ķ�����Ϣ
     * 3. ��ѯepg.xml�Ƿ�����
     * 4. ����忨״̬��Ϣ��Э��
     * 5. ��״̬Э�鷵�ظ�����
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
		//���͵�Rtvm
		MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
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
			log.error("ȡ��RTVM��URL����: " + e1.getMessage());
			isErr=false;
		}
		isErr=utilXML.SendUpXML(downString, rtvsVO.getRTVSResetURL());	
	*/
	    //��SMG_CARD_INFO
	    
	    //����1����ͨ��״̬���л�ȡͨ������TOTAL��ͨ��ֵINDEX
		//����2����ͨ��ӳ������ҵ�ͨ����Ӧ���Զ�¼�ƽ�Ŀ��Ϣ������SERVICEID,FREQ��ȡ��Ŀ��
	    //����3��������¼������ҵ�ͨ����Ӧ�������Ŀ������SERVICEID,FREQ��ȡ��Ŀ��
	    //����4����һ��һ���ӱ����ҵ��ֶ�ѡ��Ľ�Ŀ��Ϣ������SERVICEID,FREQ��ȡ��Ŀ��
    
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
					//����ͨ���Ż�ȡ�����Զ�¼�ƵĽ�Ŀ��Ϣ
					smginfo.setAutorecordList(GetAutoRecordVOByIndex(index));
					//����ͨ���Ż�ȡ��������¼�ƵĽ�Ŀ��Ϣ
					smginfo.setTaskrecordList(GetTaskRecordVOByIndex(index));
					//����ͨ���Ż�ȡ������Ƶ�Ľ�Ŀ��Ϣ
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
		//�ϱ���Ϣ��ƽ̨
		String upString = "";
		// �ϱ��ظ���xml������,�Լ����سɹ�
		upString = ReturnXMLByURL(this.bsData, NvrStatusList);
		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("ͨ��״̬�ظ�ʧ��: " + e.getMessage());
		}

		bsData = null;
		downString = null;
		utilXML = null;
		
    }
    
    
	// ͨ��״̬��ѯ�ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, List voList) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">\r\n";
		xml += "<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n";
		xml +="<ReturnInfo>\r\n";
		xml +="<GetNvrIndexTotal Total=\""+voList.size()+"\">\r\n";
		
		for(int i=0;i<voList.size();i++){
			SMGCardInfoVO vo =  (SMGCardInfoVO)voList.get(i);
			//����Э��������⣺���Ͳ�ƥ��
			//ͨ����ѯ��ҵ�����ͣ�IndexType��0 ���� ͣ�ã�1 ����ʵʱ��Ƶ��2 �ֲ�������3 ��ѭ������4 �Զ�¼�� ��5����¼�� ��6���ݲɼ���7���У�
			//ͨ������ҵ�����ͣ�0 ���� ͣ�ã�1 ����ʵʱ��Ƶ��2 �ֲ�������3 ��ѭ������4¼�� ��5����
			int type = Integer.valueOf(vo.getIndexType());
			//ͣ�ã�IndexType=\"0\" Desc=\"��ͨ��ͣ��\"/
			if(type==0){
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\" >\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"0\" OldDesc =\"��ͨ��ͣ��\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
			//ʵʱ��Ƶ
			if(type==1)
			{
				for(int j=0;j<vo.getRealvideoList().size();j++)
				{
					MonitorProgramQueryVO obj = (MonitorProgramQueryVO)vo.getRealvideoList().get(j);
					if((obj.getProgramName()=="")||(obj.getProgramName()==null))
					{
						// IndexType=\"1\" Desc=\"ʵʱ��Ƶ:����[��Ŀ��Ϊ��]"+"\"/
						xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\">\r\n";
						xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"1\" OldDesc =\"ʵʱ��Ƶ:����[��Ŀ��Ϊ��]\"/>\r\n";
						xml+="</GetNvrIndexStatus>\r\n";
					}
					else
					{
						//IndexType=\"1\" Desc=\"ʵʱ��Ƶ:����"+obj.getProgramName() +"\"/
						xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\" >\r\n";
						xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"1\" OldDesc =\"ʵʱ��Ƶ:����"+obj.getProgramName() +"\"/>\r\n";
						xml+="</GetNvrIndexStatus>\r\n";
					}
				}
			}
			//�ֲ�����:IndexType=\"2\" Desc=\"��ͨ��Ϊ�ֲ�����\"/
			if(type ==2){
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\" >\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"2\" OldDesc =\"��ͨ��Ϊ�ֲ�����\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
			//��ѭ����: IndexType=\"3\" Desc=\"��ͨ��Ϊ��ѭ����\"/
			if(type ==3){
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\">\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"3\" OldDesc =\"��ͨ��Ϊ��ѭ����\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
//			 <GetNvrIndexStatus  TaskState =��1��  Index="1" Status="1" >
//			<OldTask  Index="1" IndexType=��4�� OldDesc="�Զ�¼�� CCTV-3 ��CCTV-4"> 
//			<AutoVideo  Index="1" VideoStatus="0"  Program="CCTV-3 "  ProgramID="457"
//			 Freq = "45700"  ServiceID="457" />    
//			<AutoVideo  Index="1"  VideoStatus="1"  Program="CCTV-4 " ProgramID="459" 
//			Freq = "45900"  ServiceID="459" />
//			</OldTask >
//			<NewTask  Index="1" IndexType=��1�� NewDesc="ʵʱ��Ƶ������CCTV-1 " />
//			</GetNvrIndexStatus>
			//�Զ�¼��
			if(type ==4){
				String desc ="�Զ�¼��:";
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
					remark="�Զ�¼��:��¼�ƵĽ�Ŀ��Ϣ";
					desc=remark;
				}

				//IndexType=\"4\" Desc=\""+desc+"\"
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\" Status=\""+vo.getStatus()+"\" >\r\n";
				//<OldTask  Index="1" IndexType=��4�� OldDesc="�Զ�¼�� CCTV-3 ��CCTV-4"> 
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
			//����¼��
//			<GetNvrIndexStatus  TaskState =��1�� Index="2" Status="1" >
//			<OldTask  Index="2" IndexType=��5�� OldDesc ="����¼��CCTV -5 "> 
//			<TaskVideo  Index="2" VideoStatus="0" TaskID= " 100" />
//			</OldTask>
//			</GetNvrIndexStatus>
			if(type==5){
				String desc ="����¼��";
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
					remark="����¼����¼�ƵĽ�Ŀ��Ϣ";
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
			//���ݲɼ� IndexType=\"6\" Desc=\"��ͨ�����ݲɼ�\"/
			if(type ==6){
				xml+="<GetNvrIndexStatus TaskState=\"1\"  Index=\""+vo.getIndex()+"\" Status=\""+vo.getStatus()+"\" >\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"6\" OldDesc =\"��ͨ�����ݲɼ�\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
			//����
//			 <GetNvrIndexStatus TaskState =��1��  Index="2"  Status="0" >
//			  <OldTask  Index="2"  IndexType=��1�� OldDesc ="ʵʱ��Ƶ������CCTV-1 "/> IndexType=\"7\" Desc=\"��ͨ������\"/>
//			</GetNvrIndexStatus>
			if(type ==7){
				xml+="<GetNvrIndexStatus TaskState=\"1\" Index=\""+vo.getIndex()+"\" Status=\""+vo.getStatus()+"\"��>\r\n";
				xml+="<OldTask  Index=\""+vo.getIndex()+"\"  IndexType=\"7\" OldDesc =\"��ͨ������\"/>\r\n";
				xml+="</GetNvrIndexStatus>\r\n";
			}
		}
		xml += "</GetNvrIndexTotal>\r\n</ReturnInfo>\r\n</Msg>\r\n";
		return xml;

	}

//===================V2.4Э��=================
	
//	<GetNvrIndexTotal  Total="16" >
//	 <GetNvrIndexStatus  TaskState =��1��  Index="1" Status="1" >
//	<OldTask  Index="1" IndexType=��4�� OldDesc="�Զ�¼�� CCTV-3 ��CCTV-4"> 
//	<AutoVideo  Index="1" VideoStatus="0"  Program="CCTV-3 "  ProgramID="457"
//	 Freq = "45700"  ServiceID="457" />    
//	<AutoVideo  Index="1"  VideoStatus="1"  Program="CCTV-4 " ProgramID="459" 
//	Freq = "45900"  ServiceID="459" />
//	</OldTask >
//	<NewTask  Index="1" IndexType=��1�� NewDesc="ʵʱ��Ƶ������CCTV-1 " />
//	</GetNvrIndexStatus>
//	<GetNvrIndexStatus  TaskState =��1�� Index="2" Status="1" >
//	<OldTask  Index="2" IndexType=��5�� OldDesc ="����¼��CCTV -5 "> 
//	<TaskVideo  Index="2" VideoStatus="0" TaskID= " 100" />
//	</OldTask>
//	</GetNvrIndexStatus>
//	 <GetNvrIndexStatus TaskState =��1��  Index="2"  Status="0" >
//	  <OldTask  Index="2"  IndexType=��1�� OldDesc ="ʵʱ��Ƶ������CCTV-1 "/>
//	</GetNvrIndexStatus>
//	< /GetNvrIndexTotal  >

	
	
	
    //����ͨ���Ż�ȡ�Զ�¼�ƵĽ�Ŀ��Ϣ
    @SuppressWarnings("unchecked")
	private List<SetAutoRecordChannelVO> GetAutoRecordVOByIndex(int index) throws DaoException
    {
    	List<SetAutoRecordChannelVO> autorecord = new ArrayList();
    	StringBuffer strBuff = new StringBuffer();
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		ResultSet rs = null;
		// ȡ����ؽ�ĿƵ����Ϣ
		strBuff.append("select * from channelremapping where StatusFlag != 0 and devIndex = " + index );
		strBuff.append("  order by devIndex");
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(strBuff.toString());
			// ���д�����صĽ�Ŀ������ص��豸ͨ��
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
			log.error("�Զ�¼�� ȡ�ý�Ŀ�����Ϣ����1: " + e.getMessage());
			log.error("�Զ�¼�� ȡ�ý�Ŀ�����Ϣ����1 SQL: " + strBuff.toString());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
    	return autorecord;
    }
    
   //����ͨ���Ż�ȡ�Զ�¼�ƵĽ�Ŀ��Ϣ
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
				
				//��ѯ��Ŀ��
				SetAutoRecordChannelVO obj = new SetAutoRecordChannelVO();
				obj.setFreq(Integer.parseInt(rs.getString("Freq")));
				obj.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
				
				SetAutoRecordChannelHandle handle = new SetAutoRecordChannelHandle();
				handle.getHDFlagByProgram(obj);
				vo.setProgramname(obj.getProgramName());
				taskrecord.add(vo);
			}
		} catch (Exception e) {
			log.error("����¼���ѯ���ݿ����: " + e.getMessage());
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
        // 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 3);
		} catch (DaoException e1) {
			log.error("ȡ��ʵʱ��Ƶ��Ŀӳ����Ϣ: " + e1.getMessage());
		}
		//��ѯ��Ŀ��
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
