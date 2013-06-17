package com.bvcom.transmit.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.SysInfoVO;

/**
 * 取得数据分析时间存取配置文件
 * @author Bian Jiang
 * @data 2010.11.30
 *
 */
public class AutoAnalysisTimeQueryConfigFile {
	
	static Logger log = Logger.getLogger(AutoAnalysisTimeQueryConfigFile.class.getSimpleName());
	
	public String getStreamRoundInfoQueryURL() {
		
		Properties p = new Properties();
		
		String StreamRoundInfoQueryURL = "";
		InputStream inStr = null;
		
		MemCoreData coreData = MemCoreData.getInstance();
		SysInfoVO sysInfoVO = coreData.getSysVO();
		
		String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
		
		try {
			log.info("读取是否开启轮播复位配置文件" + filePath);
			
			try {
				inStr = new FileInputStream(filePath); 
			} catch (FileNotFoundException e) {
				log.error("读取是否开启轮播复位配置文件" + e.getMessage());
				File newFile = new File(filePath);
				newFile.createNewFile();
				inStr = new FileInputStream(filePath); 
			}
			
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
			p.load(inStr);
			StreamRoundInfoQueryURL = p.getProperty("StreamRoundInfoQueryURL");
		} catch (IOException ioe){
			log.error("读取是否开启轮播复位配置文件失败：" + ioe.getMessage());
			//CommonUtility.printErrorTrace(ioe);
			//StartTime = "2:0:0";
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
		return StreamRoundInfoQueryURL;
	}
	public String getStreamRoundInfoQueryTime() {
		
		Properties p = new Properties();
		
		String Time = "";
		InputStream inStr = null;
		
		MemCoreData coreData = MemCoreData.getInstance();
		SysInfoVO sysInfoVO = coreData.getSysVO();
		
		String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
		
		try {
			log.info("读取是否开启轮播复位配置文件" + filePath);
			
			try {
				inStr = new FileInputStream(filePath); 
			} catch (FileNotFoundException e) {
				log.error("读取是否开启轮播复位配置文件" + e.getMessage());
				File newFile = new File(filePath);
				newFile.createNewFile();
				inStr = new FileInputStream(filePath); 
			}
			
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
			p.load(inStr);
			Time = p.getProperty("StreamRoundInfoQueryTime");
		} catch (IOException ioe){
			log.error("读取是否开启轮播复位配置文件失败：" + ioe.getMessage());
			//CommonUtility.printErrorTrace(ioe);
			//StartTime = "2:0:0";
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
		return Time;
	}
	public String getStreamRoundInfoQueryRebootSwitch() {
		
		Properties p = new Properties();
		
		String RebootSwitch = "";
		InputStream inStr = null;
		
		MemCoreData coreData = MemCoreData.getInstance();
		SysInfoVO sysInfoVO = coreData.getSysVO();
		
		String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
		
		try {
			log.info("读取是否开启轮播复位配置文件" + filePath);
			
			try {
				inStr = new FileInputStream(filePath); 
			} catch (FileNotFoundException e) {
				log.error("读取是否开启轮播复位配置文件" + e.getMessage());
				File newFile = new File(filePath);
				newFile.createNewFile();
				inStr = new FileInputStream(filePath); 
			}
			
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
			p.load(inStr);
			RebootSwitch = p.getProperty("StreamRoundInfoQueryRebootSwitch");
		} catch (IOException ioe){
			log.error("读取是否开启轮播复位配置文件失败：" + ioe.getMessage());
			//CommonUtility.printErrorTrace(ioe);
			//StartTime = "2:0:0";
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
		return RebootSwitch;
	}
	
	public String getChannelScanQueryFlag() {
		
		Properties p = new Properties();
		
		String ChannelScanQueryFlag = "";
		InputStream inStr = null;
		
		MemCoreData coreData = MemCoreData.getInstance();
		SysInfoVO sysInfoVO = coreData.getSysVO();
		
		String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
		
		try {
			log.info("读取是否开启监测中心四期指定扫描配置文件" + filePath);
			
			try {
				inStr = new FileInputStream(filePath); 
			} catch (FileNotFoundException e) {
				log.error("读取是否开启监测中心四期指定扫描配置文件" + e.getMessage());
				File newFile = new File(filePath);
				newFile.createNewFile();
				inStr = new FileInputStream(filePath); 
			}
			
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
			p.load(inStr);
			ChannelScanQueryFlag = p.getProperty("ChannelScanQueryFlag");
		} catch (IOException ioe){
			log.error("读取是否开启监测中心四期指定扫描配置文件失败：" + ioe.getMessage());
			//CommonUtility.printErrorTrace(ioe);
			//StartTime = "2:0:0";
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
		return ChannelScanQueryFlag;
	}
    public String getAutoAnalysisTime() {
        
        Properties p = new Properties();
        
        String StartTime = "";
        InputStream inStr = null;
        
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysInfoVO = coreData.getSysVO();
        
        String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
        
        try {
        	log.info("读取数据分析时间存取配置文件" + filePath);
            
        	try {
        		inStr = new FileInputStream(filePath); 
	        } catch (FileNotFoundException e) {
	        	log.error("设置数据分析时间存取配置文件失败：" + e.getMessage());
	        	File newFile = new File(filePath);
	        	newFile.createNewFile();
	        	inStr = new FileInputStream(filePath); 
	        }
	        
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
            p.load(inStr);
            StartTime = p.getProperty("AnalysisStartTime");
        } catch (IOException ioe){
            log.error("读取数据分析时间存取配置文件失败：" + ioe.getMessage());
            //CommonUtility.printErrorTrace(ioe);
            StartTime = "2:0:0";
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
        return StartTime;
    }
    
    public String getStreamRoundInfoQueryStopTime() {
    	
    	Properties p = new Properties();
    	
    	String StartTime = "";
    	InputStream inStr = null;
    	
    	MemCoreData coreData = MemCoreData.getInstance();
    	SysInfoVO sysInfoVO = coreData.getSysVO();
    	
    	String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
    	
    	try {
    		log.info("读取数据分析时间存取配置文件" + filePath);
    		
    		try {
    			inStr = new FileInputStream(filePath); 
    		} catch (FileNotFoundException e) {
    			log.error("设置数据分析时间存取配置文件失败：" + e.getMessage());
    			File newFile = new File(filePath);
    			newFile.createNewFile();
    			inStr = new FileInputStream(filePath); 
    		}
    		
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
    		p.load(inStr);
    		StartTime = p.getProperty("StreamRoundInfoQueryStop");
    	} catch (IOException ioe){
    		log.error("读取数据分析时间存取配置文件失败：" + ioe.getMessage());
    		//CommonUtility.printErrorTrace(ioe);
    		StartTime = "2011-01-01 00:00:00";
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
    	return StartTime;
    }
    
    public void setAutoAnalysisTime(String StartTime) {
        
    	log.info("设置数据分析时间存取配置文件: " + StartTime);
        Properties p = new Properties();
        
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysInfoVO = coreData.getSysVO();
        
        String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
        
        InputStream inStr = null;
        
        try {
        	log.info("设置数据分析时间存取配置文件" + filePath);
        	try {
        		inStr = new FileInputStream(filePath); 
	        } catch (FileNotFoundException e) {
	        	log.error("设置数据分析时间存取配置文件失败：" + e.getMessage());
	        	File newFile = new File(filePath);
	        	newFile.createNewFile();
	        	inStr = new FileInputStream(filePath); 
	        }
            p.load(inStr);
            
            OutputStream fos = new FileOutputStream(filePath); 
            p.setProperty("AnalysisStartTime", StartTime); 
            //以适合使用 load 方法加载到 Properties 表中的格式， 
            //将此 Properties 表中的属性列表（键和元素对）写入输出流 
            p.store(fos, "Update 'AnalysisStartTime' value"); 
        } catch (IOException ioe){
            log.error("设置数据分析时间存取配置文件失败：" + ioe.getMessage());
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
    }
    
    public void setStreamRoundInfoQueryStopTime(String StopTime) {
    	
    	log.info("设置数据分析时间存取配置文件: " + StopTime);
    	Properties p = new Properties();
    	
    	MemCoreData coreData = MemCoreData.getInstance();
    	SysInfoVO sysInfoVO = coreData.getSysVO();
    	
    	String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
    	
    	InputStream inStr = null;
    	
    	try {
    		log.info("设置数据分析时间存取配置文件" + filePath);
    		try {
    			inStr = new FileInputStream(filePath); 
    		} catch (FileNotFoundException e) {
    			log.error("设置数据分析时间存取配置文件失败：" + e.getMessage());
    			File newFile = new File(filePath);
    			newFile.createNewFile();
    			inStr = new FileInputStream(filePath); 
    		}
    		p.load(inStr);
    		
    		OutputStream fos = new FileOutputStream(filePath); 
    		p.setProperty("StreamRoundInfoQueryStop", StopTime); 
    		//以适合使用 load 方法加载到 Properties 表中的格式， 
    		//将此 Properties 表中的属性列表（键和元素对）写入输出流 
    		p.store(fos, "Update 'StreamRoundInfoQueryStop' value"); 
    	} catch (IOException ioe){
    		log.error("设置数据分析时间存取配置文件失败：" + ioe.getMessage());
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
    }
    
}
