package com.bvcom.transmit.handle.smginfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.handle.video.MonitorProgramQueryHandle;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class AgentInfoSet {
	
    private static Logger log = Logger.getLogger(AgentInfoSet.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public AgentInfoSet(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
             this.bsData = bsData;
    }
    
    
    /**
     * 1. 解析通道设置协议
     * 2. 修改配置文件与数据库
     */
    public void downXML() {
    	boolean isErr=true;
    	Document document=null;
		try {
			document = utilXML.StringToXML(downString);
		} catch (CommonException e) {
			log.info("字符串转换xml错误："+e.getMessage());
			isErr=false;
		}
		/*
		List<TSCInfoVO> tscs=(List<TSCInfoVO>)coreData.getTSCList();
		for(int j=0;j<tscs.size();j++){
			TSCInfoVO tsc=tscs.get(j);
			isErr=utilXML.SendUpXML(downString, tsc.getURL());
		}
		//发送到Rtvm
		MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
        // 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
        try {
			rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 1);
			if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
				rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 2);
				if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
					rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 3);
					if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 4);
						if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
    						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 5);
    						if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
        						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 0);
    						}
						}	
					}
				}	
			}
		} catch (DaoException e1) {
			log.error("取得RTVM的URL错误: " + e1.getMessage());
			isErr=false;
		}
		isErr=utilXML.SendUpXML(downString, rtvsVO.getRTVSResetURL());
		*/
		
		//解析xml修改跟transmit相关配置项
    	List<String> AgentInfoList=parse(document);
    	for(int i=0;i<AgentInfoList.size();i++){
    		String[] agent=AgentInfoList.get(i).split(",");
    		String value=agent[2].trim();
    		//SI 类型包括类型为（1、2、3、4、8、10）
    		if(value.equalsIgnoreCase("SI")){
    			for(int j=0;j<AgentInfoList.size();j++){
    				String[] agen=AgentInfoList.get(j).split(",");
    				int Type=Integer.parseInt(agen[0].trim());
    				String Value=agen[2].trim();
	    			if(Type==1){
	    				coreData.getSysVO().setAgentName(Value);
	        		}else if(Type==2){
	        			coreData.getSysVO().setAgentType(Value);
	        		}else if(Type==3){
	        			coreData.getSysVO().setSrcCode(Value);
	        		}else if(Type==4){
	        			coreData.getSysVO().setCenterToAgentURL(Value);
	        		}else if(Type==8){
	        			coreData.getSysVO().setPSIInfoFilePath(Value);
	        		}else if(Type==10){
	        			coreData.getSysVO().setLogFilePath(Value);
	        		}
    			}
    		}
    		//VIDEO 类型包括类型为（1、2、3、4、9、10、11、12:马赛克轮播路径）
    		if(value.equalsIgnoreCase("VIDEO")){
    			for(int j=0;j<AgentInfoList.size();j++){
    				String[] agen=AgentInfoList.get(j).split(",");
    				int Type=Integer.parseInt(agen[0].trim());
    				String Value=agen[2].trim();
	    			if(Type==1){
	    				coreData.getSysVO().setAgentName(Value);
	        		}else if(Type==2){
	        			coreData.getSysVO().setAgentType(Value);
	        		}else if(Type==3){
	        			coreData.getSysVO().setSrcCode(Value);
	        		}else if(Type==4){
	        			coreData.getSysVO().setCenterToAgentURL(Value);
	        		}else if(Type==9){
        			coreData.getSysVO().setRecordFilePath(Value);
	        		}else if(Type==10){
	        			coreData.getSysVO().setLogFilePath(Value);
	        		}else if(Type==11){
	        			coreData.getSysVO().setCenterAlarmURL(Value);
	        		}else if(Type==12){
	        			coreData.getSysVO().setCenterRoundChannelURL(Value);
	        		}
    			}
    		}
    		//SMS 类型包括类型为（1、2、3、4、5、7、10）  5,7
    		/*由SMS、CAS程序处理
    		if(value.equalsIgnoreCase("SMS")){
    			for(int j=0;j<AgentInfoList.size();j++){
    				String[] agen=AgentInfoList.get(j).split(",");
    				int Type=Integer.parseInt(agen[0].trim());
    				String Value=agen[2].trim();
	    			if(Type==1){
	    				coreData.getSysVO().setAgentName(Value);
	        		}else if(Type==2){
	        			coreData.getSysVO().setAgentType(Value);
	        		}else if(Type==3){
	        			coreData.getSysVO().setSrcCode(Value);
	        		}else if(Type==4){
	        			coreData.getSysVO().setCenterToAgentURL(Value);
	        		}else if(Type==10){
	        			coreData.getSysVO().setReceFilePath(Value);
	        		}
	        	}
    		}
    		//CAS 类型包括类型为（1、2、3、4、5、6、10）  5,6
    		if(value.equalsIgnoreCase("CAS")){
    			for(int j=0;j<AgentInfoList.size();j++){
    				String[] agen=AgentInfoList.get(j).split(",");
    				int Type=Integer.parseInt(agen[0].trim());
    				String Value=agen[2].trim();
	    			if(Type==1){
	    				coreData.getSysVO().setAgentName(Value);
	        		}else if(Type==2){
	        			coreData.getSysVO().setAgentType(Value);
	        		}else if(Type==3){
	        			coreData.getSysVO().setSrcCode(Value);
	        		}else if(Type==4){
	        			coreData.getSysVO().setCenterToAgentURL(Value);
	        		}else if(Type==10){
	        			coreData.getSysVO().setReceFilePath(Value);
	        		}
	        	}
    		}
    		*/
    	}
    	String returnstr="";
    	
    	//封装MemCoreData对象的内容 保存TransmitConfig.xml
    	isErr=saveMemCoreDataToTransmitConfig(coreData);
    	
    	if(isErr){
    		returnstr = getReturnXML(this.bsData, 0);
    	}else{
    		returnstr = getReturnXML(this.bsData, 1);
    	}
        try {
            utilXML.SendUpXML(returnstr, bsData);
        } catch (CommonException e) {
            log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
        }
        
        
        
    }
    private List<String> parse(Document document){
    	List<String> list=new ArrayList<String>();
    	Element root=document.getRootElement();
    	for (Iterator<Element> iter=root.elementIterator(); iter.hasNext(); ) {
			Element AgentInfoSet =iter.next();
			for(Iterator<Element> ite=AgentInfoSet.elementIterator();ite.hasNext();){
				Element AgentInfoSetRecord=ite.next();
				String Type=AgentInfoSetRecord.attributeValue("Type");
				String Desc=AgentInfoSetRecord.attributeValue("Desc");
				String Value=AgentInfoSetRecord.attributeValue("Value");
				list.add(Type+","+Desc+","+Value);
			}
		}
    	return list;
    }
    private boolean saveMemCoreDataToTransmitConfig(MemCoreData memCoreData){
    	boolean isErr=true;
    	StringBuffer transmitConfigBuffer=new StringBuffer();
    	transmitConfigBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		transmitConfigBuffer.append("<ROOT>\n");
		transmitConfigBuffer.append("\t<SMG>\n");
		List<SMGCardInfoVO> smgList=memCoreData.getSMGCardList(); 
		for (int i = 0; i < smgList.size(); i++) {
			SMGCardInfoVO smg=smgList.get(i);
			transmitConfigBuffer.append("\t\t<SMGCardInfo Index=\""+smg.getIndex()+"\"  URL=\""+smg.getURL()+"\" HDFlag=\""+smg.getHDFlag()+"\" HDURL=\""+smg.getHDURL()+"\" IndexType=\""+smg.getIndexType()+"\" />\n");
		}
		transmitConfigBuffer.append("\t</SMG>\n");
		
		
		transmitConfigBuffer.append("\t<IPM>\n");
		List<IPMInfoVO> ipmList=memCoreData.getIPMList(); 
		for (int i = 0; i < ipmList.size(); i++) {
			IPMInfoVO ipm=ipmList.get(i);
			transmitConfigBuffer.append("\t\t<IPMInfo IndexMin=\""+ipm.getIndexMin()+"\" IndexMax=\""+ipm.getIndexMax()+"\" RecordType=\""+ipm.getRecordType()+"\"  URL=\""+ipm.getURL()+"\"  SysURL=\""+ipm.getSysURL()+"\"/>\n");
		}
		transmitConfigBuffer.append("\t</IPM>\n");
		
		transmitConfigBuffer.append("\t<TSC>\n");
		List<TSCInfoVO> tscList=memCoreData.getTSCList();
		for (int i = 0; i < tscList.size(); i++) {
			TSCInfoVO tsc=tscList.get(i);
			transmitConfigBuffer.append("\t\t<TSCInfo IndexMin=\""+tsc.getIndexMin()+"\" IndexMax=\""+tsc.getIndexMax()+"\" URL=\""+tsc.getURL()+"\" RecordType=\""+tsc.getRecordType()+"\" SysURL=\""+tsc.getSysURL()+"\"/>\n");
		}	
		transmitConfigBuffer.append("\t</TSC>\n");
		
		SysInfoVO system=memCoreData.getSysVO();
		transmitConfigBuffer.append("\t<SYSTEM>\n");
		transmitConfigBuffer.append("\t\t<SysInfo MaxRecordMbpsFlag=\""+system.getMaxRecordMbpsFlag()+"\" IsHasAlarmID=\""+system.getIsHasAlarmID()+"\" IsEPGZip=\""+system.getIsEPGZip()+"\" IsEPGFromDataBase=\""+system.getIsEPGFromDataBase()+"\" IsAutoAlarmReply=\""+system.getIsAutoAlarmReply()+"\" MaxAutoRecordNum=\""+system.getMaxAutoRecordNum()+"\" RecordFilePath =\""+system.getRecordFilePath()+"\" CenterRoundChannelURL=\""+system.getCenterRoundChannelURL()+"\" CenterAlarmURL=\""+system.getCenterAlarmURL()+"\" SrcCode=\""+system.getSrcCode()+"\" DstCode=\""+system.getDstCode()+"\" AgentType=\""+system.getAgentType()+"\" CenterToAgentURL=\""+system.getCenterToAgentURL()+"\" AgentToCenterURL=\""+system.getAgentToCenterURL()+"\"/>\n");
		transmitConfigBuffer.append("\t\t<SysLog  IsAlarmLogEnable=\""+system.getIsAlarmLogEnable()+"\" LogFilePath=\""+system.getLogFilePath()+"\" receFilePath=\""+system.getReceFilePath()+"\" sendFilePath=\""+system.getSendFilePath()+"\" alarmFilePath=\""+system.getAlarmFilePath()+"\" sendErrorFilePath=\""+system.getSendErrorFilePath()+"\"/>\n");
		transmitConfigBuffer.append("\t\t<PSIInfo MHPInfoFilePath=\""+system.getMHPInfoFilePath()+"\" EPGInfoFilePath=\""+system.getEPGInfoFilePath()+"\" PSIInfoFilePath=\""+system.getPSIInfoFilePath()+"\" TSGrabURL=\""+system.getTSGrabURL()+"\"/>\n");
		transmitConfigBuffer.append("\t\t<Tomcat TomcatHomePath=\""+system.getTomcatHome()+"\" TomcatPort=\""+system.getTomcatPort()+"\" LocalRedirectIp=\""+system.getLocalRedirectIp()+"\"/>\n");
		transmitConfigBuffer.append("\t</SYSTEM>\n");
		transmitConfigBuffer.append("</ROOT>");
    	
    	
    	String fileStr=getConfigFilePath();
		int size=CommonUtility.StoreIntoFile(transmitConfigBuffer.toString(), fileStr);
    	if(size==0){
    		isErr=false;
    	}
    	return isErr;
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
            CommonUtility.printErrorTrace(e);
        } catch (IOException ioe){
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
    private String getReturnXML(MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\"/>\r\n");
            strBuf.append("<ErrReport>\r\n");
            strBuf.append("<RebootSetRecord Comment=\"内部错误\"/>\r\n");
            strBuf.append("</ErrReport>\r\n");
        }
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
}

















