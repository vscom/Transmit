package com.bvcom.transmit.parse.si;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.si.IndexCompensationSetVO;

//ָ�겹��
public class IndexCompensationSetParse {
	//ָ�겹������xml�õ�ͨ���ŵ�����
	@SuppressWarnings("unchecked")
	public List<IndexCompensationSetVO> getIndexByDownXml(Document document){
		List<IndexCompensationSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			IndexCompensationSetVO vo = new IndexCompensationSetVO();
			
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
			vo.setIndex(Integer.parseInt(node.getText()));
			
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	//ָ�겹���ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, int value) {
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"IndexCompensationSet\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"IndexCompensationSet\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;
	}

}
