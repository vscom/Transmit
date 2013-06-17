package com.bvcom.transmit.parse.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class MonitorProgramQueryParse {
	/**
	 * 新增方法解析xml获取频点集合
	 * @param document解析对象
	 * @return 频点的集合
	 */
	public List<String> getFreqXml(Document document){
		List<String> freqList=new ArrayList<String>();
		Element root = document.getRootElement();
        Element AutoRecord=root.element("MonitorProgramQuery");
		for(Iterator<Element> iter=AutoRecord.elementIterator();iter.hasNext();){
			Element ChCode = iter.next();
            freqList.add(ChCode.attribute("Freq").getValue());
		}
		return freqList;
	}
	public List<MonitorProgramQueryVO> getDownXml(Document document){
		List<MonitorProgramQueryVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
        Element AutoRecord=root.element("MonitorProgramQuery");
        int symbolRate = 0;
        int qam = 0;
		for(Iterator<Element> iter=AutoRecord.elementIterator();iter.hasNext();){
			Element ChCode = iter.next();
			
			MonitorProgramQueryVO vo = new MonitorProgramQueryVO();
			
			try {
				vo.setIndex(Integer.parseInt(ChCode.attribute("Index").getValue()));
            } catch (Exception ex) {
            	vo.setIndex(0);
            }
            vo.setPatrolGroupIndex(vo.getIndex());
            
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
				qam = Integer.parseInt(ChCode.attribute("QAM").getValue());
				if(qam == 0) {
					vo.setQAM(64);
				} else {
					vo.setQAM(qam);
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
            
            indexlist.add(vo);
			
		}
		return indexlist;
	}
	
	
	public String createForDownXML(MSGHeadVO bsData, MonitorProgramQueryVO vo, int isSMG){
		StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getSrcCode()
                + "\" DstCode=\"" + bsData.getDstCode() + "\" SrcURL=\"" + bsData.getSrcURL() + "\" Priority=\"" + bsData.getPriority() + "\">\r\n");
        
        strBuff.append("<MonitorProgramQuery Action=\"" + (isSMG==1?"Set":"Del") + "\">\r\n");
    	strBuff.append(" <MonitorProgram  Index=\"" + vo.getSmgIndex() + "\" Freq=\"" + vo.getFreq() + "\" SymbolRate=\""+ vo.getSymbolRate() +"\" ");
    	strBuff.append(" QAM=\"QAM" + vo.getQAM() + "\" ServiceID=\""+ vo.getServiceID() +"\" VideoPID=\""+vo.getVideoPID()+"\"");
    	strBuff.append(" AudioPID=\""+vo.getAudioPID()+"\"");
    	strBuff.append(" rtvsIP=\"" + vo.getRtvsIP() + "\" rtvsPort=\""+ vo.getRtvsPort() +"\" />\r\n");
    
        strBuff.append("</MonitorProgramQuery>\r\n");
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
		
	}
	
    public String getReturnXML(MSGHeadVO head, int value, String rtvsURL) {
        
        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>\r\n");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">\r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\"/>\r\n");
        }
        strBuf.append("<ReturnInfo>\r\n");
        strBuf.append("<RealStreamURL URL=\""+ rtvsURL +"\"/>\r\n");
        strBuf.append("</ReturnInfo>\r\n");
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
	
}
