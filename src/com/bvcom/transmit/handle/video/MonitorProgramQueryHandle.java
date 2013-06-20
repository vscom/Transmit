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
import com.bvcom.transmit.parse.video.MonitorProgramQueryParse;
import com.bvcom.transmit.parse.video.RTVSResetConfigParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

/**
 * 实时视频监看
 * @author Bian Jiang
 *
 */
public class MonitorProgramQueryHandle {

    private static Logger log = Logger.getLogger(MonitorProgramQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public MonitorProgramQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    /**
     * 1. 解析实时视频监看协议
     * 2. 取得没有分配的rtvsIndex, 如果都分配了就通过时间排序取得最久一次
     * 2. 通过Index来判断是否给IPM发送指令
     *    a. Index为空发送数据给SMG, 
     *    b. 如果Index有值，先相关的rtvsIndex给SMG发送停止命令，再发送给IPM相关数据。
     * 3. 返回rtvsURL给中心
     */
    public void downXML() {
		String upString = "";
		String smgDownString = "";
		int isErrFlag = 0;
        SysInfoVO sysInfoVO = coreData.getSysVO();
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("实时视频监看StringToXML Error: " + e.getMessage());
        }
        MonitorProgramQueryParse monitorProgramQuery = new MonitorProgramQueryParse();
        List<MonitorProgramQueryVO> MonitorProgramList = monitorProgramQuery.getDownXml(document);
        
		String rtvsURL = "";
		
		RTVSResetConfigParse RTVSReset = null;
		
		int isFlag = 0; // 1: 成功 0:失败
		
		for(int i=0; i<MonitorProgramList.size(); i++) {
			MonitorProgramQueryVO vo = (MonitorProgramQueryVO)MonitorProgramList.get(i);
		
			// RunType 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
			int runType = 0;
			
			if(vo.getIndex() > 0) {
				runType = 3;
			} else {
				runType = 2;
			}
			
			if(vo.getIndex() > 0) {
				// 分组轮播监视
				try {
					vo = GetProgramPatrolInfo(vo);
				} catch (DaoException e) {
					log.error("取得轮询监视组播地址和端口号: " + e.getMessage());
				}
				
			} else {
				int priority = 0;
				try {
					priority = Integer.valueOf(this.bsData.getPriority());
				} catch (Exception ex) {
					log.error("一对一监视取得优先级错误: " + ex.getMessage());
				}
				// 一对一监视
		        try {
		        	SetAutoRecordChannelVO SetAutoRecordChannelVO = new SetAutoRecordChannelVO();
		        	// 判断是否存在一对一监测 
		        	SetAutoRecordChannelVO.setFreq(vo.getFreq());
		        	SetAutoRecordChannelVO.setServiceID(vo.getServiceID());
		        	isFlag = SetAutoRecordChannelHandle.isHaveProgramInRemapping(SetAutoRecordChannelVO);
		        	
		        	if(isFlag == 1) {
			        	vo = GetProgramInfoByDownIndex(vo, this.downString);
			        	vo.setRtvsIP(SetAutoRecordChannelVO.getUdp());
			        	vo.setRtvsPort(SetAutoRecordChannelVO.getPort());
		        	}
					// 0:空闲 1:一对一监视多画面 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面(马赛克) 6: 一对一监测的手动选台
		        	// 优先级 1是多画面，2是手动选台。
					if (priority == 1) {
						// 一对一监测多画面
						vo.setStatusFlag(1);
						log.info("一对一监测多画面优先级: " + priority);
					} else if (priority == 2){
						// 手动选台中的一对一监测
						vo.setStatusFlag(6);
						log.info("一对一监监视优先级: " + priority);
					}
				} catch (DaoException e) {
					log.error("实时视频监看 取得实时视频序号错误: " + e.getMessage());
				}

			}
			
	        // RTVS修改输入流的IP和端口
	        RTVSReset = new RTVSResetConfigParse();

	        String rtvsString = RTVSReset.createForDownXML(bsData, vo);
	        
	        try {
	        	upString = utilXML.SendDownXML(rtvsString, vo.getRTVSResetURL(), CommonUtility.CHANGE_PROGRAM_QUERY, bsData);
	        	isFlag = 1;
	        } catch (Exception e) {
	            log.error("下发RTVS修改输入流的IP和端口出错：" + vo.getRTVSResetURL());
	            isFlag = 0;
	        }
			// TODO 目前只考虑了下发一条指令
			break;
		}
		
		if(isFlag == 1) {
	        try {
	            document = utilXML.StringToXML(upString);
	            rtvsURL = RTVSReset.getReturnURL(document);
	        } catch (CommonException e) {
	            log.error("视频URL StringToXML Error: " + e.getMessage());
	            upString = "";
	        }
		}
		
		if(!upString.equals("") && isErrFlag == 0 && !rtvsURL.equals("") && isFlag==1) {
			upString = monitorProgramQuery.getReturnXML(this.bsData, 0, rtvsURL);
		} else {
			upString = monitorProgramQuery.getReturnXML(this.bsData, 1, rtvsURL);
		}
        
        try {
            // 等待一秒钟，让SMG打出流，防止RTVS在没有接到流就重启。
        	Thread.sleep(CommonUtility.VIDEO_CHANGE_SLEEPTIME);
            utilXML.SendUpXML(upString, bsData);
        } catch (Exception e) {
            log.error("实时视频监看下发上报信息失败: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        utilXML = null;
        upString = null;
        monitorProgramQuery = null;
        MonitorProgramList = null;
    }
    
//    private  MonitorProgramQueryVO GetProgramInfoByDownIndex(MonitorProgramQueryVO vo) throws DaoException {
//
//		Statement statement = null;
//		Connection conn = DaoSupport.getJDBCConnection();
//
//		ResultSet rs = null;
//			
//		// RunType 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
//		int runType = 0;
//		
//		if(vo.getIndex() > 0) {
//			runType = 3;
//		} else {
//			runType = 2;
//		}
//		
//		StringBuffer strBuff = new StringBuffer();
//		
//		int isFlag = 0;
//		
//		if(vo.getFreq() != 0) {
//			strBuff.append("select * from monitorprogramquery where statusFlag = 1 and Freq= " + vo.getFreq() + " and ServiceID=" + vo.getServiceID() + "   order by lastDatatime");
//			try {
//				statement = conn.createStatement();
//				
//				rs = statement.executeQuery(strBuff.toString());
//				
//				while(rs.next()){
//					vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
//					
//					vo.setRtvsIP(rs.getString("rtvsIP"));
//					
//					vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
//					
//					vo.setSmgURL(rs.getString("smgURL"));
//					
//					vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
//					
//					vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
//					
//					vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
//					isFlag = 1;
//					break;
//				}
//				
//			} catch (Exception e) {
//				log.error("实时视频监看 取得实时视频序号错误: " + e.getMessage());
//				isFlag = 0;
//			} finally {
//				DaoSupport.close(rs);
//				DaoSupport.close(statement);
//		
//			}
//			strBuff = null;
//		}
//		
//		if (isFlag == 0) {
//			// 没有空闲的RTVS, 根据时间取得时间最久的一次
//			strBuff = new StringBuffer();
//			strBuff.append("select *  from monitorprogramquery where statusFlag = 1  order by lastDatatime");
//			try {
//				statement = conn.createStatement();
//				
//				rs = statement.executeQuery(strBuff.toString());
//				
//				while(rs.next()){
//					vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
//					
//					vo.setRtvsIP(rs.getString("rtvsIP"));
//					
//					vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
//					
//					vo.setSmgURL(rs.getString("smgURL"));
//					
//					vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
//					
//					vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
//					
//					vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
//					break;
//				}
//				
//			} catch (Exception e) {
//				log.error("实时视频监看 取得实时视频序号错误: " + e.getMessage());
//			} finally {
//				DaoSupport.close(rs);
//				DaoSupport.close(statement);
//		
//			}
//		}
//
//		strBuff = null;
//		
//		// 跟新已经分配的rtvsIndex Flag
//		strBuff = new StringBuffer();
//		
//		String rtvsIP = "";
//		int rtvsPort = 0;
//		
//		if(vo.getFreq() != 0 && vo.getServiceID() != 0) {
//			rtvsIP = "239.0.0." + (vo.getFreq()/4000);
//			rtvsPort = vo.getServiceID() + 2000;
//			vo.setRtvsIP(rtvsIP);
//			vo.setRtvsPort(rtvsPort);
//		}
//		
//		// update transmit.monitorprogramquery c set statusFlag = 1, lastDatatime = '2009-08-17 15:30:00' where rtvsIndex = 1
//		strBuff.append("update monitorprogramquery c set ");
//		strBuff.append(" xml = '" + downString + "', ");
//		// RunType 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
//		strBuff.append(" RunType = " + runType + ", ");
//		strBuff.append(" Freq = " + vo.getFreq() + ", ");
//		strBuff.append(" ServiceID = " + vo.getServiceID() + ", ");
//		strBuff.append(" rtvsIP = '" + vo.getRtvsIP() + "', ");
//		strBuff.append(" rtvsPort = " + vo.getRtvsPort() + ", ");
//		strBuff.append(" lastDatatime = '" + CommonUtility.getDateTime() + "' ");
//		strBuff.append(" where rtvsIndex = " + vo.getRtvsIndex());
//		
//		try {
//			statement = conn.createStatement();
//			
//			statement.execute(strBuff.toString());
//			
//		} catch (Exception e) {
//			log.error("实时视频监看 更新实时视频序号状态错误: " + e.getMessage());
//			log.error("错误SQL: " + strBuff.toString());
//		} finally {
//			DaoSupport.close(statement);
//			DaoSupport.close(conn);
//		}
//		strBuff = null;
//		return vo;
//	}
    
    /**
     * 取得实时视频URL相关数据
     * @param MonitorProgramQueryVO
     * @param downXML 中心下发的XML协议
     * @param runType 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
     * @return MonitorProgramQueryVO
     * @throws DaoException
     */
    public static MonitorProgramQueryVO GetProgramInfoByDownIndex(MonitorProgramQueryVO vo, String downXML) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// 查找是否已经存在一对一监视
		strBuff.append("select * from monitorprogramquery where statusFlag = 1 order by lastDatatime");
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				try {
					vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				} catch (Exception ex) {
					vo.setRtvsPort(0);
				}
				vo.setSmgURL(rs.getString("smgURL"));
				
				try {
					vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				} catch (Exception ex) {
					vo.setSmgIndex(0);
				}
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				vo.setIsReStartRTVS(0);
				break;
			}
			
		} catch (Exception e) {
			log.error("实时视频监看 取得实时视频序号错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);		
		}
		strBuff = null;

		return vo;
	}
    
    /**
     *  取得实时视频组播地址和端口号
     * @param vo
     * @return
     * @throws DaoException
     */
//    public static MonitorProgramQueryVO GetRTVSIpPort(MonitorProgramQueryVO vo) throws DaoException {
//
//		Statement statement = null;
//		Connection conn = DaoSupport.getJDBCConnection();
//
//		ResultSet rs = null;
//		
//		StringBuffer strBuff = new StringBuffer();
//		
//		// 取得相关节目频点信息
//		strBuff.append("select *  from monitorprogramquery where RunType = 1 order by lastDatatime");
//		
//		try {
//			statement = conn.createStatement();
//			
//			rs = statement.executeQuery(strBuff.toString());
//			
//			while(rs.next()){
//				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
//				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
//				
//				vo.setRtvsIP(rs.getString("rtvsIP"));
//				
//				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
//				
//				vo.setSmgURL(rs.getString("smgURL"));
//				
//				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
//				
//				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
//				
//				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
//				
//				break;
//			}
//			
//		} catch (Exception e) {
//			log.error("实时视频监看 取得实时视频序号错误: " + e.getMessage());
//		} finally {
//			DaoSupport.close(rs);
//			DaoSupport.close(statement);
//			DaoSupport.close(conn);
//		}
//		return vo;
//	}
    
    
    public static void updataProgramPatrolGroup(List<MonitorProgramQueryVO> programPatrolList, String upXMLString) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		int count = 0;
		upXMLString = upXMLString.replaceAll("成功", "");
		
		StringBuffer strBuff = null;
		// 取得相关节目频点信息
		
		for(int i=0; i<programPatrolList.size(); i++) {
			MonitorProgramQueryVO vo = programPatrolList.get(i);
			strBuff = new StringBuffer();
			// 取得相关节目频点信息
			strBuff.append("select *  from monitorprogramquery where statusFlag = 2 and patrolGroupIndex=" + vo.getPatrolGroupIndex() + "  order by lastDatatime");
			
			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
					break;
				}
				
			} catch (Exception e) {
				log.error("实时视频监看 取得实时视频序号错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
			strBuff = null;
			
			try {
				strBuff = new StringBuffer();
				strBuff.append("update monitorprogramquery c set ");
				strBuff.append(" xml = '" + upXMLString + "', ");
				// RunType 1:手动选台 2:一对一监测 3:轮询监测 4:轮播
				strBuff.append(" RunType = 3, ");
				strBuff.append(" Freq = " + vo.getFreq() + ", ");
				strBuff.append(" ServiceID = " + vo.getServiceID() + ", ");
				strBuff.append(" lastDatatime = '" + CommonUtility.getDateTime() + "' ");
				strBuff.append(" where rtvsIndex = " + vo.getRtvsIndex());
			
				statement = conn.createStatement();
				
				statement.execute(strBuff.toString());
				
			} catch (Exception e) {
				log.error("实时视频监看 更新实时视频序号状态错误: " + e.getMessage());
				log.error("错误SQL: " + strBuff.toString());
				e.printStackTrace();
			} finally {
				DaoSupport.close(statement);
			}
			strBuff = null;
			if ((count-1) == i) {
				break;
			}
		}
		
		DaoSupport.close(conn);
		return;
	}
    
    /**
     *  取得轮询监视组播地址和端口号
     * @param vo
     * @return
     * @throws DaoException
     */
    public static MonitorProgramQueryVO GetProgramPatrolInfo(MonitorProgramQueryVO vo) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// 取得轮询监视信息
		strBuff.append("select *  from monitorprogramquery where statusFlag = 2 and patrolGroupIndex= " + vo.getPatrolGroupIndex() + " order by lastDatatime");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				
				vo.setSmgURL(rs.getString("smgURL"));
				
				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				
				vo.setFreq(Integer.parseInt(rs.getString("Freq")));
				
				vo.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
				
				break;
			}
			
		} catch (Exception e) {
			log.error("取得轮询监视错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		return vo;
	}
    
    /**
     *  取得手动选台或自动轮播组播地址和端口号
     * @param vo
     * @param statusFlag 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
     * @return
     * @throws DaoException
     */
    public static MonitorProgramQueryVO GetChangeProgramInfo(MonitorProgramQueryVO vo, int statusFlag) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// 取得手动选台或自动轮播信息
		// 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
		strBuff.append("select *  from monitorprogramquery where statusFlag = " + statusFlag);
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				
				vo.setSmgURL(rs.getString("smgURL"));
				
				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				
				vo.setFreq(Integer.parseInt(rs.getString("Freq")));
				
				vo.setServiceID(Integer.parseInt(rs.getString("ServiceID")));
				
				break;
			}
			
		} catch (Exception e) {
			log.error("取得手动选台或自动轮播错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		return vo;
	}

    /**
     *  取得手动选台或自动轮播组播地址和端口号
     * @param vo
     * @param statusFlag 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
     * @return
     * @throws DaoException
     */
    public static List<MonitorProgramQueryVO> GetChangeProgramInfoList(List<MonitorProgramQueryVO> list) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		StringBuffer strBuff = new StringBuffer();
		
		// 取得手动选台或自动轮播信息
		// 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
		strBuff.append("select *  from monitorprogramquery where statusFlag in(1,2,3,4,5,6);");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				MonitorProgramQueryVO vo= new MonitorProgramQueryVO();
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				
				vo.setSmgURL(rs.getString("smgURL"));
				
				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				list.add(vo);
			}
			
		} catch (Exception e) {
			log.error("取得手动选台或自动轮播错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		return list;
	}   
    
    /**
     *  取得手动录制组播地址和端口号
     * @param vo
     * @return
     * @throws DaoException
     */
    public static MonitorProgramQueryVO GetManualRecordProgramInfo(MonitorProgramQueryVO vo) throws DaoException {

		Statement statement = null;
		if (vo.getFreq() != 0 && vo.getServiceID() != 0) {
			
			Connection conn = DaoSupport.getJDBCConnection();
	
			ResultSet rs = null;
			
			StringBuffer strBuff = new StringBuffer();
			
			// 取得手动选台或自动轮播信息
			// 0:空闲 1:RTVS使用 2:轮播监测使用 3:手动选台和自动轮播
			strBuff.append("select *  from monitorprogramquery where Freq = " + vo.getFreq() + " and ServiceID=" + vo.getServiceID());
			
			try {
				statement = conn.createStatement();
				
				rs = statement.executeQuery(strBuff.toString());
				
				while(rs.next()){
					vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
					
//					vo.setRtvsURL(rs.getString("rtvsURL"));
					
					vo.setRtvsIP(rs.getString("rtvsIP"));
					
					vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
					
					vo.setSmgURL(rs.getString("smgURL"));
					
					vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
					
					vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
					
					vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
					
					return vo;
				}
				
			} catch (Exception e) {
				log.error("取得手动选台或自动轮播错误: " + e.getMessage());
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			}
		}
		return vo;
	}
    
    public static List GetWatchAndSeeVOList() throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		ResultSet rs = null;
		
		List monitorProgramList = new ArrayList();
		
		StringBuffer strBuff = new StringBuffer();
		
		// 取得轮询监视信息
		strBuff.append("select *  from monitorprogramquery ");
		
		try {
			statement = conn.createStatement();
			
			rs = statement.executeQuery(strBuff.toString());
			
			while(rs.next()){
				MonitorProgramQueryVO vo = new MonitorProgramQueryVO();
				vo.setRtvsIndex(Integer.parseInt(rs.getString("rtvsIndex")));
				
//				vo.setRtvsURL(rs.getString("rtvsURL"));
				
				vo.setRtvsIP(rs.getString("rtvsIP"));
				
				vo.setRtvsPort(Integer.parseInt(rs.getString("rtvsPort")));
				
				vo.setSmgURL(rs.getString("smgURL"));
				
				vo.setSmgIndex(Integer.parseInt(rs.getString("smgIndex")));
				
				vo.setStatusFlag(Integer.parseInt(rs.getString("StatusFlag")));
				
				vo.setRTVSResetURL(rs.getString("rtvsResetURL"));
				
				monitorProgramList.add(vo);
				
				break;
			}
			
		} catch (Exception e) {
			log.error("取得轮询监视错误: " + e.getMessage());
		} finally {
			DaoSupport.close(rs);
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		return monitorProgramList;
	}
    
}
