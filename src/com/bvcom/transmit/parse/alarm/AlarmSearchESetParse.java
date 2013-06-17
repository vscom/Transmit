package com.bvcom.transmit.parse.alarm;


import java.util.List;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchESetVO;

//报警上报（环境相关）
public class AlarmSearchESetParse {
	// 报警上报回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head,List<AlarmSearchESetVO> vo, int value) {
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime()+ "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"AlarmSearchSet\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"AlarmSearchSet\" Value=\"1\" Desc=\"失败\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml +="<ReturnInfo><AlarmSearchESet>";
		for(int j=0;j<vo.size();j++){
			xml +="<AlarmSearchE Type=\""+vo.get(j).getType()+"\" Desc=\""+vo.get(j).getDesc()+"\" Value=\""+vo.get(j).getValue()+"\" Time=\""+vo.get(j).getTime()+"\"/>";
		}
		xml += "</AlarmSearchESet></ReturnInfo></Msg>";
		return xml;
	}

}
