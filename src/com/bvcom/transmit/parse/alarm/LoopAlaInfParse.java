package com.bvcom.transmit.parse.alarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.LoopAlaInfVO;

//循切报警设置
public class LoopAlaInfParse {
	
	//循切报警设置解析xml得到通道号的数组
	@SuppressWarnings("unchecked")
	public List<LoopAlaInfVO> getIndexByDownXml(Document document){
		List<LoopAlaInfVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element LoopAlaInf = root.element("LoopAlaInf");
		for(Iterator<Element> iter=LoopAlaInf.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
			
			LoopAlaInfVO vo = new LoopAlaInfVO();
			
			vo.setIndex(Integer.parseInt(node.getText()));
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	// 循切报警设置回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime()+ "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"LoopAlaInf\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"LoopAlaInf\" Value=\"1\" Desc=\"失败\"/>";
		}
		xml += "</Msg>";
		return xml;
	}

}
