package com.bvcom.transmit.parse.alarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.parse.alarm.domain.AlarmSwitch;
import com.bvcom.transmit.parse.alarm.domain.AlarmSwitchDao;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;

//报警开关（频率）
public class AlarmSwitchSetParse {
	private static Logger log = Logger.getLogger(AlarmSwitchSetParse.class.getSimpleName());
	// 频率报警开关回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime()+ "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"AlarmSwitchSet\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"AlarmSwitchSet\" Value=\"1\" Desc=\"失败\"/>";
		}
		xml += "</Msg>";
		return xml;

	}
	/**
	 * 解析出频率报警开关状态 信息入库
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
				Element AlarmSwitchSet = iter.next();
				for(Iterator<Element> ite=AlarmSwitchSet.elementIterator();ite.hasNext();){
					Element AlarmSwitch  = ite.next();
					alarmSwitch=new AlarmSwitch();
					alarmSwitch.setFreq(AlarmSwitchSet.attribute("Freq").getValue());
					alarmSwitch.setServiceID("0");
					alarmSwitch.setSwitchType(2);
					alarmSwitch.setSwitchValue(Integer.parseInt(AlarmSwitch.attribute("Switch").getValue()));
					alarmSwitch.setAlarmType(Integer.parseInt(AlarmSwitch.attribute("Type").getValue()));
					alarmSwitchList.add(alarmSwitch);
				}
			}
        }catch (Exception ex) {
        	log.error("解析频率开关错误"+ex);
        }
        System.out.println(alarmSwitchList);
	    dao.save(alarmSwitchList);
	    
	}

}
