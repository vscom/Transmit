package com.bvcom.transmit.parse.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.index.GetNvrStatusVO;

//通道状态查询
public class GetNvrStatusParse {
	
	@SuppressWarnings("unchecked")
//	public GetNvrStatusVO getIndexByDownXml(Document document){
//		GetNvrStatusVO vo = new GetNvrStatusVO();
//		Element root = document.getRootElement();
//		List<Integer> index = new ArrayList<Integer>();
//        Element nvrEle = root.element("GetNvrStatus");
//		for(Iterator<Element> iter=nvrEle.elementIterator();iter.hasNext();){
//			Element element = iter.next();
//			Node node =element.selectSingleNode("@Index");
//			if(node==null)
//				continue;
//			
//			index.add(Integer.parseInt(node.getText()));
//		}
//		vo.setIndex(index);
//		return vo;
//		
//	}
	
	// 通道状态查询回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, List voList) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">\r\n";
//		if(0==value){
			xml += "<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n";
//		}else if(1==value){
//			xml += "<Return Type=\"" + head.getStatusQueryType() +"\" Value=\"1\" Desc=\"失败\"/>";
//			xml +="</Msg>";
//			return xml;
//		}
		xml +="<ReturnInfo>\r\n";
		for(int i=0;i<voList.size();i++){
			GetNvrStatusVO vo =  (GetNvrStatusVO)voList.get(i);
			xml +="<GetNvrIndexStatus  Index=\""+vo.getIndex()+"\"  Status=\""+vo.getStatus()+"\" Freq=\""+vo.getFreq()+"\" ServiceID=\""+vo.getServiceID()+"\" Desc=\""+vo.getDesc()+"\" />\r\n";
		}
		xml += "</ReturnInfo></Msg>\r\n";
		return xml;

	}

}
