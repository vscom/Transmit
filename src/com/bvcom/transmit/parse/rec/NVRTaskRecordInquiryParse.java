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

//����¼��鿴
public class NVRTaskRecordInquiryParse {
	
	//����¼�����xml�õ�ͨ���ŵ�����
	@SuppressWarnings("unchecked")
	public List<ProvisionalRecordTaskSetVO> getIndexByDownXml(Document document){
		List<ProvisionalRecordTaskSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element StreamRoundInfoQuery = root.element("NVRTaskRecordInquiry");
		for(Iterator<Element> iter=StreamRoundInfoQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			ProvisionalRecordTaskSetVO vo = new ProvisionalRecordTaskSetVO();
			
			Node node0 =element.selectSingleNode("@TaskID");
			if(node0==null)
				continue;
			
			Node node1 =element.selectSingleNode("@StartDateTime");
			String startDateTime = node1.getText();
			
			Node node2 =element.selectSingleNode("@EndDateTime");
			String endDateTime = node2.getText();
			
			vo.setTaskID(node0.getText());
			vo.setStartTime(startDateTime);
			vo.setEndTime(endDateTime);
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	// ����¼��鿴�ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head,ProvisionalRecordTaskSetVO vo,int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"NVRTaskRecordInquiry\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"NVRTaskRecordInquiry\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml +=	"</Msg>";
			return xml;
		}
		xml +="<ReturnInfo><NVRTaskRecordInquiry TaskID=\""+vo.getTaskID()+"\">";
		xml +="<NVRTaskRecord Index=\""+vo.getIndex()+"\" StartDateTime=\""+vo.getStartTime()+"\" EndDateTime=\""+vo.getEndTime()+"\" URL=\""+vo.getURL()+"\"/>";
		xml += "</NVRTaskRecordInquiry></ReturnInfo></Msg>";
		return xml;

	}

}
