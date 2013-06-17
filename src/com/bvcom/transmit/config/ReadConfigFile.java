/**
 * transmit (java转发)
 * 
 * ReadConfigFile.java    2009.11.12
 * 
 * Copyright 2009 BVCOM. All Rights Reserved.
 * 
 */
package com.bvcom.transmit.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.AlarmSwitchMemory;
import com.bvcom.transmit.util.AlarmTimeMemory;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.util.XMLExt;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;

/**
 * 
 *  从配置文件读取配置信息
 * 
 * @version  V1.0
 * @author Bian Jiang
 * @Date 2009.11.12
 */
public class ReadConfigFile {
    
    static Logger log = Logger.getLogger(ReadConfigFile.class.getSimpleName());
    
    public void initConfig() {
    	//增加方法把报警开关状态放入内存 Ji Long 2011-5-13
    	new AlarmSwitchMemory().alarmSwitchToMemory();
    	//增加方法把运行图节目信息放入内存 JI Long 2011-06-15
    	new AlarmTimeMemory().alarmTimeToMemory();
    	//
        String filePath = getConfigFilePath();
        getConfigFileInfo(filePath);
    }
    
    private String getConfigFilePath() {
        
        Properties p = new Properties();
        
        String filePath = "";
        InputStream inStr = null;
        
        try {
            inStr = this.getClass().getResourceAsStream("/config.properties");
            p.load(inStr);
            filePath = p.getProperty("configFilePath");
        } catch (FileNotFoundException e) {
            log.error("读取配置文件失败：" + e.getMessage());
            CommonUtility.printErrorTrace(e);
        } catch (IOException ioe){
            log.error("读取配置文件失败：" + ioe.getMessage());
            CommonUtility.printErrorTrace(ioe);
        } finally {
            try {
                if (inStr != null) {
                    inStr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.clear();
        }
        return filePath;
    }
    
    
    @SuppressWarnings("unchecked")
	private void getConfigFileInfo(String filePath) {
        
        UtilXML xmlutil = new UtilXML();
        log.info("Start Read Config File：" + filePath);
        Document document = xmlutil.ReadFromFile(filePath);
        
        MemCoreData coreDate = MemCoreData.getInstance();
        
        List SMGCardList = coreDate.getSMGCardList();
        
        List IPMList = coreDate.getIPMList();
        
        List TSCList = coreDate.getTSCList();
        
        SysInfoVO sysVO = coreDate.getSysVO();
        
        // Get SMGCard List Element 
        
        //程序重新启动后，要从数据库中读取数据、
        //判断SMG_CARD_INFO表中是否有数据，有则读取数据库信息，否则读取TransmitConfig.xml配置文件
        int count =0;
        try
        {
	    	Statement statement = null;
			ResultSet rs = null;
			Connection conn = DaoSupport.getJDBCConnection();
			StringBuffer strBuff1 = new StringBuffer();
			//strBuff1.append("select count(*) from smg_card_info where smgIndex="+smgIndex+" and smgInputtype="+smgInputtype);
			strBuff1.append("select count(*) from smg_card_info ");
			try {
				statement = conn.createStatement();
				rs = statement.executeQuery(strBuff1.toString());
				while(rs.next()){
					count =rs.getInt(1);
					}
			} catch (Exception e) {
			} finally {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
			}
        }catch(Exception ex){
        	
        }
        
        if(count>0){
        	try
        	{
	        	Statement statement = null;
				Connection conn = DaoSupport.getJDBCConnection();
				ResultSet rs = null;
				StringBuffer strBuff = new StringBuffer();
				strBuff.append("select *  from smg_card_info order by smgIndex ");
				try {
					statement = conn.createStatement();
					rs = statement.executeQuery(strBuff.toString());
					while(rs.next()){
						 SMGCardInfoVO smgCardInfo = new SMGCardInfoVO();
						 //获取通道号
						 smgCardInfo.setIndex(rs.getInt("smgIndex"));
						 //SMGURL
						 smgCardInfo.setURL(rs.getString("smgURL"));
						 //HDFLAG
						 smgCardInfo.setHDFlag(0);
						 //HDURL
						 smgCardInfo.setHDURL("http://192.168.0.100/Setup1");
						 
						 //通道业务类型
						 @SuppressWarnings("unused")
						 int inputtype=rs.getInt("smgInputtype");
						 switch(inputtype){
						 case 0://停用
							   smgCardInfo.setIndexType("Stop");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 1://实时视频
							   smgCardInfo.setIndexType("ChangeProgramQuery");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 2://轮播辅助
							   smgCardInfo.setIndexType("StreamRoundInfoQuery");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 3://轮循测量
							   smgCardInfo.setIndexType("AutoAnalysisTimeQuery");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 4://录像
							   smgCardInfo.setIndexType("AutoRecord");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 5://空闲
							   smgCardInfo.setIndexType("Free");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 6://频道扫、指标查询
							   smgCardInfo.setIndexType("ChannelScanQuery");
							   SMGCardList.add(smgCardInfo);
							   break;
						 case 7://指标查询
							  smgCardInfo.setIndexType("GetIndexSet");
							  SMGCardList.add(smgCardInfo);
							  break;
						 }
						 
						 
						 //=================频道扫描和指标查询占用一个通道=============
//						 if(inputtype==6){
//							 SMGCardInfoVO smgCardInfo1 = new SMGCardInfoVO();
//							 //获取通道号
//							 smgCardInfo1.setIndex(rs.getInt("smgIndex"));
//							 //SMGURL
//							 smgCardInfo1.setURL(rs.getString("smgURL"));
//							 //HDFLAG
//							 smgCardInfo1.setHDFlag(0);
//							 //HDURL
//							 smgCardInfo1.setHDURL("http://192.168.0.100/Setup1");
//							 smgCardInfo1.setIndexType("GetIndexSet");
//							 SMGCardList.add(smgCardInfo1);
//						 }
					}
				}
				catch(Exception ex){
					
				}
				finally {
					DaoSupport.close(rs);
					DaoSupport.close(statement);
				}
				strBuff = null;
				DaoSupport.close(conn);
        	}catch(Exception e){
        		
        	}
        }
        else
        {
        	  Element SMGElement =(Element)document.selectSingleNode("//ROOT//SMG");
              
              List SMGList = SMGElement.elements();
              
              for (int i=0; i< SMGList.size(); i++) {
                  Element SMGCardInfo = (Element)SMGList.get(i);
                  
                  SMGCardInfoVO smgCardInfo = new SMGCardInfoVO();
                  
                  // 取得通道信息
                  smgCardInfo.setIndex(Integer.valueOf(SMGCardInfo.attribute("Index").getValue()));
                  
                  // 通道类型(1:ChangeProgramQuery(手动选台, 频道扫描 和指标) 2:GetIndexSet(指标查询)
                  smgCardInfo.setIndexType(SMGCardInfo.attribute("IndexType").getValue());
                  
                  // IP
                  //smgCardInfo.setIP(SMGCardInfo.attribute("IP").getValue());
                  // url
                  smgCardInfo.setURL(SMGCardInfo.attribute("URL").getValue());
                  // 高清转码标记
                  smgCardInfo.setHDFlag(Integer.valueOf(SMGCardInfo.attribute("HDFlag").getValue()));
                  // 高清转码URL
                  smgCardInfo.setHDURL(SMGCardInfo.attribute("HDURL").getValue());
                  
                  SMGCardList.add(smgCardInfo);
              }
        }
        
        // Get IPM List Element 
        Element IPMElement =(Element)document.selectSingleNode("//ROOT//IPM");
        
        List IPMElementList = IPMElement.elements();
        
        for (int i=0; i< IPMElementList.size(); i++) {
            Element IPMInfoElement = (Element)IPMElementList.get(i);
            
            IPMInfoVO IPMInfo = new IPMInfoVO();
            
            // 取得通道信息
            IPMInfo.setIndexMin(Integer.valueOf(IPMInfoElement.attribute("IndexMin").getValue()));
            
            IPMInfo.setIndexMax(Integer.valueOf(IPMInfoElement.attribute("IndexMax").getValue()));
            
            // IP
            //IPMInfo.setIP(IPMInfoElement.attribute("IP").getValue());
            IPMInfo.setSysURL(IPMInfoElement.attribute("SysURL").getValue());
            
            IPMInfo.setRecordType(Integer.valueOf(IPMInfoElement.attribute("RecordType").getValue()));
            
            IPMInfo.setURL(IPMInfoElement.attribute("URL").getValue());
            
            IPMList.add(IPMInfo);
        }
        
        
        // Get TSC List Element 
        Element TSCElement =(Element)document.selectSingleNode("//ROOT//TSC");
        
        List TSCElementList = TSCElement.elements();
        
        for (int i=0; i< TSCElementList.size(); i++) {
            Element TSCInfoElement = (Element)TSCElementList.get(i);
            
            TSCInfoVO TSCInfo = new TSCInfoVO();
            
            // 取得通道信息
            TSCInfo.setIndexMin(Integer.valueOf(TSCInfoElement.attribute("IndexMin").getValue()));
            
            TSCInfo.setIndexMax(Integer.valueOf(TSCInfoElement.attribute("IndexMax").getValue()));
            
            // IP
            //TSCInfo.setIP(TSCInfoElement.attribute("IP").getValue());
            
            TSCInfo.setURL(TSCInfoElement.attribute("URL").getValue());
            
//            TSCInfo.setStreamRateURL(TSCInfoElement.attribute("SteamRateURL").getValue());
            
            TSCInfo.setSysURL(TSCInfoElement.attribute("SysURL").getValue());
            TSCInfo.setRecordType(Integer.valueOf(TSCInfoElement.attribute("RecordType").getValue()));
            
            TSCList.add(TSCInfo);
        }
        
        //by tqy 四期
        try
        {
        sysVO.setCenterAlarmURL(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@CenterRoundChannelURL", document));
        }
        catch(Exception ex){
        	sysVO.setCenterRoundChannelURL("");
        	log.info("轮播节目上报URL:" + "请检查TransmitConfig.xml的CenterRoundChannelURL节点是否存在！");
        }
        
        
        sysVO.setCenterAlarmURL(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@CenterAlarmURL", document));
        sysVO.setAgentToCenterURL(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@AgentToCenterURL", document));
        sysVO.setCenterToAgentURL(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@CenterToAgentURL", document));
        sysVO.setDstCode(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@DstCode", document));
        
    	sysVO.setSrcCode(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@SrcCode", document));
    	
    	//sysVO.setAgentName(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@AgentName", document));
    	sysVO.setAgentType(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@AgentType", document));
    	
        // MaxAutoRecordNum
        sysVO.setMaxAutoRecordNum(Integer.valueOf(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@MaxAutoRecordNum", document)));
        
        try {
        	// 是否打包为ZIP，默认为打包ZIP
        	sysVO.setIsEPGZip(Integer.valueOf(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@IsEPGZip", document)));
		} catch (Exception ex) {
			sysVO.setIsEPGZip(1);
		}
		
        try {
    	    /**
    	     * EPG信息是否从数据库取得 0:不从数据库取得数据 1:从数据库取得
    	     */
        	sysVO.setIsEPGFromDataBase(Integer.valueOf(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@IsEPGFromDataBase", document)));
		} catch (Exception ex) {
			sysVO.setIsEPGFromDataBase(0);
		}
		
        try {
        	// 报警信息是否主动不报 0:不补报报警信息 1:补报报警信息
        	sysVO.setIsAutoAlarmReply(Integer.valueOf(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@IsAutoAlarmReply", document)));
		} catch (Exception ex) {
			sysVO.setIsEPGZip(0);
		}
		/**
		 * MaxRecordMbpsFlag 允许 加大码率后录制的个数
		 */
		
		try {
        	sysVO.setMaxRecordMbpsFlag(Integer.valueOf(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@MaxRecordMbpsFlag", document)));
		} catch (Exception ex) {
			sysVO.setMaxRecordMbpsFlag(0);
		}
        
	    /**
	     * IsHasAlarmID 是否存在AlarmID, 报警数据是否入库， 0: 不入库 1:入库
	     */
        try {
        	sysVO.setIsHasAlarmID(Integer.valueOf(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@IsHasAlarmID", document)));
		} catch (Exception ex) {
			sysVO.setIsHasAlarmID(0);
		}
		
		
		
		try {
            /**
             * 录像文件保存路径
             */
        	sysVO.setRecordFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysInfo/@RecordFilePath", document));
		} catch (Exception ex) {
			sysVO.setRecordFilePath("D:\\Loging\\RecordFile");
		}
		
		
        try {
            /**
             * 报警数据保存日志使能标记
             */
        	sysVO.setIsAlarmLogEnable(Integer.valueOf(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysLog/@IsAlarmLogEnable", document)));
		} catch (Exception ex) {
			sysVO.setIsAlarmLogEnable(0);
		}
		
		
		//By TQY 四期日志文件存放路径
		try {
        	sysVO.setReceFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysLog/@LogFilePath", document));
		} catch (Exception ex) {
			sysVO.setReceFilePath("D:\\Loging\\");
		}
		
        try {
        	sysVO.setReceFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysLog/@receFilePath", document));
		} catch (Exception ex) {
			sysVO.setReceFilePath("D:\\Loging\\ReceCenterFile");
		}
        
        try {
	        sysVO.setSendFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysLog/@sendFilePath", document));
		} catch (Exception ex) {
			sysVO.setSendFilePath("D:\\Loging\\SendUpFile");
		}
        
        try {
	    	sysVO.setAlarmFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysLog/@alarmFilePath", document));
		} catch (Exception ex) {
			sysVO.setAlarmFilePath("D:\\Loging\\AlarmUpFile");
		}
	
    	try {
    		sysVO.setSendErrorFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/SysLog/@sendErrorFilePath", document));
    	} catch (Exception ex) {
    		sysVO.setSendErrorFilePath("D:\\Loging\\ErrorUpFile");
    	}
        
        // PSI 信息
        sysVO.setEPGInfoFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/PSIInfo/@EPGInfoFilePath", document));
        sysVO.setMHPInfoFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/PSIInfo/@MHPInfoFilePath", document));
        sysVO.setPSIInfoFilePath(XMLExt.getAttributeValue("/ROOT/SYSTEM/PSIInfo/@PSIInfoFilePath", document));
        sysVO.setTSGrabURL(XMLExt.getAttributeValue("/ROOT/SYSTEM/PSIInfo/@TSGrabURL", document));
        
        // TOMCAT
        sysVO.setTomcatHome(XMLExt.getAttributeValue("/ROOT/SYSTEM/Tomcat/@TomcatHomePath", document));
        sysVO.setTomcatPort(XMLExt.getAttributeValue("/ROOT/SYSTEM/Tomcat/@TomcatPort", document));
        sysVO.setLocalRedirectIp(XMLExt.getAttributeValue("/ROOT/SYSTEM/Tomcat/@LocalRedirectIp", document));
        
        AutoAnalysisTimeQueryConfigFile AutoAnalysisTimeQueryConfigFile = new AutoAnalysisTimeQueryConfigFile();
        String startTime = AutoAnalysisTimeQueryConfigFile.getAutoAnalysisTime();
        sysVO.setAutoAnalysisStartTime(startTime);
        
        log.info("End Read Config File：" + filePath);
    }
    
    
}
