package com.bvcom.transmit.handle.si;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.si.MHPQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.si.MHPQueryVO;

public class MHPQueryHandle {
	
	private static Logger log = Logger.getLogger(MHPQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public MHPQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
	public void downXML(){
		log.info("读取MHP信息");
		// MHP信息 
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("读取MHP StringToXML Error: " + e.getMessage());
        }
		
        MHPQueryParse MHPQueryParse = new MHPQueryParse();
        
        MHPQueryVO vo = MHPQueryParse.getDownXml(document);
        
        SysInfoVO sysVO = coreData.getSysVO();
        
		String data = CommonUtility.getDateHourPath(vo.getScanTime());
		
		String filename = CommonUtility.getTableFilePath(sysVO.getMHPInfoFilePath()+ "/" + data);
		
		log.info("MHP File Path: " + filename);
		
		String mhpPath = sysVO.getMHPInfoFilePath() + "\\";
		String webPath = filename.substring(mhpPath.length());
		
		log.info("MHP webPath Path: " + webPath);
		
		String redirect = "http://" + sysVO.getLocalRedirectIp() + ":"
		+ sysVO.getTomcatPort() + "/MHPInfo/" + webPath;
		
		vo.setFtp(redirect);
		
		String returnStr = MHPQueryParse.getMHPReturnXML(bsData, vo, 0);
		
        try {
            utilXML.SendUpXML(returnStr, bsData);
        } catch (CommonException e) {
            log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
        }
		
		
	}
    
}
