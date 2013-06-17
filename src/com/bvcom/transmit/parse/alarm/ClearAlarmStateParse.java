package com.bvcom.transmit.parse.alarm;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;

//报警状态清除
public class ClearAlarmStateParse {
	// 报警状态清除回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"ClearAlarmState\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"ClearAlarmState\" Value=\"1\" Desc=\"失败\"/>";
		}
		xml += "</Msg>";
		return xml;

	}

}
