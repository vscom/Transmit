package com.bvcom.transmit.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.handle.video.SetAutoRecordChannelHandle;
import com.bvcom.transmit.parse.rec.SetAutoRecordChannelParse;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;
/**
 * 清空自动录像表中的节目信息 同时 返回清空的节目信息集合便于下发给TSC
 * @author JI LONG  2011-5-13 
 *
 */
public class CleanChannelAndTSC {
	private static Logger log =Logger.getLogger(CleanChannelAndTSC.class.getSimpleName());
	private static MemCoreData coreData = MemCoreData.getInstance();
	@SuppressWarnings("unchecked")
	private static List TSCSendList = coreData.getTSCList();//tsc的列表信息
	private static List SMGSendList = null;//SMG的列表信息
	
	private String downString = new String();
	private UtilXML utilXML = new UtilXML();
	private MSGHeadVO bsData = new MSGHeadVO();
	
    public CleanChannelAndTSC( MSGHeadVO bsData) {
        this.bsData = bsData;
    }
	public void chean(){
		TSCSendList = coreData.getTSCList();//tsc的列表信息
		
		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "SELECT * FROM channelremapping ;";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				int freq=rs.getInt("Freq");
				int serviceid=rs.getInt("ServiceID");
				if(freq!=0&&serviceid!=0){
					sqlStr="SELECT * FROM channelscanlist where freq = "+freq+" and serviceid = "+serviceid+";";
					statement = conn.createStatement();
					ResultSet r = statement.executeQuery(sqlStr);
					if(!r.next()){
						sqlStr="update channelremapping set DelFlag = 1 where freq = "+freq+" and serviceid = "+serviceid+";";
						statement = conn.createStatement();
						statement.executeUpdate(sqlStr);
					}
				}
			}
		}catch (Exception e) {
			log.error("查找自动录像节目信息错误: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
		
		// TODO 映射表里面有，但是频道扫描表里面没有的情况 Add By: Jiang 2013.6.23
		// SELECT * FROM channelremapping where not exists 
		// (select * from channelscanlist where freq=channelremapping.freq and serviceid = channelremapping.serviceid)
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "SELECT * FROM channelremapping where not exists (select * from channelscanlist where freq=channelremapping.freq and serviceid = channelremapping.serviceid)";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				int freq=rs.getInt("Freq");
				int serviceid=rs.getInt("ServiceID");
				if(freq!=0&&serviceid!=0){
					sqlStr="update channelremapping set DelFlag = 1 where freq = "+freq+" and serviceid = "+serviceid+";";
					statement = conn.createStatement();
					statement.executeUpdate(sqlStr);
				}
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
		SMGSendList = new ArrayList();
		List<SetAutoRecordChannelVO> list=new ArrayList<SetAutoRecordChannelVO>(); 
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();
			String sqlStr = "SELECT * FROM channelremapping where DelFlag = 1 ;";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				SetAutoRecordChannelVO srcVo=new SetAutoRecordChannelVO();
				srcVo.setAction("Del");
				srcVo.setFreq(rs.getInt("Freq"));
				srcVo.setServiceID(rs.getInt("ServiceID"));
				srcVo.setQAM(rs.getInt("QAM"));
				srcVo.setSymbolRate(rs.getInt("SymbolRate"));
				srcVo.setDevIndex(rs.getInt("DevIndex"));
				srcVo.setTscIndex(rs.getInt("TscIndex"));
				srcVo.setVideoPID(rs.getInt("VideoPID"));
				srcVo.setAudioPID(rs.getInt("AudioPID"));
				srcVo.setRecordType(rs.getInt("RecordType"));
				list.add(srcVo);
				CommonUtility.checkSMGChannelIndex(rs.getInt("DevIndex"), SMGSendList);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("关闭数据库失败: " + e.getMessage());
			}
		}
	        
		SetAutoRecordChannelParse recordString=new SetAutoRecordChannelParse();
		downString=recordString.createForDownXML(bsData, list, "Del", false);
		
		String url = "";
		String retString = "";
	    int isError = 0;
        for(int t=0;t<TSCSendList.size();t++){
            TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(t);
            try {
            	if (tsc.getRecordType() != 1 && tsc.getRecordType() != 2) {
            		// 如果不是自动录像或异态录像就进行下一个
            		continue;
            	}
                if (!url.equals(tsc.getURL().trim())) {
                    // 自动录像下发
                	retString = utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                    url = tsc.getURL().trim();
                }
                if(!retString.equals("")) {
                	isError = utilXML.getReturnValue(retString);
	                if (isError == 1) {
	                	break;
	                }
                }
            } catch (CommonException e) {
                log.error("下发自动录像到TSC出错：" + tsc.getURL());
            }
        }
        
        // 当映射表里面的节目删除的时候，也删除SMG对于的节目信息 By: Jiang 2013.6.23
        /* 暂时先注释掉，等测试成功后再用
        String smgDownString = recordString.createForDownXML(bsData, list, "Del", true);
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
            }
        }
        */
        
		//调用删除方法 把删除标记为1的 节目信息清空
		try {
			SetAutoRecordChannelHandle.delChannelRemappingByProgram();
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
}
