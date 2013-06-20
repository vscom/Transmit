package com.bvcom.transmit.parse.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.video.RecordCapabilityQueryVO;


public class RecordCapabilityQueryParse {

	
	public List<RecordCapabilityQueryVO> getDownXml(Document document){
		
		List<RecordCapabilityQueryVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
        Element AutoRecord=root.element("RecordCapabilityQuery");
		
		for(Iterator<Element> ite=AutoRecord.elementIterator();ite.hasNext();){
			Element ChCode = ite.next();
			RecordCapabilityQueryVO vo = new RecordCapabilityQueryVO();
			
			try {
			vo.setFreq(Integer.parseInt(ChCode.attribute("Freq").getValue()));
			} catch (Exception ex) {
				vo.setFreq(0);
			}
			try {
				vo.setServiceID(Integer.parseInt(ChCode.attribute("ServiceID").getValue()));
			} catch (Exception ex) {
				vo.setServiceID(0);
			}
			try {
				vo.setProgramID(Integer.parseInt(ChCode.attribute("ProgramID").getValue()));
			} catch (Exception ex) {
				vo.setProgramID(0);
			}
            indexlist.add(vo);
		}
			
		return indexlist;
	}
	
	public String createForUpXML(MSGHeadVO bsData, List<RecordCapabilityQueryVO> AutoRecordList, int NewIndexCount, int PessCount){
		
		StringBuffer strBuff = new StringBuffer();
		
		strBuff.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
		strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\"");
		strBuff.append(bsData.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
		strBuff.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getDstCode());
		strBuff.append("\" DstCode=\"" + bsData.getSrcCode() + "\" ReplyID=\""+bsData.getCenterMsgID()+"\">");
		
        strBuff.append("<Return Type=\""+ bsData.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>");

        strBuff.append("<ReturnInfo>");
        
        strBuff.append("<RecordCapabilityQuery NewIndexCount=\"" + NewIndexCount + "\" PessCount=\"" + PessCount + "\">");
        
        for(int i=0; i< AutoRecordList.size(); i++) 
        {
        	RecordCapabilityQueryVO vo = (RecordCapabilityQueryVO)AutoRecordList.get(i);

        	strBuff.append("	<ChannelInfo Freq =\"" + vo.getFreq() + "\" ServiceID=\""+ vo.getServiceID() +"\" ");
        	strBuff.append(" ProgramID=\""+ vo.getProgramID() +"\" IsRecord=\"" + vo.getIsRecord() + "\" />");
        }
        
        strBuff.append("</RecordCapabilityQuery>");
        strBuff.append("</ReturnInfo>");
        strBuff.append("</Msg>");
        
		return strBuff.toString();
		
	}
	
}
