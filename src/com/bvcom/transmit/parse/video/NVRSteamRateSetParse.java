package com.bvcom.transmit.parse.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

public class NVRSteamRateSetParse {

	//实时视频流率解析xml得到通道号的数组
	@SuppressWarnings("unchecked")
	public List<ProvisionalRecordTaskSetVO> getIndexByDownXml(Document document){
		List<ProvisionalRecordTaskSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		//Element StreamRoundInfoQuery = root.element("NVRSteamRateSet");
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			ProvisionalRecordTaskSetVO vo = new ProvisionalRecordTaskSetVO();
			
			Node node0 =element.selectSingleNode("@Index");
			if(node0==null)
				continue;
            
			vo.setIndex(Integer.parseInt(node0.getText()));
			
//			Node node1 =element.selectSingleNode("@Width");
//			if(node1==null)
//				continue;
//			vo.setWidth(Integer.parseInt(node1.getText()));
//			
//			Node node2 =element.selectSingleNode("@Height");
//			if(node2==null)
//				continue;
//			vo.setHeight(Integer.parseInt(node2.getText()));
//			
//			Node node3 =element.selectSingleNode("@Fps");
//			if(node3==null)
//				continue;
//			vo.setFps(Integer.parseInt(node3.getText()));
//			
//			Node node4 =element.selectSingleNode("@Bps");
//			if(node4==null)
//				continue;
//			vo.setBps(Integer.parseInt(node4.getText()));
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
}
