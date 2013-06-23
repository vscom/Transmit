package com.bvcom.transmit.parse.rec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

//自动录像
public class SetAutoRecordChannelParse {

	//自动录像解析xml得到通道号的数组
	@SuppressWarnings("unchecked")
//	public List<SetAutoRecordChannelVO> getDownXml(Document document){
//		List<SetAutoRecordChannelVO> indexlist = new ArrayList();
//		
////        List SetAutoRecordChannellist = XMLExt.getMultiElement("/Msg/SetAutoRecordChannel/Channel", document);
////        
////        for(int i=0; i<SetAutoRecordChannellist.size(); i++) {
////            SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
////            
////            
////        }
//        
//        
//		Element root = document.getRootElement();
//        Element AutoRecord=root.element("SetAutoRecordChannel");
//		for(Iterator<Element> iter=AutoRecord.elementIterator();iter.hasNext();){
//			Element Channel = iter.next();
//			
//			SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
//			
//			Node node =Channel.selectSingleNode("@Action");
//			if(node==null)
//				continue;
//			
//			List indexl = new ArrayList();
//			List freql = new ArrayList();
//			List symbolRatel = new ArrayList();
//			List qaml = new ArrayList();
//			List serviceIDl = new ArrayList();
//			List videoPIDl = new ArrayList();
//			List audioPIDl = new ArrayList();
//			for(Iterator<Element> ite=Channel.elementIterator();ite.hasNext();){
//				Element ChCode = ite.next();
//				
//				indexl.add(Integer.parseInt(ChCode.attribute("Index").getValue()));
//				freql.add(Integer.parseInt(ChCode.attribute("Freq").getValue()));
//                try {
//                    symbolRatel.add(Integer.parseInt(ChCode.attribute("SymbolRate").getValue()));    
//                } catch (Exception ex) {
//                    symbolRatel.add(6875);
//                }
//				try {
//                    qaml.add(ChCode.attribute("QAM").getValue());
//                } catch (Exception ex) {
//                    qaml.add("QAM64");
//                }
//				
//				serviceIDl.add(Integer.parseInt(ChCode.attribute("ServiceID").getValue()));
//				videoPIDl.add(Integer.parseInt(ChCode.attribute("VideoPID").getValue()));
//				audioPIDl.add(Integer.parseInt(ChCode.attribute("AudioPID").getValue()));
//				
//			}
//			vo.setAction((node.getText()));
//			vo.setIndex(indexl);
//			vo.setFreq(freql);
//			vo.setSymbolRate(symbolRatel);
//			vo.setQAM(qaml);
//			vo.setServiceID(serviceIDl);
//			vo.setVideoPID(videoPIDl);
//			vo.setAudioPID(audioPIDl);
//			indexlist.add(vo);
//		}
//		return indexlist;
//		
//	}
	
	public List<SetAutoRecordChannelVO> getDownXml(Document document){
		List<SetAutoRecordChannelVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
        Element AutoRecord=root.element("SetAutoRecordChannel");
        int symbolRate = 0;
        String qam = "";
		for(Iterator<Element> iter=AutoRecord.elementIterator();iter.hasNext();){
			Element Channel = iter.next();
			
			Node node =Channel.selectSingleNode("@Action");
			if(node==null)
				continue;
			
			for(Iterator<Element> ite=Channel.elementIterator();ite.hasNext();){
				Element ChCode = ite.next();
				
				SetAutoRecordChannelVO vo = new SetAutoRecordChannelVO();
				
				try {
					vo.setIndex(Integer.parseInt(ChCode.attribute("Index").getValue()));
					vo.setDownIndex(Integer.parseInt(ChCode.attribute("Index").getValue()));
	            } catch (Exception ex) {
	            	vo.setIndex(0);
	            	vo.setDownIndex(0);
	            }
				vo.setFreq(Integer.parseInt(ChCode.attribute("Freq").getValue()));
                try {
                	symbolRate = Integer.parseInt(ChCode.attribute("SymbolRate").getValue());
                	if(symbolRate == 0) {
                		vo.setSymbolRate(6875);
                	} else {
                		vo.setSymbolRate(symbolRate);
                	}
                	
                } catch (Exception ex) {
                	vo.setSymbolRate(6875);
                }
				try {
					qam = ChCode.attribute("QAM").getValue();
					if(qam.equals("")) {
						vo.setQAM(64);
					} else if(qam.equals("QAM16")){
						vo.setQAM(16);
					} else if(qam.equals("QAM32")){
						vo.setQAM(32);
					} else if(qam.equals("QAM64")){
						vo.setQAM(64);
					} else if(qam.equals("QAM128")){
						vo.setQAM(128);
					} else if(qam.equals("QAM256")){
						vo.setQAM(256);
					}
					
                } catch (Exception ex) {
                	vo.setQAM(64);
                }
				
                vo.setServiceID(Integer.parseInt(ChCode.attribute("ServiceID").getValue()));
                
                try {
                	vo.setVideoPID(Integer.parseInt(ChCode.attribute("VideoPID").getValue()));
                } catch (Exception ex) {
                	vo.setVideoPID(0);
                }
                try {
	                vo.setAudioPID(Integer.parseInt(ChCode.attribute("AudioPID").getValue()));
	            } catch (Exception ex) {
	            	vo.setAudioPID(0);
	            }
                try {
	                vo.setRecordType(Integer.parseInt(ChCode.attribute("RecordType").getValue()));
	            } catch (Exception ex) {
	            	vo.setRecordType(2);
	            }
	            
                vo.setAction((node.getText()));
                
                if("Set".equals(Channel.attribute("Action").getValue())){
                vo.setCodingFormat(Channel.attribute("CodingFormat").getValue());
                vo.setWidth(Channel.attribute("Width").getValue());
                vo.setHeight(Channel.attribute("Height").getValue());
                vo.setFps(Channel.attribute("Fps").getValue());
                vo.setBps(Channel.attribute("Bps").getValue());
                }
                indexlist.add(vo);
			}
			
		}
		return indexlist;
		
	}
	
	public String createForDownXML(MSGHeadVO bsData, List<SetAutoRecordChannelVO> AutoRecordlist, String action, boolean isSMG){
		StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getSrcCode()
                + "\" DstCode=\"" + bsData.getDstCode() + "\" SrcURL=\"" + bsData.getSrcURL() + "\" Priority=\"" + bsData.getPriority() + "\">\r\n");
        
        strBuff.append("<SetAutoRecordChannel>\r\n");
        if("Set".equals(action)&& !isSMG ){
        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(0);
        	strBuff.append("   <Channel Action=\"" + action + "\" CodingFormat=\"" + vo.getCodingFormat() + "\"" +
        			 "\" Width=\"" + vo.getWidth() + "\"" +
        					"\" Height=\"" + vo.getHeight() + "\"" +
        							"\" Fps=\"" + vo.getFps() +"\" Bps=\"" + vo.getBps() + "\">\r\n");
        }else{
        	strBuff.append("   <Channel Action=\"" + action + "\">\r\n");
        }
        
        for(int i=0; i< AutoRecordlist.size(); i++) 
        {
        	SetAutoRecordChannelVO vo = (SetAutoRecordChannelVO)AutoRecordlist.get(i);
            
        	strBuff.append("		<ChCode Index=\"" + (isSMG?vo.getDevIndex():vo.getIpmIndex()) + "\" DevIndex=\"" + vo.getDevIndex() + "\" TscIndex=\"" + vo.getTscIndex() + "\" Freq=\"" + vo.getFreq() + "\" SymbolRate=\""+ vo.getSymbolRate() +"\" ");
        	strBuff.append(" QAM=\"QAM" + vo.getQAM() + "\" ServiceID=\""+ vo.getServiceID() +"\" VideoPID=\""+vo.getVideoPID()+"\"");
        	strBuff.append(" AudioPID=\""+vo.getAudioPID()+"\" RecordType=\"" + vo.getRecordType() + "\" />\r\n");
            
        }
        strBuff.append("   </Channel>\r\n");
        strBuff.append("</SetAutoRecordChannel>\r\n");
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
	}
	
	// 自动录像回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, int value,int temp) {
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"SetAutoRecordChannel\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			if(temp==-1){
				xml += "<Return Type=\"SetAutoRecordChannel\" Value=\"1\" Desc=\"失败-没有更多的资源可用\"/>";
				xml +="</Msg>";
			}else{
				xml += "<Return Type=\"SetAutoRecordChannel\" Value=\"1\" Desc=\"失败-没有更多的资源可用,剩余资源："+temp+"\"/>";
				xml +="</Msg>";
			}
			return xml;
		}
		xml += "</Msg>";
		return xml;
	}
    
    public String getDevDownXML(SetAutoRecordChannelVO vo, MSGHeadVO head) {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getSrcCode());
        strBuf.append("\" DstCode=\"" + head.getDstCode() + "\" SrcURL=\"http://127.0.0.1\" Priority=\"1\" >");
        strBuf.append("  <SetAutoRecordChannel>");
        
        return strBuf.toString();
    }
    
    public String replaceString(String strText) {
        
        strText = strText.replaceAll("SymbolRate=\"\"", "SymbolRate=\"6875\"");
        
        strText = strText.replaceAll("QAM=\"\"", "QAM=\"QAM64\"");
        
        return strText;
    }
}
