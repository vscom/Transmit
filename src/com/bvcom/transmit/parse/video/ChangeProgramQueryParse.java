package com.bvcom.transmit.parse.video;

import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.video.ChangeProgramQueryVO;

//手动选台
public class ChangeProgramQueryParse {

	// 手动选台下发xml解析成对象
	public ChangeProgramQueryVO getDownObject(Document document) {

		ChangeProgramQueryVO vo = new ChangeProgramQueryVO();

		Element root = document.getRootElement();	

		Element ChangeProgramQuery = root.element("ChangeProgramQuery");
			vo.setRunTime(Integer.parseInt(ChangeProgramQuery.attribute("RunTime")
					.getValue()));

		Element ChangeProgram = ChangeProgramQuery.element("ChangeProgram");

		vo.setFreq(Integer.parseInt(ChangeProgram.attribute("Freq")
						.getValue()));
		try {
			vo.setIndex(Integer.parseInt(ChangeProgram.attribute("Index")
					.getValue()));
			if(vo.getIndex() < 0) {
				vo.setIndex(0);
			}
		} catch (Exception ex) {
			vo.setIndex(0);
		}
        try {
            vo.setSymbolRate(Integer.parseInt(ChangeProgram.attribute("SymbolRate")
                    .getValue()));
        } catch (Exception ex) {
            vo.setSymbolRate(6875);
        }
        
        try {
            vo.setQAM(ChangeProgram.attribute("QAM").getValue());
        } catch (Exception ex) {
            vo.setQAM("QAM64");
        }
		
		vo.setServiceID(Integer.parseInt(ChangeProgram.attribute("ServiceID")
				.getValue()));
		vo.setVideoPID(Integer.parseInt(ChangeProgram.attribute("VideoPID")
				.getValue()));
		vo.setAudioPID(Integer.parseInt(ChangeProgram.attribute("AudioPID")
				.getValue()));
		
		
		//V2.5 CodingFormat="cbr" Width="960" Height="544" Fps="25" Bps="1500000" 
		try{
			vo.setCodingFormat(ChangeProgram.attribute("CodingFormat").getValue());
		}catch(Exception ex){
			
		}
		
		try{
			vo.setWidth(Integer.parseInt(ChangeProgram.attribute("Width").getValue()));
		}catch(Exception ex){
			
		}
		
		try{
			vo.setHeight(Integer.parseInt(ChangeProgram.attribute("Height").getValue()));
		}catch(Exception ex){
			
		}
		
		try{
			vo.setFps(Integer.parseInt(ChangeProgram.attribute("Fps").getValue()));
		}catch(Exception ex){
			
		}
		
		try{
			vo.setBps(Integer.parseInt(ChangeProgram.attribute("Bps").getValue()));
		}catch(Exception ex){
			
		}

		return vo;

	}

	// 手动选台回复上报xml解析
	public ChangeProgramQueryVO getReturnObject(Document document) {

		ChangeProgramQueryVO vo = new ChangeProgramQueryVO();

		Element root = document.getRootElement();

		Element Return = root.element("Return");

		String type = Return.attribute("Type").getValue();
		int value = Integer.parseInt(Return.attribute("Value").getValue());

		if (!type.equals("ChangeProgramQuery")) {
			vo.setReutnValue(1);
			vo.setComment("手动选台xml的Type类型不匹配");
			return vo;

		}
		Element ReturnInfo = root.element("ReturnInfo");
		Element RealStreamURL = ReturnInfo.element("RealStreamURL");

		vo.setReutnValue(value);
		vo.setIndex(Integer.parseInt(RealStreamURL.attribute("Index")
				.getValue()));
		vo.setReturnURL(RealStreamURL.attribute("URL").getValue());
		return vo;

	}

	// 手动选台回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, String url , int value, int channelIndex) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\"" + head.getCenterMsgID() +"\"> \r\n";
		if(0==value){
			xml += "<Return Type=\"ChangeProgramQuery\" Value=\"0\" Desc=\"成功\"/> \r\n";
		}else if(1==value){
			xml += "<Return Type=\"ChangeProgramQuery\" Value=\"1\" Desc=\"失败\"/> \r\n";
		}
		xml += "<ReturnInfo><RealStreamURL Index=\"" + channelIndex + "\" URL=\"" + url
				+ "\"/></ReturnInfo>\r\n";
		xml += "</Msg>";
		return xml;
	}
    
}
