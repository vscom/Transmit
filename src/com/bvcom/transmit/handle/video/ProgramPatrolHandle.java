package com.bvcom.transmit.handle.video;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.video.ProgramPatrolParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

/**
 * 轮巡监测设置
 * @author Bian Jiang
 *
 */
public class ProgramPatrolHandle {

    private static Logger log = Logger.getLogger(ProgramPatrolHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public ProgramPatrolHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 1. 下发给所有的IPM
     * 2. 上报成功信息给中心
     */
    public void downXML() {
        // 返回数据
        String upString = "";
        MemCoreData coreData = MemCoreData.getInstance();
        // 取得IPM配置文件信息
        List IPMList = coreData.getIPMList();
        
        ProgramPatrolParse programPatrolParse = new ProgramPatrolParse();
        
        // IPM 下发指令
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            try {
                // 运行图信息下发 timeout 1000*30 三十秒
            	upString = utilXML.SendDownXML(this.downString, ipm.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
            } catch (CommonException e) {
                log.error("轮巡监测设置IPM下发出错：" + ipm.getURL());
            }
            
            if(upString != null && !upString.trim().equals("")) {
            	break;
            }
            
        } // IPM 下发指令 END
        
        if(upString.equals("")) {
        	upString = utilXML.getReturnXML(this.bsData, 1);
        } else {
            Document document = null;
            try {
                document = utilXML.StringToXML(upString);
            } catch (CommonException e) {
                log.error("轮巡监测设置StringToXML Error: " + e.getMessage());
                log.error("轮巡监测设置: " + upString);
            }
            List<MonitorProgramQueryVO> programPatrolList = programPatrolParse.parseReturnXml(document);
            
            try {
            	document.setXMLEncoding("GB2312");
				MonitorProgramQueryHandle.updataProgramPatrolGroup(programPatrolList, document.asXML());
			} catch (DaoException e) {
				log.error("轮巡监测群号更新数据库失败: " + e.getMessage());
				log.error("轮巡监测设置: " + upString);
			}
            
        }
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("轮巡监测设置上报失败: " + e.getMessage());
        }
        
        bsData = null;
        this.downString = null;
        IPMList = null;
        utilXML = null;
    }
}
