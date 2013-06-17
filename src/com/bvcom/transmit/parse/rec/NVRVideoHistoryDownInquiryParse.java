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

//��ʷ��Ƶ����
public class NVRVideoHistoryDownInquiryParse {
	
	//��ʷ��Ƶ���ؽ���xml�õ�ͨ���ŵ�����
	@SuppressWarnings("unchecked")
	public List<ProvisionalRecordTaskSetVO> getIndexByDownXml(Document document){
		List<ProvisionalRecordTaskSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element StreamRoundInfoQuery = root.element("NVRVideoHistoryDownInquiry");
		for(Iterator<Element> iter=StreamRoundInfoQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			ProvisionalRecordTaskSetVO vo = new ProvisionalRecordTaskSetVO();
			
			Node node0 =element.selectSingleNode("@Index");
			if(node0==null)
				continue;
			vo.setIndex(Integer.parseInt(node0.getText()));
			
			Node node1 =element.selectSingleNode("@StartDateTime");
			vo.setStartTime(node1.getText());
			
			Node node2 =element.selectSingleNode("@EndDateTime");
			vo.setEndTime(node2.getText());
			
			try{
				node2 =element.selectSingleNode("@Freq");
				vo.setFreq(Integer.parseInt(node2.getText()));
				
				node2 =element.selectSingleNode("@ServiceID");
				vo.setServiceID(Integer.parseInt(node2.getText()));
				
			} catch (Exception ex) {
				
			}
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	// ��ʷ��Ƶ���ػظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head,ProvisionalRecordTaskSetVO vo,int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"NVRVideoHistoryDownInquiry\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"NVRVideoHistoryDownInquiry\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml +=	"</Msg>";
			return xml;
		}
		xml +="<ReturnInfo><NVRTaskRecordDownInquiry>";
		xml +="<NVRTaskRecordDown URL=\""+vo.getURL()+"\"/>";
		xml += "</NVRTaskRecordDownInquiry></ReturnInfo></Msg>";
		return xml;

	}

}
