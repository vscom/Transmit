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

//�������أ���Ŀ��أ�
public class AlarmProgramSwitchSetParse {
	private static Logger log = Logger.getLogger(AlarmProgramSwitchSetParse.class.getSimpleName());
	//��Ŀ�������ؽ���xml�õ�ͨ���ŵ�����
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
	
	//��Ŀ�������ؽ����ظ���xml����
	@SuppressWarnings("unchecked")
	public AlarmProgramSwitchSetVO getReturnByXml(Document document){
		AlarmProgramSwitchSetVO vo = new AlarmProgramSwitchSetVO();
		
		Element root = document.getRootElement();
		Element Return = root.element("Return");
		String type = Return.attribute("Type").getValue();
		int value  = Integer.parseInt(Return.attribute("Value").getValue());
		if(!type.equals("AlarmProgramSwitchSet")){
			vo.setReturnValue(1);
			vo.setComment("��Ŀ��������xml��type����");
			return vo;
		}
		vo.setReturnValue(value);
		return vo;
	}
	
	// ��Ŀ�������ػظ�xml���
	public String ReturnXMLByURL(MSGHeadVO head, int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"AlarmProgramSwitchSet\" Value=\"0\" Desc=\"�ɹ�\"/>";
		}else if(1==value){
			xml += "<Return Type=\"AlarmProgramSwitchSet\" Value=\"1\" Desc=\"ʧ��\"/>";
			xml += "</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;

	}
	/**
	 * ��������Ŀ��������״̬ ��Ϣ���
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
        	log.error("������Ŀ���ش���"+ex);
        }
	    //System.out.println(alarmSwitchList);
	    dao.save(alarmSwitchList);
	    
	}
}
