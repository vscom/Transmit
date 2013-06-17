package com.bvcom.transmit.parse.si;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.si.ChannelScanQueryVO;
import com.bvcom.transmit.vo.si.elementryPIDVO;

//频道扫描
public class ChannelScanQueryParse {
	
	public ChannelScanQueryVO getDownObject(Document document){
		
		ChannelScanQueryVO vo = new ChannelScanQueryVO();
		
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			Attribute scanTime = element.attribute("ScanTime");
			vo.setScanTime(scanTime.getValue());
//			Attribute symbolRate = element.attribute("SymbolRate");
//			Attribute qam = element.attribute("QAM");
			
			try {
			/**
			 * ScanType=0为简单
			 * 当ScanType=1时为详细扫描
			 */
				Attribute scanType = element.attribute("ScanType");
				vo.setScanType(Integer.parseInt(scanType.getValue()));
			} catch (Exception ex) {
				vo.setScanType(1);
			}
//			vo.setSymbolRate(Integer.parseInt(symbolRate.getValue()));
//			vo.setQAM(qam.getValue());
			break;
		}
		return vo;
	}
	
	/**
	 * 指定扫描协议打包
	 * @return
	 */
	public String createChannelScanXML(String str,MSGHeadVO head){
		//<?xml version="1.0" encoding="UTF-8"?>
		//<Msg Version="2.3" MsgID="2" Type="MonDown" DateTime="2002-08-17 15:30:00" SrcCode="110000X01" DstCode="110000N01" 
		//SrcURL="http://10.1.1.1:8089/" Priority="1">  
		//<ChannelScanQuery ScanTime="" StartFreq="" EndFreq="" StepFreq="" ScanType="1">		
		//</ChannelScanQuery></Msg>
		StringBuffer strBuffer=new StringBuffer();
		strBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		strBuffer.append("<Msg Version=\""+head.getVersion()+"\" MsgID=\""+head.getCenterMsgID()+"\" Type=\"MonDown\" DateTime=\""+CommonUtility.getDateTime()+"\" SrcCode=\""+head.getSrcCode()+"\" DstCode=\""+head.getDocument()+"\"");
		strBuffer.append("SrcURL=\""+head.getSrcURL()+"\" Priority=\""+head.getPriority()+"\">");
		strBuffer.append("<ChannelScanQuery ScanTime=\"\" StartFreq=\""+str.split(",")[1]+"\" EndFreq=\""+str.split(",")[2]+"\" StepFreq=\"8000\" ScanType=\"1\">");
		strBuffer.append("<ScanInfo SymbolRate=\"6875\" QAM=\"64\" />");
		strBuffer.append("</ChannelScanQuery></Msg>");
		return strBuffer.toString();
	}
	
	@SuppressWarnings("unchecked")
	public List<ChannelScanQueryVO> getReturnObject(Document document){
		List<ChannelScanQueryVO> list = new ArrayList();
		
		Element root = document.getRootElement();
		
		Element Return = root.element("Return");
		
		String type = Return.attribute("Type").getValue();
		int value = Integer.parseInt(Return.attribute("Value").getValue());
		if(!type.equals("ChannelScanQuery")){
			return list;
		}
		
		Element ReturnInfo=root.element("ReturnInfo");
		Element ChannelScanQuery=ReturnInfo.element("ChannelScanQuery");
		String scanTime=ChannelScanQuery.attribute("ScanTime").getValue();

		for(Iterator<Element> iter=ChannelScanQuery.elementIterator();iter.hasNext();){
			Element cs = iter.next();
			int freq = Integer.parseInt(cs.attribute("Freq").getValue());
			int orgnetid=Integer.parseInt(cs.attribute("OrgNetID").getValue());
			int tsid=Integer.parseInt(cs.attribute("TsID").getValue());
			String qam=cs.attribute("QAM").getValue();
			int SymbolRate=Integer.parseInt(cs.attribute("SymbolRate").getValue());
			
			for(Iterator<Element> ite=cs.elementIterator();ite.hasNext();){
				Element channel = ite.next();
				ChannelScanQueryVO vo = new ChannelScanQueryVO();
										
				vo.setScanTime(scanTime);
				vo.setFreq(freq);
				vo.setOrgNetID(orgnetid);
				vo.setTsID(tsid);
				vo.setQAM(qam);
				vo.setSymbolRate(SymbolRate);
				vo.setProgram(channel.attribute("Program").getValue());
				vo.setProgramID(Integer.parseInt(channel.attribute("ProgramID").getValue()));
				vo.setServiceID(Integer.parseInt(channel.attribute("ServiceID").getValue()));
				
				//vo.setPcr_PID(Integer.parseInt(channel.attribute("Pcr_PID").getValue()));
				vo.setVideoPID(Integer.parseInt(channel.attribute("VideoPID").getValue()));
				vo.setAudioPID(Integer.parseInt(channel.attribute("AudioPID").getValue()));
				
				try {
					vo.setEncrypt(Integer.parseInt(channel.attribute("Encrypt").getValue()));						
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					vo.setHDTV(Integer.parseInt(channel.attribute("HDTV").getValue()));						
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//added by tqy
				try{
					vo.setServiceType(channel.attribute("ServiceType").getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}

				for(Iterator<Element> pidEle=channel.elementIterator();pidEle.hasNext();){
					Element elementryPID = pidEle.next();
					try {
						elementryPIDVO pidVO = new elementryPIDVO();
						pidVO.setStreamType(Integer.parseInt(elementryPID.attribute("StreamType").getValue()));
						pidVO.setPid(Integer.parseInt(elementryPID.attribute("PID").getValue()));
						vo.addElementryPIDList(pidVO);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
				list.add(vo);
			}

		}
		
		return list;
	}
    
    /**
     * 生成返回XML信息
     * @param head
     * @param value 0:成功 1: 失败
     * @return
     */
    public String getReturnXML(MSGHeadVO head, int value) {

        String xml = null;
        xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
        xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
                + head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
                + "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\"" + head.getCenterMsgID() + "\">";
        if(0==value){
            xml += "<Return Type=\"ChangeProgramQuery\" Value=\"0\" Desc=\"成功\"/>";
        }else if(1==value){
            xml += "<Return Type=\"ChangeProgramQuery\" Value=\"1\" Desc=\"失败\"/>";
        }
        xml += "</Msg>";
        return xml;
    }
    
    /**
     * 生成返回XML信息
     * @param head
     * @param value 0:成功 1: 失败
     * @return
     */
    public String createChannelScanReturnXML( List<ChannelScanQueryVO> VOList, MSGHeadVO head, int value) {

    	if(VOList == null) {
    		return "";
    	}
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n");
        xml.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
                + head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
                + "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\"" + head.getReplyID() + "\">\r\n");
        if(0==value){
        	xml.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n");
        }else if(1==value){
        	xml.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\"/>\r\n");
        }
        
        xml.append("<ReturnInfo>\r\n");
        int beFreq = 0;
        for(int i=0; i<VOList.size(); i++) {
        	ChannelScanQueryVO vo = VOList.get(i);
        	if(!vo.getIsNewProgram()) {
        		continue;
        	}
        	if(beFreq != vo.getFreq()) {
        		xml.append("<ChannelScan Freq=\""+ vo.getFreq() +"\" OrgNetID=\"" +vo.getOrgNetID()+ "\" TsID=\"" 
        				+ vo.getTsID() + "\" QAM=\"" +vo.getQAM()+ "\" SymbolRate=\"" + vo.getSymbolRate() + "\">\r\n");
        	}
        	
        	xml.append("<Channel Program=\""+vo.getProgram()+"\" ProgramID=\"" + vo.getProgramID() + "\" ServiceID=\"" + 
        			vo.getServiceID() + "\" VideoPID=\"" + vo.getVideoPID() + "\" AudioPID=\"" + 
        			vo.getAudioPID()+ "\" Encrypt=\"" + vo.getEncrypt() + "\" HDTV=\"" + vo.getHDTV() + "\">\r\n");
        	
        	for(int j=0; j<vo.getElementryPIDList().size(); j++) {
        		elementryPIDVO pidVO = (elementryPIDVO)vo.getElementryPIDList().get(j);
        		xml.append("<elementryPID StreamType=\"" + pidVO.getStreamType() + "\" PID=\"" +pidVO.getPid()+ "\" />\r\n");
        	}
        	
        	if(beFreq != vo.getFreq()) {
        		xml.append("</ChannelScan>\r\n");
        	}
        }
        
        xml.append("</ReturnInfo>\r\n");
        xml.append("</Msg>");
        return xml.toString();
    }
    
}
