package com.bvcom.transmit.parse.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.parse.rec.RecordMbpsFlag;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

public class RecordParamSetExParse {
private static Logger log=Logger.getLogger(RecordParamSetExParse.class.getSimpleName());
	public List<ProvisionalRecordTaskSetVO> getDownXml(Document document){
		List<ProvisionalRecordTaskSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element StreamRoundInfoQuery = root.element("RecordParamSetEx");
		for(Iterator<Element> iter=StreamRoundInfoQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			Node node0 =element.selectSingleNode("@Index");

			int index = Integer.parseInt(node0.getText());
			
			ProvisionalRecordTaskSetVO vo =new ProvisionalRecordTaskSetVO();
			vo.setIndex(index);
			
			try {
				for(Iterator<Element> timeIter=element.elementIterator();timeIter.hasNext();){
					Element timeEle = timeIter.next();
					node0 =timeEle.selectSingleNode("@Freq");
					vo.setFreq(Integer.parseInt(node0.getText()));
					
					node0 =timeEle.selectSingleNode("@ServiceID");
					vo.setServiceID(Integer.parseInt(node0.getText()));
				}
			} catch (Exception ex) {
				
			}
			
			indexlist.add(vo);
		}
		return indexlist;
	}
	
	public List<RecordMbpsFlag> parse(Document document){
		List<RecordMbpsFlag> list=new ArrayList<RecordMbpsFlag>();
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element RecordParamSetEx = iter.next();
			RecordMbpsFlag rmf =new RecordMbpsFlag();
			try {
				//<RecordParamSetEx Freq="714000" ServiceID="101" Index="2" Width="352" Height="288" Fps="25" Bps="700000"/>
				rmf.setBps(Integer.parseInt(RecordParamSetEx.attribute("Bps").getValue()));
				rmf.setFps(Integer.parseInt(RecordParamSetEx.attribute("Fps").getValue()));
				rmf.setFreq(Integer.parseInt(RecordParamSetEx.attribute("Freq").getValue()));
				rmf.setHeight(Integer.parseInt(RecordParamSetEx.attribute("Height").getValue()));
				rmf.setIndex(Integer.parseInt(RecordParamSetEx.attribute("Index").getValue()));
				rmf.setServiceID(Integer.parseInt(RecordParamSetEx.attribute("ServiceID").getValue()));
				rmf.setWidth(Integer.parseInt(RecordParamSetEx.attribute("Width").getValue()));
			} catch (Exception ex) {
				log.error("解析频率开关错误"+ex);
			}
			list.add(rmf);
		}
		return list;
	}
	// 码率设置回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, int value) {
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"RecordParamSetEx\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"RecordParamSetEx\" Value=\"1\" Desc=\"失败-没有更多的资源可用\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;
	}
}
