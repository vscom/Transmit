package com.bvcom.transmit.parse.si;

import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.si.ChannelScanQueryVO;
import com.bvcom.transmit.vo.si.MHPQueryVO;

public class MHPQueryParse {
	
	
	public MHPQueryVO getDownXml(Document document){
		
		MHPQueryVO vo = new MHPQueryVO();
		
		Element root = null;
		
		root = document.getRootElement();
		Element ele = null;
		
		ele = root.element("MHPQuery");
		String ScanTime = ele.attributeValue("ScanTime").trim();
		
		if ("".equals(ScanTime)) {
			ScanTime = CommonUtility.getDateTime();
		}
		
		vo.setScanTime(ScanTime);
		return vo;
	}
	

	/**
	 * MHP 回复信息
	 * @param head
	 * @param vo
	 * @param value
	 * @return
	 */
	public String getMHPReturnXML(MSGHeadVO head, MHPQueryVO vo, int value) {

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuff.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuff.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuff.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuff.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">");
		if(0==value){
            strBuff.append("<Return Type=\"MHPQuery\" Value=\"0\" Desc=\"成功\"/>");
		}else if(1==value){
            strBuff.append("<Return Type=\"MHPQuery\" Value=\"1\" Desc=\"失败\"/>");
		}
		strBuff.append("<ReturnInfo>");
		
		/*
		<MHPQuery ScanTime="2002-09-01 10:00:00">
    	<MHP Ftp="ftp://192.168.0.1:6666/2002-09-01" UserName="guest" Pass="123"/>
    	</MHPQuery> 
		 */
		
		strBuff.append("<MHPQuery ScanTime=\"" + vo.getScanTime() + "\">");
		strBuff.append("<MHP Ftp=\"" + vo.getFtp() + "\" UserName=\"guest\" Pass=\"123\"/>");
		
		strBuff.append("</MHPQuery>");
		strBuff.append("</ReturnInfo>");
        strBuff.append("</Msg>");
		return strBuff.toString();

	}
}
