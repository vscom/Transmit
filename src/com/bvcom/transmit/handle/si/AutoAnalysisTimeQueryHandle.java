package com.bvcom.transmit.handle.si;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.config.AutoAnalysisTimeQueryConfigFile;
import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.si.AutoAnalysisTimeQueryParse;
import com.bvcom.transmit.task.AutoAnalysisTimeQueryTask;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.si.AutoAnalysisTimeQueryVO;

/**
 * 频道扫描
 * @author Bian Jiang
 * 
 */
public class AutoAnalysisTimeQueryHandle {
    
    private static Logger log = Logger.getLogger(AutoAnalysisTimeQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    public AutoAnalysisTimeQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    public void downXML() {
    	
        UtilXML utilXML = new UtilXML();
        
        String sendString = utilXML.getReturnXML(this.bsData, 0);
        
        try {
            utilXML.SendUpXML(sendString, bsData);
        } catch (CommonException e) {
            log.error("数据业务分析信息失败: " + e.getMessage());
        }
    	
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("数据业务分析 StringToXML Error: " + e.getMessage());
        }
        
        AutoAnalysisTimeQueryParse AutoAnalysisTimeQueryParse = new AutoAnalysisTimeQueryParse();
        AutoAnalysisTimeQueryVO vo = AutoAnalysisTimeQueryParse.getDownObject(document);
        
        AutoAnalysisTimeQueryConfigFile AutoAnalysisTimeQueryConfigFile = new AutoAnalysisTimeQueryConfigFile();
        AutoAnalysisTimeQueryConfigFile.setAutoAnalysisTime(vo.getStartTime());
        
        AutoAnalysisTimeQueryTask.stop();
        try {
        	Thread.sleep(1000);
        } catch(Exception ex) {
        	
        }
         
        AutoAnalysisTimeQueryTask autoAnalysisTimeQueryTask = new AutoAnalysisTimeQueryTask();
        autoAnalysisTimeQueryTask.newScheduler();
        autoAnalysisTimeQueryTask.start();
        
    	// 下发数据业务分析时，进行实时扫描
    	ChannelScanQueryHandle ChannelScanQueryHandle = new ChannelScanQueryHandle();
    	//by tqy 新增加协议头信息
    	ChannelScanQueryHandle.setBsData(this.bsData);
    	ChannelScanQueryHandle.channelScanNow();
    }
    

    
}

