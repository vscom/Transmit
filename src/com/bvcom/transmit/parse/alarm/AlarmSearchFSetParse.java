package com.bvcom.transmit.parse.alarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.XMLExt;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchPSetVO;

//报警上报（频率相关）
public class AlarmSearchFSetParse {
	
	private static Logger log = Logger.getLogger(AlarmSearchFSetParse.class.getSimpleName());
	
	MemCoreData coreData = MemCoreData.getInstance();
	SysInfoVO sysVO = coreData.getSysVO();
	
    /**
     * 上报XML信息
     * @param document
     * @return
     */
    public List getUpList(Document document){
        List<AlarmSearchPSetVO> ReturnList = new ArrayList<AlarmSearchPSetVO>();
        
        List AlarmSearchFSetlist = XMLExt.getMultiElement("/Msg/ReturnInfo/AlarmSearchFSet", document);
        
        for(int i=0; i<AlarmSearchFSetlist.size(); i++) {
        	
        	int Index = 0;
        	int Freq = 0;
        	
        	//频点级报警没有ServiceID VideoPID AudioPID  吉龙修改
        	
        	int ServiceID = 0;
        	int VideoPID = 0;
        	int AudioPID = 0;
        	
            try {
            	Index = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchFSetlist.get(i), "Index"));	
            } catch (Exception ex) {
            }
            try {
            	Freq = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchFSetlist.get(i), "Freq"));
            } catch (Exception ex) {
            }
            
            Element element = (Element)AlarmSearchFSetlist.get(i);
            
            List AlarmSearchFlist = element.elements();
        	
        	for(int j=0; j < AlarmSearchFlist.size(); j ++) {
        		
                AlarmSearchPSetVO AlarmSearchPSet = new AlarmSearchPSetVO();
                
        		AlarmSearchPSet.setIndex(Index);
        		AlarmSearchPSet.setFreq(Freq);
        		AlarmSearchPSet.setServiceID(ServiceID);
        		AlarmSearchPSet.setVideoPID(VideoPID);
        		AlarmSearchPSet.setAudioPID(AudioPID);
        		
        		AlarmSearchPSet.setType(Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchFlist.get(j), "Type")));
        		AlarmSearchPSet.setDesc(XMLExt.getElementValue((Element)AlarmSearchFlist.get(j), "Desc"));
        		
        		int value = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchFlist.get(j), "Value"));
        		
        		AlarmSearchPSet.setValue(value);
        		AlarmSearchPSet.setTime(XMLExt.getElementValue((Element)AlarmSearchFlist.get(j), "Time"));
        		
        		AlarmSearchPSet.setAlarmType("AlarmSearchFSet");
        		
        		if (value != 1 && value != 2) {
        			continue;
        		}
        		ReturnList.add(AlarmSearchPSet);
        		
        	}
        }
        return ReturnList;
    }
    
	//节目报警开关解析xml得到通道号的数组
//	@SuppressWarnings("unchecked")
//	public List<AlarmSearchFSetVO> getIndexByDownXml(Document document){
//		List<AlarmSearchFSetVO> indexlist = new ArrayList();
//		
//		Element root = document.getRootElement();
//		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
//			Element element = iter.next();
//			Node node =element.selectSingleNode("@Index");
//			if(node==null)
//				continue;
//			
//			AlarmSearchFSetVO vo = new AlarmSearchFSetVO();
//			vo.setIndex(Integer.parseInt(node.getText()));
//			indexlist.add(vo);
//		}
//		return indexlist;
//		
//	}
	
	// 报警上报回复xml打包
//	public String ReturnXMLByURL(MSGHeadVO head,List<AlarmSearchFSetVO> list, int value) {
//		
//		String xml = null;
//		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
//		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
//				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
//				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
//				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
//		if(0==value){
//			xml += "<Return Type=\"AlarmSearchFSet\" Value=\"0\" Desc=\"成功\"/>";
//		}else if(1==value){
//			xml += "<Return Type=\"AlarmSearchFSet\" Value=\"1\" Desc=\"失败\"/>";
//			xml +="</Msg>";
//			return xml;
//		}
//		xml +="<ReturnInfo>";
//		for(int i=0;i<list.size();i++){
//			xml +="<AlarmSearchFSet Freq=\""+list.get(i).getFreq()+"\" Index=\""+list.get(i).getIndex()+"\">";
//				for(int j=0;j<list.get(i).getType().size();j++){
//					xml +="<AlarmSearchF Type=\""+list.get(i).getType().get(j)+"\" Desc=\""+list.get(i).getDesc().get(j)+"\" Value=\""+list.get(i).getValue().get(j)+"\" Time=\""+list.get(i).getTime().get(j)+"\"/>";
//				}
//			xml +="</AlarmSearchFSet>";
//		}
//		xml += "</ReturnInfo></Msg>";
//		return xml;
//	}

	public String createForUpXML(MSGHeadVO head, List<AlarmSearchPSetVO> AlarmSearchPSetList, int value){
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\"/>\r\n");
        }
        
        strBuf.append("<ReturnInfo>\r\n");
        
        /*
        <ReturnInfo>
	  		<AlarmSearchFSet Index="0" Freq="658000" >
	  			       <AlarmSearchF Type="1" AlarmID="123456789012" Desc="失锁" Value="0" Time="2002-08-17 15:30:00"/> 
	  		</AlarmSearchFSet>
  	    </ReturnInfo>
       */
        int freq = 0;
        int count = 0;
        for(int i=0; i<AlarmSearchPSetList.size(); i++) {
        	AlarmSearchPSetVO AlarmSearchPSet = AlarmSearchPSetList.get(i);
        	
        	if (AlarmSearchPSet.getFreq() == 0) {
        		log.info("生成节目报警XML出错 Freq:" + AlarmSearchPSet.getFreq());
        		continue;
        	}
        	
        	if (freq != AlarmSearchPSet.getFreq()) {
        		if (i != 0) {
        			strBuf.append(" </AlarmSearchFSet>\r\n");
        		}
        		strBuf.append("	<AlarmSearchFSet Index=\"0\" Freq=\"" + AlarmSearchPSet.getFreq()+ "\" >\r\n");
        	}
        	freq = AlarmSearchPSet.getFreq();

        	strBuf.append("	<AlarmSearchF Type=\"" + AlarmSearchPSet.getType() + "\"");
        	
        	if (sysVO.getIsHasAlarmID() == 1) {
        		strBuf.append("	AlarmID=\"" + AlarmSearchPSet.getAlarmID() + "\"");
        	}
        	
        	strBuf.append("	Desc=\"" + AlarmSearchPSet.getDesc() + "\" Value=\"" + AlarmSearchPSet.getValue() + "\" Time=\"" + AlarmSearchPSet.getTime() + "\"/> \r\n");
        	
        	if (i == AlarmSearchPSetList.size() - 1) {
                strBuf.append(" </AlarmSearchFSet>\r\n");
        	}
        	count++;
        }

        if (count == 0) {
        	return null;
        }
        strBuf.append("</ReturnInfo>\r\n");
        
        strBuf.append("</Msg>\r\n");
        return strBuf.toString();
	}
	
}
