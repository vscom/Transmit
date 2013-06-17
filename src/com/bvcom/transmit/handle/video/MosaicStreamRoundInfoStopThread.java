package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.bvcom.transmit.config.AutoAnalysisTimeQueryConfigFile;
import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

public class MosaicStreamRoundInfoStopThread extends Thread {
	private static Logger log = Logger.getLogger(MosaicStreamRoundInfoStopThread.class.getSimpleName());
	private UtilXML utilXML = new UtilXML();
	private int year,month,day,hourOfDay, minute, second; 
	public MosaicStreamRoundInfoStopThread(){
		//getStartTime();
	}
	
	private void getStartTime() {
    	
        AutoAnalysisTimeQueryConfigFile AutoAnalysisTimeQueryConfigFile = new AutoAnalysisTimeQueryConfigFile();
        try {
	        String startTime = AutoAnalysisTimeQueryConfigFile.getStreamRoundInfoQueryStopTime();
	        
	        String[] timeArray = startTime.split("-");
	        this.year =Integer.valueOf(timeArray[0]); 
	        this.month =Integer.valueOf(timeArray[1]); 
	        this.day =Integer.valueOf(timeArray[2]); 
	        this.hourOfDay =Integer.valueOf(timeArray[3]); 
	        this.minute = Integer.valueOf(timeArray[4]); 
	        this.second = Integer.valueOf(timeArray[5]); 
        } catch (Exception ex) {
        	this.year = 2000; 
        	this.month = 1; 
        	this.day = 1; 
	        this.hourOfDay = 1; 
	        this.minute = 0; 
	        this.second = 0; 
        }
    }
	
	@Override
	/**
    * TODO �����˹���ɾ����صĽ�Ŀ��Ϣ 
    * Ŀǰֻ�����忨�·�Del��� 
    * ��Ҫɾ��recordType = 4�Ľ�Ŀ
    * ����Ҫ������Щ��Ϣ�·�Set����
    * By: Bian Jiang 2012.3.21
	 */
	public void run() {
		//log.info("\n\n    \t------- ���������������ֲ� ֹͣ�߳�: " +year+"-"+month+"-"+day+" "+ hourOfDay + ":" + minute + ":" + second + " ---------\n");
		//AutoAnalysisTimeQueryConfigFile autoAnalysisTimeOueryConfigFile = new AutoAnalysisTimeQueryConfigFile();
		//String StopTime = autoAnalysisTimeOueryConfigFile.getStreamRoundInfoQueryStopTime();
		//String[] strDate = StopTime.split("-");
		//Date stopDate = new Date(Integer.parseInt(strDate[0]) - 1900, Integer.parseInt(strDate[1]) - 1, Integer.parseInt(strDate[2]), Integer.parseInt(strDate[3]), Integer.parseInt(strDate[4]), Integer.parseInt(strDate[5]));
		//if (stopDate.after(new Date())) {
			//log.info("------- ��ʼ�����������ֲ� ֹͣ����: " + year+"-"+month+"-"+day+" "+ hourOfDay + ":" + minute + ":" + second + " ---------");
			
			// ȡ����������صĽ�Ŀ��Ϣ
			List<SetAutoRecordChannelVO> voList = getProgramInfoByIndex();
			
			String sendString=createForDownXML(voList);
			List<SMGCardInfoVO> smgList=new ArrayList<SMGCardInfoVO>();
			for(int i=0;i<voList.size();i++){
				CommonUtility.checkSMGChannelIndex(voList.get(i).getDevIndex(), smgList);
			}
			
			//��忨�����Զ�¼������
			for(int i=0;i<smgList.size();i++){
				utilXML.SendUpXML(sendString, smgList.get(i).getURL());
			}
			//ͬʱ����һ��һ��Ŀ����recordtype=4 �޸�Ϊ��ʼ״̬
			//BY TQY �������
			updateMosaicChannelMapping(voList);
			
		//}
	}

	public String createForDownXML(List<SetAutoRecordChannelVO> AutoRecordlist){
		StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"2.3\" MsgID=\"2\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + MemCoreData.getInstance().getSysVO().getSrcCode()
                + "\" DstCode=\"" + MemCoreData.getInstance().getSysVO().getDstCode() + "\" SrcURL=\"" + MemCoreData.getInstance().getSysVO().getCenterToAgentURL() + "\" Priority=\"1\">\r\n");
        
        strBuff.append("<SetAutoRecordChannel>\r\n");
        strBuff.append("   <Channel Action=\"Del\">\r\n");
        
        for(int i=0; i< AutoRecordlist.size(); i++) 
        {
        	
        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
        	if (vo.getFreq() == 0 ) {
        		continue;
        	}
        	
        	strBuff.append("		<ChCode Index=\"" + vo.getDevIndex() + "\" Freq=\"" + vo.getFreq() + "\" SymbolRate=\""+ vo.getSymbolRate() +"\" ");
        	strBuff.append(" QAM=\"QAM" + vo.getQAM() + "\" ServiceID=\""+ vo.getServiceID() +"\" VideoPID=\""+vo.getVideoPID()+"\"");
        	strBuff.append(" AudioPID=\""+vo.getAudioPID() + "\" />\r\n");
            
        }
        strBuff.append("   </Channel>\r\n");
        strBuff.append("</SetAutoRecordChannel>\r\n");
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
	}

	
	/*
	 *������������ؽ�Ŀ��Ϣ������һ��һ��Ŀ��
	 * 
	 */
	public void updateMosaicChannelMapping(List<SetAutoRecordChannelVO>  voList)
	{
		for(int i =0;i<voList.size();i++)
		{
			@SuppressWarnings("unused")
			SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)voList.get(i);
			try {
				this.delChannelRemappingIndex(vo);
			} catch (DaoException e) {
				e.printStackTrace();
			}
		}
	}
	
	  public  void delChannelRemappingIndex(SetAutoRecordChannelVO vo) throws DaoException {

			StringBuffer strBuff = new StringBuffer();
			
			Statement statement = null;
			Connection conn = DaoSupport.getJDBCConnection();

			// ȡ����ؽ�ĿƵ����Ϣ
			
			strBuff.append("update transmit.channelremapping c set ");
			strBuff.append(" StatusFlag = 0, tscIndex = 0, freq=0, ServiceID=0, VideoPID=0, AudioPID=0, ");
			strBuff.append(" DownIndex = 0, udp='', port=0, smgURL='', HDFlag=0, ProgramName='', ");
			strBuff.append(" lasttime = '" + CommonUtility.getDateTime() + "' ");
			strBuff.append(" where channelindex = " + vo.getIndex());
			strBuff.append(" and devIndex = " + vo.getDevIndex());
			
			try {
				statement = conn.createStatement();
				
				statement.execute(strBuff.toString());
				
			} catch (Exception e) {
				log.error("�Զ�¼�� ����ͨ��ӳ������: " + e.getMessage());
			} finally {
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
			strBuff = null;
			//log.info("�Զ�¼�� ����ͨ��ӳ���ɹ�! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		}
	/**
	 * ȡ����������صĽ�Ŀ��Ϣ
	 * recordType 0����¼��1:������ϴ���¼��   2��24Сʱ¼��(Ĭ��)	3: ����¼��  4: �����˺ϳ��ֲ�
	 * @return
	 */
	public List<SetAutoRecordChannelVO> getProgramInfoByIndex() {

		List<SetAutoRecordChannelVO> voList = new ArrayList<SetAutoRecordChannelVO>();

		Statement statement = null;
		Connection conn = null;

		ResultSet rs = null;
		try {
			conn = DaoSupport.getJDBCConnection();
			StringBuffer strBuff = new StringBuffer();
			// ȡ����ؽ�ĿƵ����Ϣ
			strBuff.append("select *  from channelremapping where  RecordType = 4 ;");
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff.toString());
				while (rs.next()) {
					int freq = Integer.parseInt(rs.getString("freq"));
					int serverID = Integer.parseInt(rs.getString("serviceID"));
					if (freq == 0 || serverID == 0) {
						continue;
					}
					SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
					vo.setServiceID(serverID);
					vo.setFreq(freq);
					vo.setIndex(Integer.parseInt(rs.getString("channelindex")));
					vo.setTscIndex(Integer.parseInt(rs.getString("TscIndex")));
					vo.setSymbolRate(Integer.parseInt(rs.getString("symbolrate")));
					vo.setQAM(Integer.parseInt(rs.getString("qam")));
					vo.setVideoPID(Integer.parseInt(rs.getString("videoPID")));
					vo.setAudioPID(Integer.parseInt(rs.getString("audioPID")));
					vo.setAction(rs.getString("Action"));
					vo.setRecordType(Integer.parseInt(rs.getString("RecordType")));
					vo.setDevIndex(Integer.parseInt(rs.getString("DevIndex")));
					voList.add(vo);
				}

			} catch (Exception e) {
				log.error("�Զ�¼�� ȡ�ý�Ŀ���ͨ������: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			strBuff = null;
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return voList;
	}
}



