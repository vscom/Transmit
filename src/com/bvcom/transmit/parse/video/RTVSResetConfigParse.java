package com.bvcom.transmit.parse.video;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.si.ConstellationQueryVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class RTVSResetConfigParse {

	public String createForDownXML(MSGHeadVO bsData, MonitorProgramQueryVO vo){
		StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getSrcCode()
                + "\" DstCode=\"" + bsData.getDstCode() + "\" SrcURL=\"" + bsData.getSrcURL() + "\" Priority=\"" + bsData.getPriority() + "\">\r\n");
        
        strBuff.append("<RTVSResetConfig RunTime=\""+vo.getRunTime()+"\">\r\n");
    	strBuff.append(" <RTVSConfig  Index=\"" + vo.getIndex() + "\" Freq=\"" + vo.getFreq() + "\" ");
    	strBuff.append(" ServiceID=\""+ vo.getServiceID() +"\"");
    	
    	//2012-07-16 added by tqy
    	strBuff.append(" ViedoPID=\""+ vo.getVideoPID() +"\"");
    	strBuff.append(" AudioPID=\""+ vo.getAudioPID() +"\"");
    	
    	//如果CodingFormat="cbr" Width="960"  Height="544" Fps="25" Bps="1500000" 
    	try
    	{
	    	if(vo.getCodingFormat().equals(null)){
	    		
	    	}
	    	else
	    	{
	    		strBuff.append(" CodingFormat=\""+ vo.getCodingFormat() +"\"");
	    		strBuff.append(" Width=\""+ vo.getWidth() +"\"");
	    		strBuff.append(" Height=\""+ vo.getHeight() +"\"");
	    		strBuff.append(" Fps=\""+ vo.getFps() +"\"");
	    		strBuff.append(" Bps=\""+ vo.getBps() +"\"");
	    	}
    	}
    	catch(Exception ex){
    		
    	}
    	
    	
    	strBuff.append(" SMGUdpIP=\"" + vo.getRtvsIP() + "\" SMGUdpPort=\""+ vo.getRtvsPort() +"\" Type=\"" + vo.getStatusFlag() + "\"  />\r\n");
        strBuff.append("</RTVSResetConfig>\r\n");
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
	}
	
	
	public String getReturnURL(Document document){
		String retURL = "";
		
		if (document == null) {
			return retURL;
		}
		List<ConstellationQueryVO> list = new ArrayList();
		
		Element root = document.getRootElement();
		
		Element Return = root.element("Return");
		
		String type = Return.attribute("Type").getValue();
		if(!type.equals("RTVSResetConfig")){
			return retURL;
		}
		
		Element ReturnInfo=root.element("ReturnInfo");
		Element RealStreamURL=ReturnInfo.element("RealStreamURL");
		
		retURL = RealStreamURL.attribute("URL").getValue();
		
		return retURL;
	}
	
	
}
