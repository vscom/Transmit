package com.bvcom.transmit.parse.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

/**
 * 轮询监测
 * @author Bian Jiang
 *
 */
public class ProgramPatrolParse {
	/**
	 * 新增方法解析xml获取频点集合
	 * @param document解析对象
	 * @return 频点的集合
	 */
	public List<String> getFreqXml(Document document){
		Element root = document.getRootElement();
        Element ReturnInfo=root.element("ReturnInfo");
        List<String> freqList =new ArrayList<String>();
        try {
	        for(Iterator<Element> iter=ReturnInfo.elementIterator();iter.hasNext();){
				Element PatrolGroup = iter.next();
				for(Iterator<Element> it=PatrolGroup.elementIterator();it.hasNext();){
					Element Channel  = it.next();
					//System.out.println("***"+utilXML.XMLToString(Channel.getDocument()));
					freqList.add(Channel.attribute("Freq").getValue());
				}
					
			}
        }catch (Exception ex) {
        	ex.printStackTrace();
        }
		return freqList;
	}
	public List<MonitorProgramQueryVO> parseReturnXml(Document document){
		
		Element root = document.getRootElement();
		
		Element Return = root.element("Return");

		String type = Return.attribute("Type").getValue();
		int value = Integer.parseInt(Return.attribute("Value").getValue());
		
		if(value != 0) {
			return null;
		}
		
		List<MonitorProgramQueryVO> indexlist = new ArrayList();
		
        Element ReturnInfo=root.element("ReturnInfo");
        
		for(Iterator<Element> iter=ReturnInfo.elementIterator();iter.hasNext();){
			Element PatrolGroup = iter.next();
			
			MonitorProgramQueryVO vo = new MonitorProgramQueryVO();
			
			try {
				int index = Integer.parseInt(PatrolGroup.attribute("Index").getValue());
				if(index < 0) {
					continue;
				}
				vo.setPatrolGroupIndex(index);
            } catch (Exception ex) {
            	vo.setPatrolGroupIndex(0);
            }
            
            indexlist.add(vo);
		}
		return indexlist;
	}
}
