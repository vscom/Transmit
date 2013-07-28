package com.bvcom.transmit.handle.si;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.config.AutoAnalysisTimeQueryConfigFile;
import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.handle.video.SetAutoRecordChannelHandle;
import com.bvcom.transmit.parse.rec.SetAutoRecordChannelParse;
import com.bvcom.transmit.parse.si.ChannelScanQueryParse;
import com.bvcom.transmit.util.CleanChannelAndTSC;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
import com.bvcom.transmit.vo.si.ChannelScanQueryVO;

public class ChannelScanQueryHandle {

	private static Logger log = Logger.getLogger(ChannelScanQueryHandle.class
			.getSimpleName());

	MSGHeadVO bsData = new MSGHeadVO();

	String downString = new String();

	UtilXML utilXML = new UtilXML();

    MemCoreData coreData = MemCoreData.getInstance();
    
	public ChannelScanQueryHandle(String centerDownStr, MSGHeadVO bsData) {
		this.downString = centerDownStr;
		this.bsData = bsData;
	}
	
	public ChannelScanQueryHandle() {
	}

	public MSGHeadVO setBsData(MSGHeadVO bsData){
		this.bsData = bsData;
		return this.bsData;
	}
	/**
	 * 频道扫描处理
	 * 1. 下发频道扫描
	 * 2. 接收频道扫描结果
	 * 3. 扫描结果入库
	 * 4. 扫描结果上报中心
	 *
	 */
	public void downXML() {

		List SMGSendList = new ArrayList();

		CommonUtility.checkSMGChannelType("ChannelScanQuery", SMGSendList);
		UtilXML xmlUtil = new UtilXML();

		String returnStr = "";
		
        SysInfoVO sysVO = coreData.getSysVO();

        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("历史视频查看StringToXML Error: " + e.getMessage());
        }
        

        ChannelScanQueryParse ChannelScanQueryParse = new ChannelScanQueryParse();

        ChannelScanQueryVO vo = ChannelScanQueryParse.getDownObject(document);
        
        String channelScanPath = "";
        
		try {
			File readFilePath = null;
			if (bsData.getVersion().equals(CommonUtility.XML_VERSION_2_3)) {
				channelScanPath = CommonUtility.CHANNEL_SCAN_PATH_2_3;
			}
			else if (bsData.getVersion().equals(CommonUtility.XML_VERSION_2_5)) {
				channelScanPath = CommonUtility.CHANNEL_SCAN_PATH_2_5;
				this.downString = this.downString.replaceAll("Version=\"2.5\"", "Version=\"2.3\"");
//				System.out.println(" ------ 处理后 协议 -------"+bsData);
			}
			else {
				channelScanPath = CommonUtility.CHANNEL_SCAN_PATH_2_0;
			}
			readFilePath = new File(channelScanPath);
			returnStr = CommonUtility.readStringFormFile(readFilePath);

		} catch (CommonException e1) {
			// e1.printStackTrace();
			log.error("取得频道扫描信息失败: " + channelScanPath);
		}

		if (vo.getScanTime() == null || vo.getScanTime().equals("") || returnStr.trim().equals("") || vo.getScanType() == 0) {
			
			for (int i = 0; i < SMGSendList.size(); i++) {
				SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
				try {
					// TODO 在这里添加指定频点扫协议 监测中心四期测试有可能搞一些不规则的频点 如 52.5 等 2011-08-15 Ji Long 
					// 把指定扫 收到的 频道表 和全频点扫 收到的频道表 合成为 一个  返回给平台 并保存到指定文件目录
					//ChannelScanQueryFlag
					AutoAnalysisTimeQueryConfigFile  autoAnalysisTimeQueryConfigFile=new AutoAnalysisTimeQueryConfigFile(); 
					String str =autoAnalysisTimeQueryConfigFile.getChannelScanQueryFlag();
					String strr="";
					if(str.split(",")[0].equals("1")){
						ChannelScanQueryParse channelScanQueryParse=new ChannelScanQueryParse();
						String sendStr=channelScanQueryParse.createChannelScanXML(str, bsData);
						// 频道扫描信息下发 timeout 1000*60*5 五分钟
						strr = utilXML.SendDownXML(sendStr, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT2,bsData);
					}
					
					// 频道扫描信息下发 timeout 1000*60*50 五十分钟
					returnStr = utilXML.SendDownXML(this.downString, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT,bsData);
					if(returnStr == null || returnStr.equals("")) {
						log.error("取得频道扫描列表失败: " + smg.getURL());
						continue;
					}
					
					//如果扫到的频道表长度不为0 则 把两次拼接
					if(str.split(",").equals("1")){
						if(strr.length()!=0){
							//拼接两次的频道表
						}
					}
					
					returnStr = CommonUtility.RegReplaceString(returnStr, "ScanTime");
					
					if (vo.getScanType() == 1) {
						/**
						 * ScanType=0为简单
						 * 当ScanType=1时为详细扫描
						 */
						CommonUtility.StoreIntoFile(returnStr, channelScanPath);
		                CommonUtility.StoreIntoFile(returnStr, sysVO.getTomcatHome() + "/webapps/transmit/ChannelScanQuery.xml");
					}
					break;
				} catch (CommonException e) {
					log.error("向SMG下发频道扫描出错：" + smg.getURL());
				}
			}

		}
		
		if (vo.getScanType() == 1) {
			/**
			 * ScanType=0为简单
			 * 当ScanType=1时为详细扫描
			 */
	        try {
	        	if(returnStr == null || returnStr.equals("")) {
	        		log.error("取得频道扫描列表失败, 请过几分钟再试。");
	        		return;
	        	}
	        	CommonUtility.StoreIntoFile(returnStr, sysVO.getTomcatHome() + "/webapps/transmit/ChannelScanQuery.xml");
	        	
	        	returnStr = returnStr.replaceAll("'", " ");
	        	
	            document = utilXML.StringToXML(returnStr);
	            List<ChannelScanQueryVO> ChannelScanQueryVOList = ChannelScanQueryParse.getReturnObject(document);
	            delChannelScanTable();
	            
	            //added by tqy 新增加ServiceType入库操作，兼容原来表结构
	            try {
	            	updateChannelScanTable(ChannelScanQueryVOList);
	            }
	            catch(Exception ex){
	            	upChannelScanTable(ChannelScanQueryVOList);
	            }
	        } catch (Exception e) {
	            log.error("频道扫描结果入库失败 Error: " + e.getMessage());
	        }
		}
		
		try {
        	// 判断是否完整的XML协议 Add Start By Bian Jiang 2011.2.21
            int msgEnd = returnStr.indexOf(CommonUtility.XML_MSG_END) + CommonUtility.XML_MSG_END.length();
            
            if (msgEnd != CommonUtility.XML_MSG_END.length()) {
            	try {
            		returnStr = returnStr.substring(0, msgEnd);	
            	} catch (Exception ex) {
            		log.warn("频道列表信息处理出错: " + ex.getMessage());
            	}
            }
           // 判断是否完整的XML协议 Add End By Bian Jiang 2011.2.21
			
			// 国家监测中心东软前端更新，增加了对时间的判断，所以不能每次都修改时间
			utilXML.SendUpXML(utilXML.replaceXMLMsgHeader(returnStr, bsData),bsData);
		} catch (CommonException e) {
			log.error("上发频道扫描信息失败: " + e.getMessage());
		}
		
		//添加 清空自动录像节目信息  和给TSC下发删除协议 
		CleanChannelAndTSC  ccat=new CleanChannelAndTSC(bsData);
		ccat.chean();
		
		// 频道表里面有的节目，但是在映射表里面没有，需要自动把节目配置到节目映射表里面
		// By: Jiang 2013.6.29
		getFreqFromScanlistByRemapping();
		
		bsData = null;
		downString = null;
		SMGSendList = null;
		utilXML = null;
	}
	
	/**
	 * 获取在频道列表里面没有被分配的频点
	 * By: Jiang 2013.6.29
	 */
	private void getFreqFromScanlistByRemapping() {
		// select * from channelscanlist where not exists (select * from channelremapping where freq=channelremapping.freq and serviceid = channelremapping.serviceid) group by freq
		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "select * from channelscanlist where not exists (select * from channelremapping where freq=channelremapping.freq and serviceid = channelremapping.serviceid) group by freq";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				int freq=rs.getInt("Freq");
				// TODO 根据查出来的频点，获取全部节目信息，下发给SMG设备进行频点锁定
				getProgramByFreq(freq);
			}
		}catch (Exception e) {
			log.error("映射表里面有，但是频道扫描表里面没有的情况: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
	}

	/**
	 * 从频道扫描表里面获取节目信息
	 * @param freq
	 * @return
	 * By: Jiang 2013.6.29
	 */
	private void getProgramByFreq(int freq) {
		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		int devIndex = 0;
		List<SetAutoRecordChannelVO> programList = new ArrayList<SetAutoRecordChannelVO>(); 
		try {
			// 从频道扫描表里面获取节目信息
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "SELECT * FROM channelscanlist where freq = " + freq + ";";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				SetAutoRecordChannelVO srcVo=new SetAutoRecordChannelVO();
				srcVo.setAction("Set");
				srcVo.setFreq(rs.getInt("Freq"));
				srcVo.setServiceID(rs.getInt("ServiceID"));
				srcVo.setProgramName(rs.getString("Program"));
				srcVo.setQAM(rs.getInt("QAM"));
				srcVo.setSymbolRate(rs.getInt("SymbolRate"));
				srcVo.setVideoPID(rs.getInt("VideoPID"));
				srcVo.setAudioPID(rs.getInt("AudioPID"));
				srcVo.setHDFlag(rs.getInt("HDTV"));
				programList.add(srcVo);
			}
			
		} catch (Exception e) {
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		
		// 从映射表里面获取没有被使用的通道或者频点已经被分配的通道
		devIndex = getUndistributedByFreq(freq);
		try {
			// 从节目映射表里面获取，当前设备通道信息
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "SELECT * FROM channelremapping where Devindex = " + devIndex + ";";
			rs = statement.executeQuery(sqlStr);
			int count = 0;
			while (rs.next()) {
				SetAutoRecordChannelVO vo = programList.get(count);
				vo.setDevIndex(rs.getInt("Devindex"));
				vo.setIndex(rs.getInt("channelindex"));
				// Type: 9: 频道扫描Java转发自动给SMS分配的节目 add By: Jiang 2013.7.10
				vo.setRecordType(9);
				count++;
			}
		} catch (Exception e) {
			log.error("getProgramByFreq 从映射表里面获取没有被使用的通道或者频点已经被分配的通道: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		
		// 下发节目信息给SMG
		// 生成数据下发给SMG
		List SMGSendList = new ArrayList();
		CommonUtility.checkSMGChannelIndex(devIndex, SMGSendList);
		
		SetAutoRecordChannelParse recordString = new SetAutoRecordChannelParse();
		String smgDownString = recordString.createForDownXML(bsData, programList, "Set", true);
		String url = "";
        for(int l=0;l<SMGSendList.size();l++)
        {
            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(l);
            try {
                if (!url.equals(smg.getURL().trim())) {
                smgDownString = smgDownString.replaceAll("QAM=\"64\"", "QAM=\"QAM64\"");
            	smgDownString = smgDownString.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
                // 自动录像下发 timeout 1000*30 三十秒
                utilXML.SendDownNoneReturn(recordString.replaceString(smgDownString), smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                url = smg.getURL().trim();
                }
                // 高清相关配置
                if (smg.getHDFlag() == 1 && smg.getHDURL() != null && !smg.getHDURL().trim().equals("")) {
                	// 高清转码下发
                	utilXML.SendDownNoneReturn(recordString.replaceString(smgDownString), smg.getHDURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                }
                try {
					Thread.sleep(1000 * 1);
				} catch (InterruptedException e) {
				}
            } catch (CommonException e) {
                log.error("下发自动录像到SMG出错：" + smg.getURL());
                return;
            }
        }
		
		// SMG 下发成功更新节目映射表数据库
        SetAutoRecordChannelHandle autoRecord = new SetAutoRecordChannelHandle();
        for (int i=0; i < programList.size(); i++) {
        	try {
        		SetAutoRecordChannelVO vo = programList.get(i);
				autoRecord.upChannelRemappingIndex(vo);
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
	}
	
	/**
	 * 获取在界面映射表中没有被分配的通道
	 * 从映射表里面获取没有被使用的通道或者频点已经被分配的通道
	 * @return devIndex SMG 的设备通道号
	 * By: Jiang 2013.6.29
	 */
	private int getUndistributedByFreq(int freq) {
		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		int devIndex = 0;
		// 通过Freq 和 StatusFlag 来确定相关的频点是否分配
		StringBuffer strBuff = new StringBuffer();
		
		//是否为未使用的频点通道信息
		strBuff.append("select * from channelremapping where devindex in (select DevIndex from channelremapping where delflag =0 and freq = " + freq + ") and delflag =0 and statusflag = 0");
		System.out.println("是否为未使用的频点通道信息{freq}"+strBuff.toString());
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery(strBuff.toString());
			
			// 库中存在相关的节目返回相关的设备通道
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				break;
			}
		} catch (Exception e) {
			log.error("getUndistributedByFreq 获取在界面映射表中没有被分配的通道: " + e.getMessage());
			log.error("getUndistributedByFreq 获取在界面映射表中没有被分配的通道2 SQL: " + strBuff.toString());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				if (devIndex > 0) {
					DaoSupport.close(conn);
					return devIndex;
				}
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// 频点没有分配的时候获取通道信息
		strBuff = new StringBuffer();
		// SELECT * FROM channelremapping c where statusflag = 0 and Devindex not in (select devindex from channelremapping where statusflag = 1) group by devindex
		strBuff.append("SELECT * FROM channelremapping c where delflag =0 and statusflag = 0 and Devindex not in (select devindex from channelremapping where delflag =0 and statusflag = 1) group by devindex");
		System.out.println("没有取得相关的通道，需要从新分配一个新的SMG板卡:"+strBuff.toString());
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(strBuff.toString());
			// 取得没有被分配的SMG设备
			while(rs.next()){
				devIndex = Integer.parseInt(rs.getString("DevIndex"));
				break;
			}
		} catch (Exception e) {
			log.error("getUndistributedByFreq 获取在界面映射表中没有被分配的通道: " + e.getMessage());
			log.error("getUndistributedByFreq 获取在界面映射表中没有被分配的通道2 SQL: " + strBuff.toString());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return devIndex;
	}

	/**
	 * 更新入库频道扫描表
	 * @param 需要更新的XML数据
	 * @throws DaoException 
	 */
	public static void upChannelScanTable(List<ChannelScanQueryVO> ChannelScanQueryVOList) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		for(int i=0; i<ChannelScanQueryVOList.size(); i++) {
			
			StringBuffer strBuff = new StringBuffer();
			ChannelScanQueryVO vo = ChannelScanQueryVOList.get(i);
			// insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime)
			// values(1, 1, 1, 'test', 1, 1, 1, 1, 1, '2000-01-01 08:54:55', '2000-01-01 08:54:55')
			
			strBuff.append("insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime, LastFlag)");
			strBuff.append(" values(");
			strBuff.append(vo.getFreq() + ", ");
			strBuff.append("'" + vo.getQAM() + "', ");
			strBuff.append(vo.getSymbolRate() + ", ");
			strBuff.append("'" +  vo.getProgram() + "', ");
			strBuff.append(vo.getServiceID() + ", ");
			strBuff.append(vo.getVideoPID() + ", ");
			strBuff.append(vo.getAudioPID() + ", ");
			strBuff.append(vo.getEncrypt() + ", ");
			strBuff.append(vo.getHDTV() + ", ");
			strBuff.append("'" +  vo.getScanTime() + "', ");
			strBuff.append("'" + CommonUtility.getDateTime() + "', ");
			strBuff.append("1)");
	
			try {
				statement = conn.createStatement();
	
				statement.executeUpdate(strBuff.toString());
	
			} catch (Exception e) {
				log.error("频道扫描更新数据库错误: " + e.getMessage());
				log.error("频道扫描更新数据库错误 SQL：\n" + strBuff.toString());
			} finally {
				DaoSupport.close(statement);
			}
		}
		DaoSupport.close(conn);
		log.info("扫描结果更新数据库成功!");
	}

	
	public static void updateChannelScanTable(List<ChannelScanQueryVO> ChannelScanQueryVOList) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		for(int i=0; i<ChannelScanQueryVOList.size(); i++) {
			
			StringBuffer strBuff = new StringBuffer();
			ChannelScanQueryVO vo = ChannelScanQueryVOList.get(i);
			// insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV,ServiceType, ScanTime, LastTime,LastFlag)
			// values(1, 1, 1, 'test', 1, 1, 1, 1, 1, '2000-01-01 08:54:55', '2000-01-01 08:54:55')
			
			strBuff.append("insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ServiceType,ScanTime, LastTime, LastFlag)");
			strBuff.append(" values(");
			strBuff.append(vo.getFreq() + ", ");
			strBuff.append("'" + vo.getQAM() + "', ");
			strBuff.append(vo.getSymbolRate() + ", ");
			strBuff.append("'" +  vo.getProgram() + "', ");
			strBuff.append(vo.getServiceID() + ", ");
			strBuff.append(vo.getVideoPID() + ", ");
			strBuff.append(vo.getAudioPID() + ", ");
			strBuff.append(vo.getEncrypt() + ", ");
			strBuff.append(vo.getHDTV() + ", ");
			
			//added by tqy
			strBuff.append("'" + vo.getServiceType() + "', ");
			
			strBuff.append("'" +  vo.getScanTime() + "', ");
			strBuff.append("'" + CommonUtility.getDateTime() + "', ");
			strBuff.append("1)");
	
			try {
				statement = conn.createStatement();
	
				statement.executeUpdate(strBuff.toString());
	
			} catch (Exception e) {
				log.error("频道扫描更新数据库错误: " + e.getMessage());
				log.error("频道扫描更新数据库错误 SQL：\n" + strBuff.toString());
			} finally {
				DaoSupport.close(statement);
			}
		}
		DaoSupport.close(conn);
		log.info("扫描结果更新数据库成功!");
	}
	/**
	 * 更新入库频道扫描表
	 * @param 需要更新的XML数据
	 * @throws DaoException 
	 */
	public static void upChannelScanTable(ChannelScanQueryVO vo) throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();

		StringBuffer strBuff = new StringBuffer();
		// insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime)
		// values(1, 1, 1, 'test', 1, 1, 1, 1, 1, '2000-01-01 08:54:55', '2000-01-01 08:54:55')
		
		strBuff.append("insert into channelscanlist(Freq, QAM, SymbolRate, Program, ServiceID, VideoPID, AudioPID, EncryptFlg, HDTV, ScanTime, LastTime, LastFlag)");
		strBuff.append(" values(");
		strBuff.append(vo.getFreq() + ", ");
		strBuff.append("'" + vo.getQAM() + "', ");
		strBuff.append(vo.getSymbolRate() + ", ");
		strBuff.append("'" +  vo.getProgram() + "', ");
		strBuff.append(vo.getServiceID() + ", ");
		strBuff.append(vo.getVideoPID() + ", ");
		strBuff.append(vo.getAudioPID() + ", ");
		strBuff.append(vo.getEncrypt() + ", ");
		strBuff.append(vo.getHDTV() + ", ");
		strBuff.append("'" +  vo.getScanTime() + "', ");
		strBuff.append("'" + CommonUtility.getDateTime() + "', ");
		strBuff.append("1)");

		try {
			statement = conn.createStatement();

			statement.executeUpdate(strBuff.toString());

		} catch (Exception e) {
			log.error("频道扫描更新数据库错误: " + e.getMessage());
			log.error("频道扫描更新数据库错误 SQL：\n" + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
		}
		DaoSupport.close(conn);
		log.info("扫描结果更新数据库成功!");
	}
	
	/**
	 * 更新频道扫描表
	 * @throws DaoException 
	 */
	public static void delChannelScanTable() throws DaoException {

		Statement statement = null;
		Connection conn = DaoSupport.getJDBCConnection();
		StringBuffer strBuff = new StringBuffer();

		// update channelscanlist set LastFlag = 0
		strBuff.append("update channelscanlist set LastFlag = 0");

		try {
			statement = conn.createStatement();

			statement.executeUpdate(strBuff.toString());

		} catch (Exception e) {
			log.error("更新频道扫描数据库错误: " + e.getMessage());
			log.error("更新频道扫描数据库错误 SQL：\n" + strBuff.toString());
		} finally {
			DaoSupport.close(statement);
			DaoSupport.close(conn);
		}
		
		log.info("扫描结果更新数据库成功!");
	}
	
	/**
	 * 实时频点扫描
	 */
    public void channelScanNow() {
        
    	log.info("开始进行频道扫描: ");
        List SMGSendList = new ArrayList();
        
        CommonUtility.checkSMGChannelType("ChannelScanQuery", SMGSendList);

        String upString = "";
        
        upString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        upString += "<Msg Version=\"2.3\" MsgID=\"1000\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"110000G01\" DstCode=\"11000M01\" SrcURL=\"http://10.24.32.28:8089/servlet/receiver\" Priority=\"1\">";
        upString += " <ChannelScanQuery ScanTime=\"\" ScanType=\"1\" SymbolRate=\"6875\" QAM=\"QAM64\" />     ";
        upString += "</Msg>";
        

        UtilXML utilXML = new UtilXML();
        
        String retXML = "";
        
        for (int i=0; i< SMGSendList.size(); i++) {
            SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
            try {
                Document document = null;
                SysInfoVO sysVO = coreData.getSysVO();
                
                // 频道扫描信息下发 timeout 1000*60*10 十分钟
                retXML = utilXML.SendDownXML(upString, smg.getURL(), CommonUtility.CHANNEL_SCAN_WAIT_TIMEOUT, bsData);
                
            	if(retXML.equals("")) {
            		log.error("频道扫描结果为空");
            		break;
            	}
            	
            	retXML = CommonUtility.RegReplaceString(retXML, "ScanTime");
            	
            	// 判断是否完整的XML协议 Add Start By Bian Jiang 2011.2.21
                int msgEnd = retXML.indexOf(CommonUtility.XML_MSG_END) + CommonUtility.XML_MSG_END.length();
                
                if (msgEnd != CommonUtility.XML_MSG_END.length()) {
                	try {
                		retXML = retXML.substring(0, msgEnd);
                	} catch (Exception ex) {
                		 log.warn("频道列表信息处理出错: " + ex.getMessage());
                	}
                }
               // 判断是否完整的XML协议 Add End By Bian Jiang 2011.2.21
                
				if (bsData.getVersion() == CommonUtility.XML_VERSION_2_3) {
					CommonUtility.StoreIntoFile(retXML, CommonUtility.CHANNEL_SCAN_PATH_2_3);
				} else {
					CommonUtility.StoreIntoFile(retXML, CommonUtility.CHANNEL_SCAN_PATH_2_0);
				}

                CommonUtility.StoreIntoFile(retXML, sysVO.getTomcatHome() + "/webapps/transmit/ChannelScanQuery.xml");
                
                try {
                    ChannelScanQueryParse ChannelScanQueryParse = new ChannelScanQueryParse();
                    
                	retXML = retXML.replaceAll("'", " ");
                	
                    document = utilXML.StringToXML(retXML);
                    List<ChannelScanQueryVO> ChannelScanQueryVOList = ChannelScanQueryParse.getReturnObject(document);
                    ChannelScanQueryHandle.delChannelScanTable();
                    ChannelScanQueryHandle.upChannelScanTable(ChannelScanQueryVOList);
                } catch (Exception e) {
                    log.error("频道扫描结果入库失败 Error: " + e.getMessage());
                }
                
                // FIXED 只有一个通道做频道扫描
                break;
            } catch (Exception e) {
                log.error("向SMG下发频道扫描出错：" + smg.getURL());
            }
        }
        //添加 清空自动录像节目信息  和给TSC下发删除协议 
        //清除自动录制中的SrcURL,不返回给监管平台
        //BY TQY 四期
        this.bsData.setSrcURL("");
		CleanChannelAndTSC  ccat=new CleanChannelAndTSC(bsData);
		ccat.chean();
    }
}
