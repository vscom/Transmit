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
 * ȡ�����ݷ���ʱ���ȡ�����ļ�
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
			log.info("��ȡ�Ƿ����ֲ���λ�����ļ�" + filePath);
			
			try {
				inStr = new FileInputStream(filePath); 
			} catch (FileNotFoundException e) {
				log.error("��ȡ�Ƿ����ֲ���λ�����ļ�" + e.getMessage());
				File newFile = new File(filePath);
				newFile.createNewFile();
				inStr = new FileInputStream(filePath); 
			}
			
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
			p.load(inStr);
			StreamRoundInfoQueryURL = p.getProperty("StreamRoundInfoQueryURL");
		} catch (IOException ioe){
			log.error("��ȡ�Ƿ����ֲ���λ�����ļ�ʧ�ܣ�" + ioe.getMessage());
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
			log.info("��ȡ�Ƿ����ֲ���λ�����ļ�" + filePath);
			
			try {
				inStr = new FileInputStream(filePath); 
			} catch (FileNotFoundException e) {
				log.error("��ȡ�Ƿ����ֲ���λ�����ļ�" + e.getMessage());
				File newFile = new File(filePath);
				newFile.createNewFile();
				inStr = new FileInputStream(filePath); 
			}
			
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
			p.load(inStr);
			Time = p.getProperty("StreamRoundInfoQueryTime");
		} catch (IOException ioe){
			log.error("��ȡ�Ƿ����ֲ���λ�����ļ�ʧ�ܣ�" + ioe.getMessage());
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
			log.info("��ȡ�Ƿ����ֲ���λ�����ļ�" + filePath);
			
			try {
				inStr = new FileInputStream(filePath); 
			} catch (FileNotFoundException e) {
				log.error("��ȡ�Ƿ����ֲ���λ�����ļ�" + e.getMessage());
				File newFile = new File(filePath);
				newFile.createNewFile();
				inStr = new FileInputStream(filePath); 
			}
			
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
			p.load(inStr);
			RebootSwitch = p.getProperty("StreamRoundInfoQueryRebootSwitch");
		} catch (IOException ioe){
			log.error("��ȡ�Ƿ����ֲ���λ�����ļ�ʧ�ܣ�" + ioe.getMessage());
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
			log.info("��ȡ�Ƿ��������������ָ��ɨ�������ļ�" + filePath);
			
			try {
				inStr = new FileInputStream(filePath); 
			} catch (FileNotFoundException e) {
				log.error("��ȡ�Ƿ��������������ָ��ɨ�������ļ�" + e.getMessage());
				File newFile = new File(filePath);
				newFile.createNewFile();
				inStr = new FileInputStream(filePath); 
			}
			
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
			p.load(inStr);
			ChannelScanQueryFlag = p.getProperty("ChannelScanQueryFlag");
		} catch (IOException ioe){
			log.error("��ȡ�Ƿ��������������ָ��ɨ�������ļ�ʧ�ܣ�" + ioe.getMessage());
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
        	log.info("��ȡ���ݷ���ʱ���ȡ�����ļ�" + filePath);
            
        	try {
        		inStr = new FileInputStream(filePath); 
	        } catch (FileNotFoundException e) {
	        	log.error("�������ݷ���ʱ���ȡ�����ļ�ʧ�ܣ�" + e.getMessage());
	        	File newFile = new File(filePath);
	        	newFile.createNewFile();
	        	inStr = new FileInputStream(filePath); 
	        }
	        
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
            p.load(inStr);
            StartTime = p.getProperty("AnalysisStartTime");
        } catch (IOException ioe){
            log.error("��ȡ���ݷ���ʱ���ȡ�����ļ�ʧ�ܣ�" + ioe.getMessage());
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
    		log.info("��ȡ���ݷ���ʱ���ȡ�����ļ�" + filePath);
    		
    		try {
    			inStr = new FileInputStream(filePath); 
    		} catch (FileNotFoundException e) {
    			log.error("�������ݷ���ʱ���ȡ�����ļ�ʧ�ܣ�" + e.getMessage());
    			File newFile = new File(filePath);
    			newFile.createNewFile();
    			inStr = new FileInputStream(filePath); 
    		}
    		
//            inStr = this.getClass().getResourceAsStream("/AutoAnalysisTime.properties");
    		p.load(inStr);
    		StartTime = p.getProperty("StreamRoundInfoQueryStop");
    	} catch (IOException ioe){
    		log.error("��ȡ���ݷ���ʱ���ȡ�����ļ�ʧ�ܣ�" + ioe.getMessage());
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
        
    	log.info("�������ݷ���ʱ���ȡ�����ļ�: " + StartTime);
        Properties p = new Properties();
        
        MemCoreData coreData = MemCoreData.getInstance();
        SysInfoVO sysInfoVO = coreData.getSysVO();
        
        String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
        
        InputStream inStr = null;
        
        try {
        	log.info("�������ݷ���ʱ���ȡ�����ļ�" + filePath);
        	try {
        		inStr = new FileInputStream(filePath); 
	        } catch (FileNotFoundException e) {
	        	log.error("�������ݷ���ʱ���ȡ�����ļ�ʧ�ܣ�" + e.getMessage());
	        	File newFile = new File(filePath);
	        	newFile.createNewFile();
	        	inStr = new FileInputStream(filePath); 
	        }
            p.load(inStr);
            
            OutputStream fos = new FileOutputStream(filePath); 
            p.setProperty("AnalysisStartTime", StartTime); 
            //���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ�� 
            //���� Properties ���е������б�����Ԫ�ضԣ�д������� 
            p.store(fos, "Update 'AnalysisStartTime' value"); 
        } catch (IOException ioe){
            log.error("�������ݷ���ʱ���ȡ�����ļ�ʧ�ܣ�" + ioe.getMessage());
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
    	
    	log.info("�������ݷ���ʱ���ȡ�����ļ�: " + StopTime);
    	Properties p = new Properties();
    	
    	MemCoreData coreData = MemCoreData.getInstance();
    	SysInfoVO sysInfoVO = coreData.getSysVO();
    	
    	String filePath = sysInfoVO.getTomcatHome() + "\\webapps\\transmit\\WEB-INF\\classes\\AutoAnalysisTime.properties";
    	
    	InputStream inStr = null;
    	
    	try {
    		log.info("�������ݷ���ʱ���ȡ�����ļ�" + filePath);
    		try {
    			inStr = new FileInputStream(filePath); 
    		} catch (FileNotFoundException e) {
    			log.error("�������ݷ���ʱ���ȡ�����ļ�ʧ�ܣ�" + e.getMessage());
    			File newFile = new File(filePath);
    			newFile.createNewFile();
    			inStr = new FileInputStream(filePath); 
    		}
    		p.load(inStr);
    		
    		OutputStream fos = new FileOutputStream(filePath); 
    		p.setProperty("StreamRoundInfoQueryStop", StopTime); 
    		//���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ�� 
    		//���� Properties ���е������б�����Ԫ�ضԣ�д������� 
    		p.store(fos, "Update 'StreamRoundInfoQueryStop' value"); 
    	} catch (IOException ioe){
    		log.error("�������ݷ���ʱ���ȡ�����ļ�ʧ�ܣ�" + ioe.getMessage());
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
