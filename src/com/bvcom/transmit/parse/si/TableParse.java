package com.bvcom.transmit.parse.si;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;

public class TableParse {
	public String getTableReturnXML(MSGHeadVO head, String Redirect, int value) {

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuff.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuff.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuff.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuff.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">");
		if(0==value){
            strBuff.append("<Return Type=\"table\" Value=\"0\" Desc=\"³É¹¦\" Redirect=\"" + Redirect + "\" />");
		}else if(1==value){
            strBuff.append("<Return Type=\"table\" Value=\"1\" Desc=\"Ê§°Ü\" Redirect=\"" + Redirect + "\"  />");
		}
        strBuff.append("</Msg>");
		return strBuff.toString();
	}
}
