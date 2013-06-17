package com.bvcom.transmit.parse.alarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.AlarmProgramThresholdSetVO;

//�������ޣ���Ŀ��أ�
public class AlarmProgramThresholdSetParse {
	
	//��Ŀ�������޽���xml�õ�ͨ���ŵ�����
	@SuppressWarnings("unchecked")
	public List<AlarmProgramThresholdSetVO> getIndexByDownXml(Document document){
		List<AlarmProgramThresholdSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
			
			AlarmProgramThresholdSetVO vo = new AlarmProgramThresholdSetVO();
			
			vo.setIndex(Integer.parseInt(node.getText()));
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	//��Ŀ�������޽����ظ���xml����
	@SuppressWarnings("unchecked")
	public AlarmProgramThresholdSetVO getReturnByXml(Document document){
		AlarmProgramThresholdSetVO vo = new AlarmProgramThresholdSetVO();
		
		Element root = document.getRootElement();
		Element Return = root.element("Return");
		String type = Return.attribute("Type").getValue();
		int value  = Integer.parseInt(Return.attribute("Value").getValue());
		if(!type.equals("AlarmProgramThresholdSet")){
			vo.setReturnValue(1);
			vo.setComment("��Ŀ��������xml��type����");
			return vo;
		}
		vo.setReturnValue(value);
		return vo;
	}
	
	// ��Ŀ�������޻ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head,int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"AlarmProgramThresholdSet\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"AlarmProgramThresholdSet\" Value=\"1\" Desc=\"ʧ��\"/>";
		}
		xml += "</Msg>";
		return xml;

	}

}
