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
    * TODO 马赛克过期删除相关的节目信息 
    * 目前只给给板卡下发Del命令， 
    * 需要删除recordType = 4的节目
    * 还需要更具这些信息下发Set命令
    * By: Bian Jiang 2012.3.21
	 */
	public void run() {
		//log.info("\n\n    \t------- 启动启动马赛克轮播 停止线程: " +year+"-"+month+"-"+day+" "+ hourOfDay + ":" + minute + ":" + second + " ---------\n");
		//AutoAnalysisTimeQueryConfigFile autoAnalysisTimeOueryConfigFile = new AutoAnalysisTimeQueryConfigFile();
		//String StopTime = autoAnalysisTimeOueryConfigFile.getStreamRoundInfoQueryStopTime();
		//String[] strDate = StopTime.split("-");
		//Date stopDate = new Date(Integer.parseInt(strDate[0]) - 1900, Integer.parseInt(strDate[1]) - 1, Integer.parseInt(strDate[2]), Integer.parseInt(strDate[3]), Integer.parseInt(strDate[4]), Integer.parseInt(strDate[5]));
		//if (stopDate.after(new Date())) {
			//log.info("------- 开始启动马赛克轮播 停止任务: " + year+"-"+month+"-"+day+" "+ hourOfDay + ":" + minute + ":" + second + " ---------");
			
			// 取得马赛克相关的节目信息
			List<SetAutoRecordChannelVO> voList = getProgramInfoByIndex();
			
			String sendString=createForDownXML(voList);
			List<SMGCardInfoVO> smgList=new ArrayList<SMGCardInfoVO>();
			for(int i=0;i<voList.size();i++){
				CommonUtility.checkSMGChannelIndex(voList.get(i).getDevIndex(), smgList);
			}
			
			//向板卡发送自动录像设置
			for(int i=0;i<smgList.size();i++){
				utilXML.SendUpXML(sendString, smgList.get(i).getURL());
			}
			//同时更新一对一节目表，将recordtype=4 修改为初始状态
			//BY TQY 监测四期
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
	 *根据马赛克相关节目信息，更新一对一节目表
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

			// 取得相关节目频点信息
			
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
				log.error("自动录像 更新通道映射表错误: " + e.getMessage());
			} finally {
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
			strBuff = null;
			//log.info("自动录像 更新通道映射表成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		}
	/**
	 * 取得马赛克相关的节目信息
	 * recordType 0：不录像，1:代表故障触发录制   2：24小时录像(默认)	3: 任务录像  4: 马赛克合成轮播
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
			// 取得相关节目频点信息
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
				log.error("自动录像 取得节目相关通道错误: " + e.getMessage());
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



