package com.bvcom.transmit.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.NVRSteamRateSetVO;

//ʵʱ��Ƶ����
public class NVRSteamRateSetParse {

	//ʵʱ��Ƶ���ʽ���xml�õ�ͨ���ŵ�����
	@SuppressWarnings("unchecked")
	public List<NVRSteamRateSetVO> getIndexByDownXml(Document document){
		List<NVRSteamRateSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			NVRSteamRateSetVO vo = new NVRSteamRateSetVO();
			
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
			vo.setIndex(Integer.parseInt(node.getText()));
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	//ʵʱ��Ƶ���ʻظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, int value) {
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"NVRSteamRateSet\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"NVRSteamRateSet\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;
	}
}
