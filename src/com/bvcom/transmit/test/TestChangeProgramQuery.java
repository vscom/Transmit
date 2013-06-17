package com.bvcom.transmit.test;

import java.util.Date;

import org.dom4j.Document;

import com.bvcom.transmit.parse.video.ChangeProgramQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.video.ChangeProgramQueryVO;

public class TestChangeProgramQuery {


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
		
//		ChangeProgramQueryParse cpqp = new ChangeProgramQueryParse();
//		String url = "htp://127.0.0.1";
//		MSGHeadVO head = new MSGHeadVO();
//		
//		head.setCenterMsgID("1");
//		head.setDateTime(data);
//		head.setVersion("1");
//		head.setDstCode("001122");
//		head.setSrcCode("221100");
//		head.setSrcURL("http://loclahost");
//		head.setPriority("3");
//		
//		String xml = cpqp.ReturnXMLByURL(head, url);
//		System.out.println(xml);

		
		String name = "110000";
		
		String xml =null;
		
//		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
//		xml += "<Msg" + MSG_VERSION + 
//		  MSG_ID + MSG_TYPE + MSG_DATA_TIME + "\"" + data + "\"" + 
//		  MSG_SRC_CODE +"\"" + name + "\"" + MSG_DST_CODE + MSG_PRIORITY+MSG_REPLYID+"\"" + 1 + "\"" +">";
//		xml += "<ChangeProgramQuery><ChangeProgram  Index=\"1\" Freq=\"482000\" SymbolRate=\"6875\" QAM=\"QAM64\" ServiceID=\"190\" VideoPID=\"1032\" AudioPID=\"1033\"/></ChangeProgramQuery>";
//		xml +="</Msg>";
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg" + MSG_VERSION + 
		  MSG_ID + MSG_TYPE + MSG_DATA_TIME + "\"" + data + "\"" + 
		  MSG_SRC_CODE +"\"" + name + "\"" + MSG_DST_CODE + MSG_PRIORITY+MSG_REPLYID+"\"" + 1 + "\"" +">";
		xml += "<Return Type=\"ChangeProgramQuery\" Value=\"0\" Desc=\"�ɹ�\"/><ReturnInfo><RealStreamURL Index=\"3\" URL=\"svrt://127.0.0.1/192.168.0.191:8000:HIK-DS8000HC:0:0:admin:12345/av_stream\"/></ReturnInfo></Msg>";
		
		System.out.println("�ַ���xml��"+xml);
		
		UtilXML uxml = new UtilXML();
		Document document = uxml.StringToXML(xml);
		
		ChangeProgramQueryParse cpqp =new ChangeProgramQueryParse();
		
//		ChangeProgramQueryVO vod= cpqp.getDownObject(document);
//		System.out.println(vod.getFreq());
		
		ChangeProgramQueryVO vor= cpqp.getReturnObject(document);
		System.out.println(vor.getIndex());

	}

}
