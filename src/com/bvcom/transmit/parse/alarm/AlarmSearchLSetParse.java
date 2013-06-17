package com.bvcom.transmit.parse.alarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.alarm.AlarmSearchLSetVO;

//循切报警查询
public class AlarmSearchLSetParse {
	
	//循切报警查询解析xml得到通道号的数组
	@SuppressWarnings("unchecked")
	public List<AlarmSearchLSetVO> getIndexByDownXml(Document document){
		List<AlarmSearchLSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
			
			AlarmSearchLSetVO vo = new AlarmSearchLSetVO();
			
			vo.setIndex(Integer.parseInt(node.getText()));
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	// 循切报警查询回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head,List<AlarmSearchLSetVO> list, int value) {

		AlarmSearchLSetVO vo = list.get(0);
		
		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
				+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
				+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
				+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"AlarmSearchLSet\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"AlarmSearchLSet\" Value=\"1\" Desc=\"失败\"/>";
			xml +="</Msg>";
			return xml;
		}
		xml +="<ReturnInfo><RoundStream Index=\""+vo.getIndex()+"\" DelayTime=\""+vo.getDelayTime()+"\" AlaType=\""+vo.getAlaType()+"\">";
		for(int i=0;i<list.size();i++){
			xml +="<Channel Freq=\""+list.get(i).getFreq()+"\">";
				for(int j=0;j<list.get(i).getType().size();j++){
					xml +="<AlarmSearchL Type=\""+list.get(i).getType().get(j)+"\" Desc=\""+list.get(i).getDesc().get(j)+"\" Value=\""+list.get(i).getValue().get(j)+"\" Time=\""+list.get(i).getTime().get(j)+"\"/>";
				}
			xml +="</Channel>";
		}
		xml += "</RoundStream></ReturnInfo></Msg>";
		return xml;
	}

}
