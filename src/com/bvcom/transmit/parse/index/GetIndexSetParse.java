package com.bvcom.transmit.parse.index;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.index.GetIndexSetVO;

//����ָ���ѯ
public class GetIndexSetParse {

	// ����ָ���ѯ�ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, GetIndexSetVO vo  ,int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"GetIndexSet\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"GetIndexSet\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml +="<ReturnInfo><GetIndexSet Index=\""+vo.getIndex()+"\"  Freq=\""+vo.getFreq()+"\">";
		for(int i=0;i<vo.getType().size();i++){
			xml +="<GetIndex  Type=\""+vo.getType().get(i)+"\" Desc=\""+vo.getDesc().get(i)+"\" Value=\""+vo.getValue()+"\"/>";
		}
		xml += "</GetIndexSet></ReturnInfo></Msg>";
		return xml;
	}
}
