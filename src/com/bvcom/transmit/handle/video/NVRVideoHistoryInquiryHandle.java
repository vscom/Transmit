package com.bvcom.transmit.handle.video;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.alarm.domain.AlarmSwitch;
import com.bvcom.transmit.parse.rec.NVRVideoHistoryInquiryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

public class NVRVideoHistoryInquiryHandle {
	private static Logger log = Logger.getLogger(NVRVideoHistoryInquiryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRVideoHistoryInquiryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }

    /**
     * TSC 生成播放列表返回给java
     * 1.解析xml得到通道index列表
     * 2.下发xml给相应的tsc
     * 3.tsc上报的xml转发给中心（回复）
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		 // 返回数据
		@SuppressWarnings("unused")
		String upString = "";
        
        Document document = null;
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        List TSCSendList = coreData.getTSCList();//tsc的列表信息
        
        
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("历史视频查看StringToXML Error: " + e.getMessage());
        }
        NVRVideoHistoryInquiryParse nvrHistory = new NVRVideoHistoryInquiryParse();
        List<ProvisionalRecordTaskSetVO> nvrHistorylist = nvrHistory.getIndexByDownXml(document);
        
		long t1 = System.currentTimeMillis();

		String startDataTime = "";
		String endDataTime = "";
		
		String url = "";
        for(int i=0; i< nvrHistorylist.size(); i++){
        	ProvisionalRecordTaskSetVO vo = nvrHistorylist.get(i);
        	
        	int index = 0;
        	
        	startDataTime = vo.getStartTime();
        	endDataTime = vo.getEndTime();
        	
        	// Del By Bian Jiang 节目信息都从节目映射表取得 2010.9.8
//        	if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_3)) {
//        		try {
//					index = getIndexByProgram(vo);
//					this.downString = this.downString.replaceAll("Index=\"0\"", "Index=\"" + index + "\"");
//				} catch (DaoException e) {
//					
//				}
//        	} else if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_0)) {
//        		// 通道映射取得当前节目信息
//        		try {
//					index = NVRVideoHistoryInquiryHandle.getIndexByProgramForChannelRemap(vo);
//					this.downString = this.downString.replaceAll("Index=\"" + vo.getIndex() + "\"", "Index=\"" + index + "\"");
//				} catch (DaoException e) {
//				}
//        	}
        	
			try {
				vo = NVRVideoHistoryInquiryHandle.getIndexByProgramForChannelRemap(vo);
			} catch (DaoException e1) {

			}
			
			/**
			 * 广州监测中心项目, TSC不需要Index号
			 * By: Bian Jiang
			 * 2011.4.7
			 */
//			this.downString = this.downString.replaceAll("Index=\"" + index + "\"", "Index=\"" + vo.getIndex() + "\"");
			
			
			//修改给tsc下发时候 判断是哪个tsc录制的就给哪个tsc下发  
        	for(int j=0;j<TSCSendList.size();j++){
        		TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(j);
    			try {
    				int tscIndex=getTscIndex(vo);
    				if(tscIndex >= tsc.getIndexMin() && tscIndex <= tsc.getIndexMax() ){
    					if(!url.equals(tsc.getURL())) {
//                      历史视频查看下发 timeout 1000*20 二十秒
    						upString = utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
    						//break;
    						url = tsc.getURL();
    						if(upString.equals("")) {
    							log.info("返回信息为空: " + tsc.getURL());
    							continue;
    						} else {
    							break;
    						}
    					}
    				}
                    
                } catch (CommonException e) {
                    log.error("下发历史视频查看到TSC出错：" + tsc.getURL());
                    upString = "";
                }
        	}
        	
        	
        }
        
        long t2 = System.currentTimeMillis();
        
      //上报回复的xml给中心
        try {
        	int javaSleepTime = 0;
        	if(upString == null || upString.equals("")) {
        		upString = utilXML.getReturnXML(bsData, 1);
        	} else {
//        		javaSleepTime = CommonUtility.NVRVideoSleepTime(startDataTime, endDataTime);
//        		log.info("--> TSC视频查看时间: " + ((t2-t1)) + " ms,  Java Sleep Time: " + (javaSleepTime/1000) + "s");
        	}
			
        	
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("历史视频查回复失败: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        utilXML = null;
        nvrHistory = null;
        
    }
	
	//获取该节目所在tsc的哪个通道
	public static int getTscIndex(ProvisionalRecordTaskSetVO vo){
		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		int tscIndex=0;
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();

			String sqlStr = "SELECT TscIndex FROM channelremapping a where Freq = "+vo.getFreq()+" and ServiceID = "+vo.getServiceID()+";";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				tscIndex=rs.getInt("TscIndex");
			}
		} catch (Exception e) {
			log.error("查找报警开关状态集合错误: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		
		return tscIndex;
	}
	
	
    public static ProvisionalRecordTaskSetVO getIndexByProgramForChannelRemap(ProvisionalRecordTaskSetVO vo) throws DaoException {

		int index = 0;
		int devIndex = 0;
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// 取得相关节目频点信息
		strBuff.append("select channelindex, DevIndex  from channelremapping where Freq = " + vo.getFreq() + " and ServiceID = " + vo.getServiceID());

		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				index = Integer.parseInt(rs.getString("channelindex"));
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				vo.setDevIndex(devIndex);
				vo.setIndex(index);
				return vo;
			}
			
		} catch (Exception e) {
			log.error("自动录像 取得节目相关通道错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		log.info("自动录像 取得节目相关通道成功! channelindex:" + vo.getIndex() + " freq:" + vo.getFreq() + " serviceID:" + vo.getServiceID());
		return vo;
	}
    
	public static int getIndexByProgram(ProvisionalRecordTaskSetVO vo) throws DaoException  {

		int index = 0;
		StringBuffer strBuff = new StringBuffer();
		
		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		// TODO 可能出现多个频点出现同一个节目
		strBuff.append("select channelindex from channelprogramstatus where Freq = " + vo.getFreq() + " and  ");
		strBuff.append("ServiceID = " + vo.getServiceID());
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				index = Integer.parseInt(rs.getString("channelindex"));
			}
			
			if(index == 0) {
				strBuff = new StringBuffer();
				strBuff.append("select channelindex from channelprogramstatus where channelFlag = 0 ");
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					index = Integer.parseInt(rs.getString("channelindex"));
					break;
				}
			}
			
		} catch (Exception e) {
			log.error("自动录像查询通道数据库错误: " + e.getMessage());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		strBuff = null;
		log.info("自动录像查询通道数据库成功!");
		
		vo.setIndex(index);
		return index;
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
