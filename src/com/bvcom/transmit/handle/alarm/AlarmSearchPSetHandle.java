package com.bvcom.transmit.handle.alarm;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.parse.alarm.AlarmSearchPSetParse;
import com.bvcom.transmit.parse.alarm.domain.AlarmSwitch;
import com.bvcom.transmit.parse.video.domain.AlarmTimeDao;
import com.bvcom.transmit.util.AlarmSwitchMemory;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchPSetVO;

/**
 * 报警查询
 * 
 * @author Bian Jiang
 * 
 */
public class AlarmSearchPSetHandle {

	private static Logger log = Logger.getLogger(AlarmSearchPSetHandle.class
			.getSimpleName());

	private MSGHeadVO bsData = new MSGHeadVO();

	private String downString = new String();

	private UtilXML utilXML = new UtilXML();

	public AlarmSearchPSetHandle(String centerDownStr, MSGHeadVO bsData) {
		this.downString = centerDownStr;
		this.bsData = bsData;
	}

	/**
	 * 1. 下发给IPM 2. 接收IPM返回的信息，替换Freq等信息 3. 上报中心
	 * 
	 */
	public void downXML() {
		String upString = "";
		List IPMSendList = new ArrayList();

		boolean isErr = false;

		Document document = null;
		try {
			document = utilXML.StringToXML(this.downString);
		} catch (CommonException e) {
			log.error("报警查询StringToXML Error: " + e.getMessage());
		}
		;

		AlarmSearchPSetParse AlarmSearchPSetParse = new AlarmSearchPSetParse();
		List<AlarmSearchPSetVO> voList = AlarmSearchPSetParse
				.getDownList(document);

		for (int i = 0; i < voList.size(); i++) {
			AlarmSearchPSetVO AlarmSearchPSet = voList.get(i);
			CommonUtility.checkIPMChannelIndex(AlarmSearchPSet.getIndex(),
					IPMSendList);
			break;
		}

		for (int i = 0; i < IPMSendList.size(); i++) {
			IPMInfoVO smg = (IPMInfoVO) IPMSendList.get(i);
			try {
				// 报警查询信息下发 timeout 1000*10 10秒
				upString = utilXML.SendDownXML(this.downString, smg.getURL(),
						CommonUtility.CONN_WAIT_TIMEOUT, bsData);
			} catch (CommonException e) {
				log.error("向IPM下发报警查询设置出错：" + smg.getURL());
				upString = "";
			}
		}

		try {
			if (upString == null || upString.equals("")) {
				upString = utilXML.getReturnXML(bsData, 1);
			}
			document = utilXML.StringToXML(upString);
		} catch (CommonException e) {
			log.error("报警查询StringToXML Error: " + e.getMessage());
			isErr = true;
		}
		;

		if (!isErr) {
			// 替换频点等信息
			AlarmSearchPSetParse.replaceFreqInfo(document, voList);
			document.setXMLEncoding("GB2312");
			upString = document.asXML();
		} else {
			upString = utilXML.getReturnXML(bsData, 1);
		}

		try {
			utilXML.SendUpXML(upString, bsData);
		} catch (CommonException e) {
			log.error("报警查询回复失败: " + e.getMessage());
		}

	}

	/**
	 * 节目报警主动上报 1. 从数据库取得Freq等列表信息 2. 更新XML数据信息 3. 返回修改后的XML
	 * 
	 */
	public String upXML() {
		// 数据库取得详细频点信息
		List<AlarmSearchPSetVO> rsList = new ArrayList<AlarmSearchPSetVO>();

		Document document = null;
		try {
			document = utilXML.StringToXML(this.downString);
		} catch (CommonException e) {
			log.error("报警查询StringToXML Error: " + e.getMessage());
			// TODO 上报错误信息
		}

		// 取得上报频点信息
		AlarmSearchPSetParse AlarmSearchPSetParse = new AlarmSearchPSetParse();
		List<AlarmSearchPSetVO> freqList = AlarmSearchPSetParse
				.getUpList(document);

		this.getFreqInfoFromDB(freqList, rsList);

		if (rsList.size() > 0) {
			// 更新XML信息的Freq等信息
			AlarmSearchPSetParse.replaceFreqInfo(document, rsList);
		} else {
			return "";
		}

		return document.asXML();
	}

	/**
	 * 从数据库查询通道详细信息
	 * 
	 * @param freqList
	 * @param rsList
	 */
	private void getFreqInfoFromDB(List freqList, List rsList) {
		StringBuffer strBuff = new StringBuffer();

		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		int freq = 0;
		int serviceID = 0;

		try {
			conn = DaoSupport.getJDBCConnection();
			// SELECT * FROM transmit.channelstatus c where channelindex = 1 or
			// channelindex = 3

			for (int i = 0; i < freqList.size(); i++) {
				AlarmSearchPSetVO AlarmSearchPSet = (AlarmSearchPSetVO) freqList
						.get(i);
				strBuff = new StringBuffer();
				strBuff.append("SELECT * FROM channelscanlist c where ");
				strBuff.append(" lastflag = 1 and Freq = "
						+ AlarmSearchPSet.getFreq() + " and ServiceID = "
						+ AlarmSearchPSet.getServiceID());

				try {
					statement = conn.createStatement();
					rs = statement.executeQuery(strBuff.toString());

					while (rs.next()) {

						if (rs.getString("Freq") == null
								|| rs.getString("Freq").equals("")
								|| rs.getString("Freq").equals("0")) {
							continue;
						} else {
							freq = Integer.parseInt(rs.getString("Freq"));
							if (freq == 0) {
								continue;
							}
						}
						AlarmSearchPSetVO rsAlarmSearchPSet = new AlarmSearchPSetVO();

						rsAlarmSearchPSet.setFreq(Integer.parseInt(rs
								.getString("Freq")));
						if (rs.getString("ServiceID") == null
								|| rs.getString("ServiceID").equals("")
								|| rs.getString("ServiceID").equals("0")) {
							continue;
						} else {
							serviceID = Integer.parseInt(rs
									.getString("ServiceID"));
							if (serviceID == 0) {
								continue;
							}
						}
						rsAlarmSearchPSet.setServiceID(Integer.parseInt(rs
								.getString("ServiceID")));
						rsAlarmSearchPSet.setVideoPID(Integer.parseInt(rs
								.getString("VideoPID")));
						rsAlarmSearchPSet.setAudioPID(Integer.parseInt(rs
								.getString("AudioPID")));
						rsList.add(rsAlarmSearchPSet);
						break;
					}

				} catch (Exception e) {
					log.error("报警信息查询节目信息错误: " + e.getMessage());
					/**输出错误信息到文件*/
		    		PrintWriter pw;
					try {
						pw = new PrintWriter(new File("D:/AlarmSearchPSetHandle.log"));
						e.printStackTrace(pw);
						pw.flush();
						pw.close();
					} catch (Exception e1) {
					}
					/******/
				} finally {
					DaoSupport.close(rs);
					DaoSupport.close(statement);
				}

				strBuff = null;
			}

		} catch (DaoException e1) {
			log.error("通道状态查询数据库错误: " + e1.getMessage());
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
	}

	/**
	 * 
	 * 判断该集合中的报警记录 开关状态 如果开关状态为0则丢弃该报警
	 * 增加判断 增加运行图报警过滤
	 * 判断是否重复报警重复则丢弃
	 * @param alarmList
	 * @return List New List
	 */
	public static List getFreqFromDB(List alarmList, String type) {

		
		//TODO 增加过滤 码流层运行图 报警过滤 Ji Long 2011-06-16
		AlarmTimeDao dao=new AlarmTimeDao();
		alarmList=dao.select(alarmList);
		
		 Statement statement = null;
		 ResultSet rs = null;
		 Connection conn = null;
		List newList = new ArrayList();

		 try {
			conn = DaoSupport.getJDBCConnection();
	
			for (int i = 0; i < alarmList.size(); i++) {
				AlarmSearchPSetVO alarmSearchPSet = (AlarmSearchPSetVO) alarmList
						.get(i);
				/*
				 * StringBuffer strBuff = new StringBuffer(); strBuff.append("SELECT *
				 * FROM channelremapping c where "); strBuff.append("statusFlag = 1
				 * and Freq = " + alarmSearchPSet.getFreq());
				 *  // 如果是节目报警的加上ServiceID if (type.equals("AlarmSearchPSet")) {
				 * strBuff.append(" and ServiceID = " +
				 * alarmSearchPSet.getServiceID()); }
				 * 
				 * try { statement = conn.createStatement(); rs =
				 * statement.executeQuery(strBuff.toString()); boolean isTrue =
				 * false; while (rs.next()) { isTrue = true; break; } if(isTrue) {
				 */
				// TODO
				// 增加判断 如果改报警对应得频率或节目 的报警开关已经关闭
				// 则丢掉该报警
				if (alarmIsList(alarmSearchPSet)) {
	//				log.info("该报警开关已经关闭则丢弃频点" + alarmSearchPSet.getFreq() + "节目id"
	//						+ alarmSearchPSet.getServiceID() + "报警类型"
	//						+ alarmSearchPSet.getAlarmType() + "");
				} else {
					try {
//						conn = DaoSupport.getJDBCConnection();
						StringBuffer strBuff = new StringBuffer();
						// 过滤重复报警 吉龙
						boolean falg = false;
						int serviceID = alarmSearchPSet.getServiceID();
						int freq = alarmSearchPSet.getFreq();
						int alarmValue = alarmSearchPSet.getValue();
						int alarmType = alarmSearchPSet.getType();
						// if(serviceID==0&&videoPID==0&&audioPID==0){
						try {
							statement = conn.createStatement();
							strBuff.append("select id FROM alarmsearchtable ");
							strBuff.append(" where freq = " + freq + " ");
							strBuff.append(" and ServiceID = " + serviceID + " ");
							strBuff.append(" and AlarmValue = " + alarmValue + " ");
							strBuff.append(" and AlarmType = " + alarmType + " ");
							strBuff.append(" and AlarmEndTime is " + "null" );
							rs = statement.executeQuery(strBuff.toString());
							if (rs.next()) {
								//log.info("该报警已存在并且状态一致 则丢弃");
								continue;
							} else {
								falg = true;
								strBuff = new StringBuffer();
							}
						} catch (Exception e) {
							log.error("过滤重复报警错误: " + e.getMessage());
						}
						if(falg ){
							AlarmSearchPSetVO vo = new AlarmSearchPSetVO();
							vo.setAlarmType(alarmSearchPSet.getAlarmType());
							vo.setIndex(alarmSearchPSet.getIndex());
							vo.setFreq(alarmSearchPSet.getFreq());
							// if (type.equals("AlarmSearchPSet")) {
							vo.setServiceID(alarmSearchPSet.getServiceID());
							vo.setVideoPID(alarmSearchPSet.getVideoPID());
							vo.setAudioPID(alarmSearchPSet.getAudioPID());
							// }
							vo.setAlarmID(alarmSearchPSet.getAlarmID());
							vo.setReturnValue(alarmSearchPSet.getReturnValue());
							vo.setComment(alarmSearchPSet.getComment());
							vo.setType(alarmSearchPSet.getType());
							vo.setDesc(alarmSearchPSet.getDesc());
							vo.setValue(alarmSearchPSet.getValue());
							vo.setTime(alarmSearchPSet.getTime());
							newList.add(vo);
						}
					} catch (Exception e) {
						log.error("报警信息过滤错误: " + e.getMessage());
					}finally{
						try {
							DaoSupport.close(statement); 
							DaoSupport.close(rs);
						}catch (DaoException e) {
							log.error("关闭数据库失败: " + e.getMessage()); 
						}
					}
					
				}
				/*
				 * } } catch (Exception e) { log.error("报警信息查询频点信息错误: " +
				 * e.getMessage()); } finally { DaoSupport.close(rs);
				 * DaoSupport.close(statement); }
				 * 
				 * strBuff = null; }
				 *  } catch (DaoException e1) { log.error("报警信息查询频点信息错误: " +
				 * e1.getMessage()); } finally { try { DaoSupport.close(conn); }
				 * catch (DaoException e) { log.error("关闭数据库失败: " + e.getMessage()); } }
				 */
			}
		 } catch (Exception e) {
				log.error("报警信息过滤错误: " + e.getMessage());
		 }finally{
			try {
				DaoSupport.close(conn); 
			}catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage()); 
			}
		}
		return newList;
	}

	private static boolean alarmIsList(AlarmSearchPSetVO vo) {
		boolean temp = false;
		List<AlarmSwitch> list = AlarmSwitchMemory.alarmSwitchList;
		for (AlarmSwitch as : list) {
			if (as.getAlarmType() == vo.getType()) {
				if (vo.getServiceID() != 0) {
//					log.info("确定节目收到协议中频点" + vo.getFreq() + ", serviceid"
//							+ vo.getServiceID() + ", 报警类型" + vo.getType());
//					log.info("确定节目内存中报警频点" + as.getFreq() + ", serviceid"
//							+ as.getServiceID() + ", 报警类型" + as.getAlarmType()
//							+ ", 报警状态" + as.getSwitchValue());
					if (as.getFreq().equals(vo.getFreq() + "")
							&& as.getServiceID().equals(vo.getServiceID() + "")
							&& as.getSwitchValue() == 0
							&& as.getSwitchType() == 1) {
						temp = true;
						break;
					}
				} else {
//					log.info("确定频点收到协议中频点" + vo.getFreq() + ", serviceid"
//							+ vo.getServiceID() + ", 报警类型" + vo.getType());
//					log.info("确定频点内存中报警频点" + as.getFreq() + ", serviceid"
//							+ as.getServiceID() + ", 报警类型" + as.getAlarmType()
//							+ ", 报警状态" + as.getSwitchValue());
					if (as.getFreq().equalsIgnoreCase(vo.getFreq() + "")
							&& as.getSwitchValue() == 0
							&& as.getSwitchType() == 2) {
						temp = true;
						break;
					}
				}
			}
		}
		//log.info("第一次判断后该节目开关状态" + temp);
		if (temp == false) {
			for (AlarmSwitch as : list) {
				if (as.getAlarmType() == vo.getType()) {
					if (vo.getServiceID() != 0) {
//						log.info("所有节目收到协议中频点" + vo.getFreq() + ", serviceid"
//								+ vo.getServiceID() + ", 报警类型" + vo.getType());
//						log.info("所有节目内存中报警频点" + as.getFreq() + ", serviceid"
//								+ as.getServiceID() + ", 报警类型"
//								+ as.getAlarmType() + ", 报警状态"
//								+ as.getSwitchValue());
						if ((as.getFreq().equals("arr")||as.getFreq().equals("ALL"))
								&& as.getServiceID().equals(vo.getServiceID() + "")
								&& as.getSwitchValue() == 0
								&& as.getSwitchType() == 1) {
							temp = true;
							break;
						}
					} else {
//						log.info("所有频点收到协议中频点" + vo.getFreq() + ", serviceid"
//								+ vo.getServiceID() + ", 报警类型" + vo.getType());
//						log.info("所有频点内存中报警频点" + as.getFreq() + ", serviceid"
//								+ as.getServiceID() + ", 报警类型"
//								+ as.getAlarmType() + ", 报警状态"
//								+ as.getSwitchValue());
						if ((as.getFreq().equals("arr")||as.getFreq().equals("ALL"))
								&& as.getSwitchValue() == 0) {
							temp = true;
							break;
						}
					}
				}
			}
		}
		//log.info("最终返回时 该节目开关状态" + temp);
		return temp;
	}
}
