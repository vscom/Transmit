package com.bvcom.transmit.parse.alarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.parse.alarm.domain.AlarmSwitch;
import com.bvcom.transmit.parse.alarm.domain.AlarmSwitchDao;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.AlarmProgramSwitchSetVO;

//报警开关（节目相关）
public class AlarmProgramSwitchSetParse {
	private static Logger log = Logger.getLogger(AlarmProgramSwitchSetParse.class.getSimpleName());
	//节目报警开关解析xml得到通道号的数组
	@SuppressWarnings("unchecked")
	public List<AlarmProgramSwitchSetVO> getIndexByDownXml(Document document){
		List<AlarmProgramSwitchSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
			
			AlarmProgramSwitchSetVO vo = new AlarmProgramSwitchSetVO();
			vo.setIndex(Integer.parseInt(node.getText()));
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	//节目报警开关解析回复的xml对象
	@SuppressWarnings("unchecked")
	public AlarmProgramSwitchSetVO getReturnByXml(Document document){
		AlarmProgramSwitchSetVO vo = new AlarmProgramSwitchSetVO();
		
		Element root = document.getRootElement();
		Element Return = root.element("Return");
		String type = Return.attribute("Type").getValue();
		int value  = Integer.parseInt(Return.attribute("Value").getValue());
		if(!type.equals("AlarmProgramSwitchSet")){
			vo.setReturnValue(1);
			vo.setComment("节目报警开关xml的type错误");
			return vo;
		}
		vo.setReturnValue(value);
		return vo;
	}
	
	// 节目报警开关回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"AlarmProgramSwitchSet\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"AlarmProgramSwitchSet\" Value=\"1\" Desc=\"失败\"/>";
			xml += "</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;

	}
	/**
	 * 解析出节目报警开关状态 信息入库
	 * @param document
	 * JI LONG  2011-5-12 
	 */
	public void parseDB(Document document){
		List<AlarmSwitch> alarmSwitchList=new ArrayList<AlarmSwitch>();
		AlarmSwitchDao dao=new AlarmSwitchDao();
		Element root = document.getRootElement();
		AlarmSwitch alarmSwitch=null;
        try {
	        for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
				Element AlarmProgramSwitchSet = iter.next();
				for(Iterator<Element> ite=AlarmProgramSwitchSet.elementIterator();ite.hasNext();){
					Element AlarmProgram  = ite.next();
					for(Iterator<Element> it=AlarmProgram.elementIterator();it.hasNext();){
						Element APS =it.next();
						alarmSwitch=new AlarmSwitch();
						alarmSwitch.setFreq(AlarmProgramSwitchSet.attribute("Freq").getValue());
						alarmSwitch.setServiceID(AlarmProgram.attribute("ServiceID").getValue());
						alarmSwitch.setSwitchType(1);
						alarmSwitch.setSwitchValue(Integer.parseInt(APS.attribute("Switch").getValue()));
						alarmSwitch.setAlarmType(Integer.parseInt(APS.attribute("Type").getValue()));
						alarmSwitchList.add(alarmSwitch);
					}
					
				}
			}
        }catch (Exception ex) {
        	log.error("解析节目开关错误"+ex);
        }
	    //System.out.println(alarmSwitchList);
	    dao.save(alarmSwitchList);
	    
	}
}
