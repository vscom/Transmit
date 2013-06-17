package com.bvcom.transmit.parse.index;

import java.util.List;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.index.GetIndexESetVO;

//���л���ָ���ѯ
public class GetIndexESetParse {

	// ���л���ָ��ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, List<GetIndexESetVO> vo  ,int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"GetIndexESet\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"GetIndexESet\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml +="<ReturnInfo><GetIndexESet>";
		for(int i=0;i<vo.size();i++){
			xml +="<GetIndexE  Type=\""+vo.get(i).getType()+"\" Desc=\""+vo.get(i).getDesc()+"\" Value=\""+vo.get(i).getValue()+"\"/>";
		}
		xml += "</GetIndexESet></ReturnInfo></Msg>";
		return xml;
	}
}
