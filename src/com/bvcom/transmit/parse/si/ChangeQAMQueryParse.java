package com.bvcom.transmit.parse.si;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.si.ChangeQAMQueryVO;

//QAM����
public class ChangeQAMQueryParse {

	//QAM���ƽ���xml�õ�ͨ���ŵ�����
	@SuppressWarnings("unchecked")
	public List<ChangeQAMQueryVO> getIndexByDownXml(Document document){
		List<ChangeQAMQueryVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element MatrixQuery = root.element("ChangeQAMQuery");
		for(Iterator<Element> iter=MatrixQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
            ChangeQAMQueryVO vo = new ChangeQAMQueryVO();
            try {
    			vo.setIndex(Integer.parseInt(node.getText()));
            } catch (Exception ex) {
            	vo.setIndex(99);
            }
            
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	//QAM���ƻظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, int value) {
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"ChangeQAMQuery\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"ChangeQAMQuery\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;
	}
}
