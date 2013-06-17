package com.bvcom.transmit.parse.alarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.AlarmThread;
import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.XMLExt;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchPSetVO;


//报警上报（节目相关）
public class AlarmSearchPSetParse {
    
	private static Logger log = Logger.getLogger(AlarmSearchPSetParse.class.getSimpleName());
	MemCoreData coreData = MemCoreData.getInstance();
	SysInfoVO sysVO = coreData.getSysVO();
	 
    /**
     * 下发XML信息
     * @param document
     * @return
     */
    public List getDownList(Document document){
        List<AlarmSearchPSetVO> ReturnList = new ArrayList<AlarmSearchPSetVO>();
        
        List AlarmSearchPSetlist = XMLExt.getMultiElement("/Msg/AlarmSearchPSet", document);
        
        for(int i=0; i<AlarmSearchPSetlist.size(); i++) {
            AlarmSearchPSetVO AlarmSearchPSet = new AlarmSearchPSetVO();
            AlarmSearchPSet.setIndex(Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "Index")));
            AlarmSearchPSet.setFreq(Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "Freq")));
            AlarmSearchPSet.setServiceID(Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "ServiceID")));
            AlarmSearchPSet.setVideoPID(Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "VideoPID")));
            AlarmSearchPSet.setAudioPID(Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "AudioPID")));
            ReturnList.add(AlarmSearchPSet);
        }
        return ReturnList;
    }
    
    /**
     * 上报XML信息
     * @param document
     * @return
     */
    public List getUpList(Document document){
        List<AlarmSearchPSetVO> ReturnList = new ArrayList<AlarmSearchPSetVO>();
        
        List AlarmSearchPSetlist = XMLExt.getMultiElement("/Msg/ReturnInfo/AlarmSearchPSet", document);
        
        for(int i=0; i<AlarmSearchPSetlist.size(); i++) {
        	
        	int Index = 0;
        	int Freq = 0;
        	int ServiceID = 0;
        	int VideoPID = 0;
        	int AudioPID = 0;
        	
            try {
            	Index = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "Index"));	
            } catch (Exception ex) {
            }
            try {
            	Freq = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "Freq"));
            } catch (Exception ex) {
            }
            try {
            	ServiceID = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "ServiceID"));
            } catch (Exception ex) {
            }
            try {
            	VideoPID = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "VideoPID"));
            } catch (Exception ex) {
            }
            try {
            	AudioPID = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPSetlist.get(i), "AudioPID"));
            } catch (Exception ex) {
            }
            
            Element element = (Element)AlarmSearchPSetlist.get(i);
            
            List AlarmSearchPlist = element.elements();
        	
        	for(int j=0; j < AlarmSearchPlist.size(); j ++) {
        		
                AlarmSearchPSetVO AlarmSearchPSet = new AlarmSearchPSetVO();
                
        		AlarmSearchPSet.setIndex(Index);
        		AlarmSearchPSet.setFreq(Freq);
        		AlarmSearchPSet.setServiceID(ServiceID);
        		AlarmSearchPSet.setVideoPID(VideoPID);
        		AlarmSearchPSet.setAudioPID(AudioPID);
        		
        		AlarmSearchPSet.setType(Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPlist.get(j), "Type")));
        		AlarmSearchPSet.setDesc(XMLExt.getElementValue((Element)AlarmSearchPlist.get(j), "Desc"));
        		
        		int value = Integer.valueOf(XMLExt.getElementValue((Element)AlarmSearchPlist.get(j), "Value"));
        		AlarmSearchPSet.setValue(value);
        		AlarmSearchPSet.setTime(XMLExt.getElementValue((Element)AlarmSearchPlist.get(j), "Time"));
        		
        		AlarmSearchPSet.setAlarmType("AlarmSearchPSet");
        		
        		if (value != 1 && value != 2) {
        			continue;
        		}
        		
        		ReturnList.add(AlarmSearchPSet);
        	}
        }
        return ReturnList;
    }
    
    public void replaceFreqInfo(Document document, List AlarmSearchPSetlist) {
        Element root = document.getRootElement();
        Attribute attr = null;
        Element ruturnEle = null, eleTo = null;
        
        for (Iterator i = root.elementIterator(); i.hasNext();) {
            ruturnEle = (Element) i.next();
            if (ruturnEle.getName().compareTo("ReturnInfo") == 0) {
                int count = 0;
                for (Iterator j = ruturnEle.elementIterator(); j.hasNext();) {
                    Element AlarmEle = (Element) j.next();
                    if (AlarmEle.getName().compareTo("AlarmSearchPSet") == 0) {
                        AlarmSearchPSetVO AlarmSearchPSet = (AlarmSearchPSetVO)AlarmSearchPSetlist.get(count);
                        for (Iterator k = AlarmEle.attributeIterator(); k.hasNext();) {
                            attr = (Attribute) k.next();
                            if (attr.getName().compareTo("Index") == 0) {
                                attr.setValue(String.valueOf(AlarmSearchPSet.getIndex()));
                            } else if(attr.getName().compareTo("Freq") == 0) {
                                attr.setValue(String.valueOf(AlarmSearchPSet.getFreq()));
                            } else if(attr.getName().compareTo("ServiceID") == 0) {
                                attr.setValue(String.valueOf(AlarmSearchPSet.getServiceID()));
                            } else if(attr.getName().compareTo("VideoPID") == 0) {
                                attr.setValue(String.valueOf(AlarmSearchPSet.getVideoPID()));
                            } else if(attr.getName().compareTo("AudioPID") == 0) {
                                attr.setValue(String.valueOf(AlarmSearchPSet.getAudioPID()));
                            }
                        }
                        count++;
                    }
                }
            }
        }
    }
    
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
	  		<AlarmSearchPSet Index="0" Freq="658000" ServiceID="10" VideoPID="2060" AudioPID="2061">
	  			   <AlarmSearchP Type="31" AlarmID="123456789012" Desc="静帧" Value="1" Time="2002-08-17 15:30:00"/> 
	  		</AlarmSearchPSet>
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
        	
        	if (sysVO.getIsHasAlarmID() == 1) {
        		if (AlarmSearchPSet.getAlarmID() == null || AlarmSearchPSet.getAlarmID().equals("")) {
        			log.info("生成节目报警XML出错 Freq:" + AlarmSearchPSet.getFreq() + " AlarmID: " + AlarmSearchPSet.getAlarmID());
        			continue;
        		}
        	}
        	
        	if (freq != AlarmSearchPSet.getFreq()) {
        		if (i != 0) {
        			strBuf.append("	</AlarmSearchPSet>\r\n");
        		}
        		strBuf.append("	<AlarmSearchPSet Index=\"0\" Freq=\"" + AlarmSearchPSet.getFreq() + "\" ServiceID=\"" + AlarmSearchPSet.getServiceID() + "\" VideoPID=\"" + AlarmSearchPSet.getVideoPID() + "\" AudioPID=\"" + AlarmSearchPSet.getAudioPID() + "\" >\r\n");
        	}
        	freq = AlarmSearchPSet.getFreq();

        	strBuf.append("		<AlarmSearchP Type=\"" + AlarmSearchPSet.getType() + "\"");
        	
        	if (sysVO.getIsHasAlarmID() == 1) {
        		strBuf.append(" AlarmID=\"" + AlarmSearchPSet.getAlarmID() + "\"");
        	}
        	
        	strBuf.append(" Desc=\"" + AlarmSearchPSet.getDesc() + "\" Value=\"" + AlarmSearchPSet.getValue() + "\" Time=\"" + AlarmSearchPSet.getTime() + "\"/> \r\n");
        	
        	if (i == AlarmSearchPSetList.size() - 1) {
                strBuf.append("	</AlarmSearchPSet>\r\n");
        	}
        	count++;
        }
        
        if (count == 0) {
        	return null;
        }
        
        strBuf.append("</ReturnInfo>");
        
        strBuf.append("</Msg>");
        return strBuf.toString();
	}
	
	
}
