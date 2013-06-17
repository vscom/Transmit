package com.bvcom.transmit.parse.si;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.si.EPGQueryVo;

public class EPGQueryParse {

	/**
	 * 解析下发的EPG查询指令
	 * @param document
	 * @return
	 */
	public EPGQueryVo getDownObject(Document document){
		
		EPGQueryVo vo = new EPGQueryVo();
		
		Element root = document.getRootElement();
		
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			
			Element element = iter.next();
			
			Attribute scanTime = element.attribute("ScanTime");
			vo.setScanTime(scanTime.getValue());
			break;
		}
		return vo;
	}
	
	/**
	 * 取得EPG返回信息中的记录信息
	 * @param document
	 * @return List<EPGQueryVo>
	 */
	public List<EPGQueryVo> getReturnObject(Document document){
		List<EPGQueryVo> list = new ArrayList();
		
		Element root = document.getRootElement();
		
		try {
			Element Return = root.element("Return");
			String type = Return.attribute("Type").getValue();
			int value = Integer.parseInt(Return.attribute("Value").getValue());
			if(!type.equals("EPGQuery")){
				return list;
			}
		} catch (Exception ex) {
			
		}
		
		Element ReturnInfo=root.element("ReturnInfo");
		Element EPGQuery=ReturnInfo.element("EPGQuery");
		String scanTime=EPGQuery.attribute("ScanTime").getValue();

		for(Iterator<Element> iter=EPGQuery.elementIterator();iter.hasNext();){
			Element cs = iter.next();
			String freq = cs.attribute("Freq").getValue();
			
			for(Iterator<Element> ite=cs.elementIterator();ite.hasNext();){
				Element channel = ite.next();
				
				EPGQueryVo vo = new EPGQueryVo();
				
				try {
					vo.setScanTime(scanTime);
					vo.setFreq(freq);
					vo.setProgramID(channel.attribute("ProgramID").getValue());
					vo.setProgram(channel.attribute("Program").getValue());
					vo.setProgramType(channel.attribute("ProgramType").getValue());
					
					vo.setStartTime(channel.attribute("StartTime").getValue());
					vo.setProgramLen(channel.attribute("ProgramLen").getValue());
					vo.setState(channel.attribute("State").getValue());
					vo.setEncryption(channel.attribute("Encryption").getValue());
					
					list.add(vo);
				} catch (Exception ex) {
					continue;
				} finally {
					vo = null;
				}
				
			}
		}
		return list;
	}
	
	public String getEPGReturnXML(MSGHeadVO head, String Redirect, int value) {

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuff.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuff.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuff.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">\r\n");
		if(0==value){
            strBuff.append("<Return Type=\"EPGQuery\" Value=\"0\" Desc=\"成功\" Redirect=\"" + Redirect + "\" />\r\n");
		}else if(1==value){
            strBuff.append("<Return Type=\"EPGQuery\" Value=\"1\" Desc=\"失败\" Redirect=\"" + Redirect + "\"  />\r\n");
		}
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
	}
	
	/**
	 * 转换EPG信息为XML文件
	 * @param head
	 * @param EPGQueryList
	 * @param scanTime
	 * @return
	 */
	public String getEPGInfoXMLByList(MSGHeadVO head, List<EPGQueryVo> EPGQueryList, String scanTime) {

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuff.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuff.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuff.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">\r\n");
        strBuff.append("<Return Type=\"EPGQuery\" Value=\"0\" Desc=\"成功\" Redirect=\"\" />\r\n");
		
        strBuff.append("<ReturnInfo>\r\n");
        strBuff.append("<EPGQuery ScanTime=\"" + scanTime + "\">\r\n");
        
        String freq = "";
        for(int i=0; i<EPGQueryList.size(); i++) {
        	EPGQueryVo vo = EPGQueryList.get(i);
        	if(!freq.equals(vo.getFreq())) {
        		if(i != 0) {
        			strBuff.append("</EPGInfo>\r\n");
        		}
        		strBuff.append("<EPGInfo Freq=\"" + vo.getFreq() + "\">\r\n");
        	}
        	
        	strBuff.append(" <EPG ProgramID=\""+ vo.getProgramID() +"\" Program=\"" + vo.getProgram() + "\" ProgramType=\"" + vo.getProgramType() + "\" ");
        	strBuff.append(" StartTime=\"" + vo.getStartTime() + "\" ProgramLen=\"" + vo.getProgramLen() + "\" State=\"" + vo.getState() + "\" Encryption=\"" + vo.getEncryption() + "\"/>\r\n");
        	
        	freq = vo.getFreq();
        }
        if(EPGQueryList.size() > 0) {
        	strBuff.append("</EPGInfo>\r\n");
        }
        
        strBuff.append(" </EPGQuery>\r\n");
        strBuff.append("</ReturnInfo>\r\n");
        
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
	}
	
}
