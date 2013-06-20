package com.bvcom.transmit.test;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;

import com.bvcom.transmit.parse.alarm.AlarmProgramSwitchSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.alarm.AlarmProgramSwitchSetVO;

public class TestAlarmProgramSwitchSet {

//	XML头
	private static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
//	MSG节点的Version属性
	private static String MSG_VERSION = " Version=\"4\"";
//	MSG节点的MsgID属性
	private static String MSG_ID = " MsgID=\"2\"";
	//MSG节点的Type属性
	private static String MSG_TYPE = " Type=\"AD988MonUp\"";
	//MSG节点的Time属性
	private static String MSG_DATA_TIME = " DateTime=";
	//MSG节点的源端编码属性
	private static String MSG_SRC_CODE = " SrcCode=";
	//MSG节点的目标编码属性
	private static String MSG_DST_CODE = " DstCode=\"110000N01\"";
//	MSG节点的优先级属性
	private static String MSG_PRIORITY = " Priority=\"1\"";
//	MSG节点的优先级属性
	private static String MSG_REPLYID = " ReplyID=";
	
	public static void main(String[] args) throws CommonException {
		String data;
		java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		data = format1.format(new Date());
		String name = "110000";
		
		String xml =null;
		
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg" + MSG_VERSION + 
		  MSG_ID + MSG_TYPE + MSG_DATA_TIME + "\"" + data + "\"" + 
		  MSG_SRC_CODE +"\"" + name + "\"" + MSG_DST_CODE + MSG_PRIORITY+MSG_REPLYID+"\"" + 1 + "\"" +">";
		xml += "<AlarmProgramSwitchSet Index=\"1\" Freq=\"482000\" >  <AlarmProgram ServiceID=\"10\" VideoPID=\"2060\" AudioPID=\"2061\"> <AlarmProgramSwitch Type=\"31\" Desc=\"静帧\" Switch = \"1\"/> <AlarmProgramSwitch Type=\"32\" Desc=\"黑场\" Switch = \"1\"/><AlarmProgramSwitch Type=\"33\" Desc=\"无伴音\" Switch = \"1\"/></AlarmProgram></AlarmProgramSwitchSet>";
		xml += "<AlarmProgramSwitchSet Index=\"2\" Freq=\"482000\" >  <AlarmProgram ServiceID=\"10\" VideoPID=\"2060\" AudioPID=\"2061\"> <AlarmProgramSwitch Type=\"31\" Desc=\"静帧\" Switch = \"1\"/> <AlarmProgramSwitch Type=\"32\" Desc=\"黑场\" Switch = \"1\"/><AlarmProgramSwitch Type=\"33\" Desc=\"无伴音\" Switch = \"1\"/></AlarmProgram></AlarmProgramSwitchSet>";
		xml += "<AlarmProgramSwitchSet Index=\"3\" Freq=\"482000\" >  <AlarmProgram ServiceID=\"10\" VideoPID=\"2060\" AudioPID=\"2061\"> <AlarmProgramSwitch Type=\"31\" Desc=\"静帧\" Switch = \"1\"/> <AlarmProgramSwitch Type=\"32\" Desc=\"黑场\" Switch = \"1\"/><AlarmProgramSwitch Type=\"33\" Desc=\"无伴音\" Switch = \"1\"/></AlarmProgram></AlarmProgramSwitchSet>";
		xml += "<AlarmProgramSwitchSet Index=\"2\" Freq=\"482000\" >  <AlarmProgram ServiceID=\"10\" VideoPID=\"2060\" AudioPID=\"2061\"> <AlarmProgramSwitch Type=\"31\" Desc=\"静帧\" Switch = \"1\"/> <AlarmProgramSwitch Type=\"32\" Desc=\"黑场\" Switch = \"1\"/><AlarmProgramSwitch Type=\"33\" Desc=\"无伴音\" Switch = \"1\"/></AlarmProgram></AlarmProgramSwitchSet>";
		xml +="</Msg>";
		
		UtilXML uxml = new UtilXML();
		Document document = uxml.StringToXML(xml);
		
		AlarmProgramSwitchSetParse apssp = new AlarmProgramSwitchSetParse();
		List<AlarmProgramSwitchSetVO> list = apssp.getIndexByDownXml(document);
		for(int i=0;i<list.size();i++){
			System.out.print(list.get(i));
		}

	}

}
