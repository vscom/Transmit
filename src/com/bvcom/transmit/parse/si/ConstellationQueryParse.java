package com.bvcom.transmit.parse.si;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.si.ChannelScanQueryVO;
import com.bvcom.transmit.vo.si.ConstellationQueryVO;

public class ConstellationQueryParse {

	public ConstellationQueryVO getDownObject(Document document){
		
		ConstellationQueryVO vo = new ConstellationQueryVO();
		
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			Attribute freq = element.attribute("Freq");
			Attribute symbolRate = element.attribute("SymbolRate");
			Attribute qam = element.attribute("QAM");
			
			vo.setFreq(Integer.parseInt(freq.getValue()));
			vo.setSymbolRate(Integer.parseInt(symbolRate.getValue()));
			vo.setQAM(qam.getValue());
			break;
		}
		return vo;
	}
	
	public String createDownXML(ConstellationQueryVO vo, MSGHeadVO bsData){
		
		StringBuffer downString = new StringBuffer();
		
		downString.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		downString.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getDstCode()
                + "\" DstCode=\"" + bsData.getSrcCode() + "\" SrcURL=\""+bsData.getSrcURL()+ "\" Priority=\"1\"> \r\n");
		downString.append(" <NephogramQuery QueryFreq=\"" +vo.getFreq()+ "\" SymbolRate=\"" + vo.getSymbolRate() + "\" QAM=\"" +vo.getQAM()+ "\" /> \r\n");
		downString.append("</Msg>");
		
		return downString.toString();
	}
	
	public List<ConstellationQueryVO> getReturnObject(Document document){
		List<ConstellationQueryVO> list = new ArrayList();
		
		Element root = document.getRootElement();
		
		Element Return = root.element("Return");
		
		String type = Return.attribute("Type").getValue();
		if(!type.equals("NephogramQuery")){
			return list;
		}
		
		Element ReturnInfo=root.element("ReturnInfo");
		Element NephogramQuery=ReturnInfo.element("NephogramQuery");
		
		String freq = NephogramQuery.attribute("QueryFreq").getValue();
		String symbolRate = "6875";
		try {
			symbolRate = NephogramQuery.attribute("SymbolRate").getValue();
		} catch (Exception ex) {
			
		}
		
		String qam = "QAM64";
		try {
			qam = NephogramQuery.attribute("QAM").getValue();
		} catch (Exception ex) {
			
		}
		
		String mer = "38";
		try {
			mer = NephogramQuery.attribute("MER").getValue();
		} catch (Exception ex) {
			
		}
		
		for(Iterator<Element> iter=NephogramQuery.elementIterator();iter.hasNext();){
			Element cs = iter.next();
			double ValueI = Double.valueOf(cs.attribute("I").getValue());
			double ValueQ = Double.valueOf(cs.attribute("Q").getValue());
			
			
			ConstellationQueryVO vo = new ConstellationQueryVO();
			
			vo.setFreq(Integer.parseInt(freq));
			vo.setQAM(qam);
			vo.setSymbolRate(Integer.parseInt(symbolRate));
			vo.setValueI(ValueI);
			vo.setValueQ(ValueQ);
			vo.setMER(Integer.parseInt(mer));
			
			list.add(vo);
		}
		return list;
	}
	
	public String createReturnXML(List<ConstellationQueryVO> voList, MSGHeadVO head, int value){
		
		StringBuffer downString = new StringBuffer();
		int freq = 0;
		String qam = "";
		int mer = 0;

		for(int i=0; i<voList.size(); i++) {
			ConstellationQueryVO vo = voList.get(i);
			mer = vo.getMER();
			freq = vo.getFreq();
			qam = vo.getQAM();
			break;
		}
		
		downString.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
		downString.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
		downString.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
		downString.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
		downString.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
        	downString.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"³É¹¦\"/>\r\n");
        }else if(1==value){
        	downString.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"Ê§°Ü\"/>\r\n");
        }
        downString.append("<ReturnInfo>\r\n");
        downString.append(" <ConstellationQuery Freq=\"" + freq + "\" QAM=\"" + qam + "\" MER=\"" + mer + "\"> \r\n");
        for(int i=0; i<voList.size(); i++) {
        	ConstellationQueryVO vo = voList.get(i);
        	downString.append("		<Value I=\"" + (vo.getValueI()/72) + "\" Q=\"" + (vo.getValueQ()/72) + "\"/>\r\n");
        }
        downString.append("</ConstellationQuery>\r\n");
        downString.append("</ReturnInfo>\r\n");
        downString.append("</Msg>");
		
		return downString.toString();
	}
	
}
