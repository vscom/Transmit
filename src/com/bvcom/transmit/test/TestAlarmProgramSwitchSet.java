package com.bvcom.transmit.test;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;

import com.bvcom.transmit.parse.alarm.AlarmProgramSwitchSetParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.alarm.AlarmProgramSwitchSetVO;

public class TestAlarmProgramSwitchSet {

//	XMLͷ
	private static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
//	MSG�ڵ��Version����
	private static String MSG_VERSION = " Version=\"4\"";
//	MSG�ڵ��MsgID����
	private static String MSG_ID = " MsgID=\"2\"";
	//MSG�ڵ��Type����
	private static String MSG_TYPE = " Type=\"AD988MonUp\"";
	//MSG�ڵ��Time����
	private static String MSG_DATA_TIME = " DateTime=";
	//MSG�ڵ��Դ�˱�������
	private static String MSG_SRC_CODE = " SrcCode=";
	//MSG�ڵ��Ŀ���������
	private static String MSG_DST_CODE = " DstCode=\"110000N01\"";
//	MSG�ڵ�����ȼ�����
	private static String MSG_PRIORITY = " Priority=\"1\"";
//	MSG�ڵ�����ȼ�����
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
		xml += "<AlarmProgramSwitchSet Index=\"1\" Freq=\"482000\" >  <AlarmProgram ServiceID=\"10\" VideoPID=\"2060\" AudioPID=\"2061\"> <AlarmProgramSwitch Type=\"31\" Desc=\"��֡\" Switch = \"1\"/> <AlarmProgramSwitch Type=\"32\" Desc=\"�ڳ�\" Switch = \"1\"/><AlarmProgramSwitch Type=\"33\" Desc=\"�ް���\" Switch = \"1\"/></AlarmProgram></AlarmProgramSwitchSet>";
		xml += "<AlarmProgramSwitchSet Index=\"2\" Freq=\"482000\" >  <AlarmProgram ServiceID=\"10\" VideoPID=\"2060\" AudioPID=\"2061\"> <AlarmProgramSwitch Type=\"31\" Desc=\"��֡\" Switch = \"1\"/> <AlarmProgramSwitch Type=\"32\" Desc=\"�ڳ�\" Switch = \"1\"/><AlarmProgramSwitch Type=\"33\" Desc=\"�ް���\" Switch = \"1\"/></AlarmProgram></AlarmProgramSwitchSet>";
		xml += "<AlarmProgramSwitchSet Index=\"3\" Freq=\"482000\" >  <AlarmProgram ServiceID=\"10\" VideoPID=\"2060\" AudioPID=\"2061\"> <AlarmProgramSwitch Type=\"31\" Desc=\"��֡\" Switch = \"1\"/> <AlarmProgramSwitch Type=\"32\" Desc=\"�ڳ�\" Switch = \"1\"/><AlarmProgramSwitch Type=\"33\" Desc=\"�ް���\" Switch = \"1\"/></AlarmProgram></AlarmProgramSwitchSet>";
		xml += "<AlarmProgramSwitchSet Index=\"2\" Freq=\"482000\" >  <AlarmProgram ServiceID=\"10\" VideoPID=\"2060\" AudioPID=\"2061\"> <AlarmProgramSwitch Type=\"31\" Desc=\"��֡\" Switch = \"1\"/> <AlarmProgramSwitch Type=\"32\" Desc=\"�ڳ�\" Switch = \"1\"/><AlarmProgramSwitch Type=\"33\" Desc=\"�ް���\" Switch = \"1\"/></AlarmProgram></AlarmProgramSwitchSet>";
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
