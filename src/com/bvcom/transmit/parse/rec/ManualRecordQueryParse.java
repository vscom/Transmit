package com.bvcom.transmit.parse.rec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.ManualRecordQueryVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

//�ֶ�¼��
public class ManualRecordQueryParse {
	
	//�ֶ�¼�����xml�õ�ͨ���ŵ�����
	@SuppressWarnings("unchecked")
	public List<ManualRecordQueryVO> getIndexByDownXml(Document document){
		List<ManualRecordQueryVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element StreamRoundInfoQuery = root.element("ManualRecordQuery");
		for(Iterator<Element> iter=StreamRoundInfoQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			Node node =element.selectSingleNode("@Index");
            ManualRecordQueryVO vo = new ManualRecordQueryVO();
            vo.setIndex(Integer.parseInt(node.getText()));
            
            node =element.selectSingleNode("@Time");
            vo.setTime(node.getText());
            
            
            node =element.selectSingleNode("@Freq");
            try {
            	vo.setFreq(Integer.parseInt(node.getText()));
            } catch (Exception ex) {
            	
            }
            node =element.selectSingleNode("@ServiceID");
            try {
            	vo.setServiceID(Integer.parseInt(node.getText()));	
            } catch (Exception ex) {
            	
            }
            
         
           try{
        	   //�����������Э�飺�ֶ�¼����Чʱ��
               node = element.selectSingleNode("@Lifecycle");
               
        	   vo.setFileSaveTime(node.getText());
        	   
           }catch(Exception ex){
        	   
           }
           
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	public String createForDownXML(MSGHeadVO bsData, List<ManualRecordQueryVO> ManualRecordlist, MonitorProgramQueryVO rtvsVO){
		StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
        strBuff.append("<Msg Version=\"" + bsData.getVersion() + "\" MsgID=\""
                + bsData.getCenterMsgID() + "\" Type=\"MonDown\" DateTime=\""
                + CommonUtility.getDateTime() + "\" SrcCode=\"" + bsData.getSrcCode()
                + "\" DstCode=\"" + bsData.getDstCode() + "\" SrcURL=\"" + bsData.getSrcURL() + "\" Priority=\"" + bsData.getPriority() + "\">\r\n");
        
        strBuff.append("<ManualRecordQuery>\r\n");
        for(int i=0; i<ManualRecordlist.size(); i++) {
        	 ManualRecordQueryVO vo  = ManualRecordlist.get(i);
        	strBuff.append("  <ManualRecord  Index=\"" + vo.getIndex() + "\" Time=\"" +vo.getTime()+ "\" Freq=\"" + vo.getFreq() + "\" ");
        	if(bsData.getVersion().equals("2.3")){
        		strBuff.append(" ServiceID=\"" + vo.getServiceID() + "\"  IP=\"" + rtvsVO.getRtvsIP() + "\" Port=\"" + rtvsVO.getRtvsPort() +"\" />\r\n");
        	}
        	else{
        		strBuff.append(" ServiceID=\"" + vo.getServiceID() + "\"  IP=\"" + rtvsVO.getRtvsIP() + "\" Port=\"" + rtvsVO.getRtvsPort() +"\" fileSaveTime=\"" + vo.getFileSaveTime()+ "\"/>\r\n");
        	}
        }
        
        strBuff.append("</ManualRecordQuery>\r\n");
        strBuff.append("</Msg>\r\n");
		return strBuff.toString();
		
	}
	
	// �ֶ�¼��ظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head,int value) {

        StringBuffer strBuff = new StringBuffer();
        strBuff.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuff.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuff.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuff.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuff.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">");
		if(0==value){
            strBuff.append("<Return Type=\"ManualRecordQuery\" Value=\"0\" Desc=\"�ɹ�\"/>");
		}else if(1==value){
            strBuff.append("<Return Type=\"ManualRecordQuery\" Value=\"1\" Desc=\"ʧ��\"/>");
		}
        strBuff.append("</Msg>");
		return strBuff.toString();

	}

}
