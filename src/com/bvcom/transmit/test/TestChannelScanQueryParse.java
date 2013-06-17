package com.bvcom.transmit.test;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;

import com.bvcom.transmit.parse.si.ChannelScanQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.si.ChannelScanQueryVO;

public class TestChannelScanQueryParse {

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
//		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
//		xml += "<Msg" + MSG_VERSION + 
//		  MSG_ID + MSG_TYPE + MSG_DATA_TIME + "\"" + data + "\"" + 
//		  MSG_SRC_CODE +"\"" + name + "\"" + MSG_DST_CODE + MSG_PRIORITY+MSG_REPLYID+"\"" + 1 + "\"" +">";
//		xml += " <ChannelScanQuery ScanTime=\"\" SymbolRate=\"6875\" QAM=\"QAM64\"/>";
//		xml +="</Msg>";
//		
//		System.out.println("�ַ���xml��"+xml);
//		
//		UtilXML uxml = new UtilXML();
//		Document document = uxml.StringToXML(xml);
//		
//		ChannelScanQueryParse csqp =new ChannelScanQueryParse();
//		
//		ChannelScanQueryVO vo= csqp.getDownObject(document);
//		
//		System.out.println(vo.getQAM());
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg" + MSG_VERSION + 
		  MSG_ID + MSG_TYPE + MSG_DATA_TIME + "\"" + data + "\"" + 
		  MSG_SRC_CODE +"\"" + name + "\"" + MSG_DST_CODE + MSG_PRIORITY+MSG_REPLYID+"\"" + 1 + "\"" +">";
		xml += " <Return Type=\"ChannelScanQuery\" Value=\"0\" Desc=\"�ɹ�\"/>" +
				"<ReturnInfo><ChannelScanQuery ScanTime=\"2002-09-01 10:00:00\">" +
				"<ChannelScan  Freq=\"482000\" OrgNetID=\"5678\" TsID=\"12\">" +
				"<Channel Program=\"����һ\" ProgramID=\"123\" ServiceID=\"10\" Pcr_PID=\"122\" VideoPID=\"2060\" AudioPID=\"2061\"/>" +
				"<Channel Program=\"�����\" ProgramID=\"124\" ServiceID=\"11\" Pcr_PID=\"123\" VideoPID=\"2062\" AudioPID=\"2064\"/>" +
				"</ChannelScan></ChannelScanQuery></ReturnInfo></Msg>";
		
		System.out.println("�ַ���xml��"+xml);
		
		UtilXML uxml = new UtilXML();
		Document document = uxml.StringToXML(xml);
		
		ChannelScanQueryParse csqp =new ChannelScanQueryParse();
		
//		List<ChannelScanQueryVO> vo= csqp.getReturnObject(document);
//		for(int i=0;i<vo.size();i++){
//			System.out.println(vo.get(i).getFreq());
//			
//		}
		

	}

}
