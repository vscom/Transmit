package com.bvcom.transmit.parse.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.parse.video.domain.AlarmTime;
import com.bvcom.transmit.parse.video.domain.AlarmTimeDao;
import com.bvcom.transmit.util.AlarmTimeMemory;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.video.AlarmTimeSetVO;

//运行图
public class AlarmTimeSetParse {
	
	//解析收到平台下发的运行图协议，把节目信息入库
	//并且更新内存中节目运行图状态
	//Ji  Long 2011-6-15
	public void parse(Document document){
		List<AlarmTime> list=new ArrayList<AlarmTime>();
		AlarmTimeDao dao=new AlarmTimeDao();
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element AlarmTimeSet = iter.next();
			String freq=AlarmTimeSet.attributeValue("Freq");
			String serviceid=AlarmTimeSet.attributeValue("ServiceID");
			for(Iterator<Element> it=AlarmTimeSet.elementIterator();it.hasNext();){
				Element TaskType = it.next();
				String name=TaskType.getName();
				String type=TaskType.attributeValue("Type");
				String AlarmEndTime=TaskType.attributeValue("AlarmEndTime");
				AlarmTime at=new AlarmTime();
				at.setFreq(Integer.parseInt(freq));
				at.setServiceID(Integer.parseInt(serviceid));
				at.setTaskType(name);
				at.setType(Integer.parseInt(type));
				at.setAlarmEndTime(AlarmEndTime);
				if(name.equals("MonthTime")){
					String month=TaskType.attributeValue("Month");
					String day=TaskType.attributeValue("Day");
					String starttime=TaskType.attributeValue("StartTime");
					String endtime=TaskType.attributeValue("EndTime");
					at.setMonth(month);
					at.setDay(Integer.parseInt(day));
					at.setStartTime(starttime);
					at.setEndTime(endtime);
					list.add(at);
				}
				if(name.equals("WeeklyTime")){
					String dayofweek=TaskType.attributeValue("DayofWeek");
					String starttime=TaskType.attributeValue("StartTime");
					String endtime=TaskType.attributeValue("EndTime");
					at.setMonth("''");
					at.setDayofWeek(Integer.parseInt(dayofweek));
					at.setStartTime(starttime);
					at.setEndTime(endtime);
					list.add(at);
				}
				if(name.equals("DayTime")){
					String startdatetime=TaskType.attributeValue("StartDateTime");
					String enddatetime=TaskType.attributeValue("EndDateTime");
					at.setMonth("''");
					at.setStartDateTime(startdatetime);
					at.setEndDateTime(enddatetime);
					list.add(at);
				}
			}
		}
		//保存运行图节目信息 
		dao.save(list);
		
		//更新内存中运行图节目信息
		AlarmTimeMemory.alarmTimeList=dao.list();
	}
	
	//运行图解析xml得到通道号的数组
	@SuppressWarnings("unchecked")
	public List<AlarmTimeSetVO> getIndexByDownXml(Document document){
		List<AlarmTimeSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
			
			Node node1 =element.selectSingleNode("@Freq");
			if(node1==null)
				continue;
			Node node2 =element.selectSingleNode("@ServiceID");
			if(node2==null)
				continue;
			
			AlarmTimeSetVO vo = new AlarmTimeSetVO();
			vo.setIndex(Integer.parseInt(node.getText()));
			vo.setFreq(Integer.parseInt(node1.getText()));
			vo.setServiceID(Integer.parseInt(node2.getText()));
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	// 运行图回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, int value) {
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"AlarmTimeSet\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"AlarmTimeSet\" Value=\"1\" Desc=\"失败\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;
	}
 
}
