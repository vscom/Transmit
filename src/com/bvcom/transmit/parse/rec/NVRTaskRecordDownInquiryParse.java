package com.bvcom.transmit.parse.rec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

//任务录像下载
public class NVRTaskRecordDownInquiryParse {

	//任务录像下载解析xml得到通道号的数组
	@SuppressWarnings("unchecked")
	public List<ProvisionalRecordTaskSetVO> getIndexByDownXml(Document document){
		List<ProvisionalRecordTaskSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element StreamRoundInfoQuery = root.element("NVRTaskRecordDownInquiry");
		for(Iterator<Element> iter=StreamRoundInfoQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			ProvisionalRecordTaskSetVO vo = new ProvisionalRecordTaskSetVO();
			
			Node node0 =element.selectSingleNode("@TaskID");
			if(node0==null)
				continue;
			
			vo.setTaskID(node0.getText());
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	// 任务录像下载回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head,ProvisionalRecordTaskSetVO vo,int value) {
		//		
		// String date;
		// java.text.DateFormat format1 = new java.text.SimpleDateFormat(
		// "yyyy-MM-dd hh:mm:ss");
		// date = format1.format(new Date());

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"NVRVideoHistoryInquiry\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"NVRVideoHistoryInquiry\" Value=\"1\" Desc=\"失败\"/>";
			xml +=	"</Msg>";
			return xml;
		}
		xml +="<ReturnInfo><NVRTaskRecordDownInquiry>";
		xml +="<NVRTaskRecordDown URL=\""+vo.getURL()+"\"/>";
		xml += "</NVRTaskRecordDownInquiry></ReturnInfo></Msg>";
		return xml;

	}

}
