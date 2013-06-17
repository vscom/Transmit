package com.bvcom.transmit.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.MatrixQueryVO;

//�����л�
public class MatrixQueryParse {
	//�����л�����xml�õ�ͨ���ŵ�����
	@SuppressWarnings("unchecked")
	public List<MatrixQueryVO> getIndexByDownXml(Document document){
		List<MatrixQueryVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element MatrixQuery = root.element("MatrixQuery");
		for(Iterator<Element> iter=MatrixQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			MatrixQueryVO vo = new MatrixQueryVO();
			
			Node node =element.selectSingleNode("@Index1");
			if(node==null)
				continue;
			vo.setIndex1(Integer.parseInt(node.getText()));
			
			Node node1 =element.selectSingleNode("@Index2");
			if(node1==null)
				continue;
			vo.setIndex2(Integer.parseInt(node1.getText()));
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	// �����л��ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, int value) {
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"MatrixQuery\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"MatrixQuery\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;
	}

}
