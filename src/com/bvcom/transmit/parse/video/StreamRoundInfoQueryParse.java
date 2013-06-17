package com.bvcom.transmit.parse.video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.video.StreamRoundInfoQueryVO;
//自动轮播
public class StreamRoundInfoQueryParse {
	/**
	 * 新增方法解析xml获取频点集合
	 * @param document解析对象
	 * @return 频点的集合
	 */
	public List<String> getFreqXml(Document document){
		Element root = document.getRootElement();
        Element StreamRoundInfoQuery=root.element("StreamRoundInfoQuery");
        List<String> freqList =new ArrayList<String>();
        try {
	        for(Iterator<Element> iter=StreamRoundInfoQuery.elementIterator();iter.hasNext();){
				Element RoundStream  = iter.next();
				for(Iterator<Element> it=RoundStream .elementIterator();it.hasNext();){
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
	
	//自动轮播解析xml得到通道号的数组
	@SuppressWarnings("unchecked")
	public List<StreamRoundInfoQueryVO> getIndexByDownXml(Document document){
		List<StreamRoundInfoQueryVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element StreamRoundInfoQuery = root.element("StreamRoundInfoQuery");
		for(Iterator<Element> iter=StreamRoundInfoQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			
			Node node =element.selectSingleNode("@Index");
			if(node==null)
				continue;
			
			StreamRoundInfoQueryVO vo = new StreamRoundInfoQueryVO();
			
			vo.setIndex(Integer.parseInt(node.getText()));
			
			indexlist.add(vo);
		}
		return indexlist;
		
	}
	
	// 自动轮播回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head, List<StreamRoundInfoQueryVO> list ,int value) {
		//		
		// String date;
		// java.text.DateFormat format1 = new java.text.SimpleDateFormat(
		// "yyyy-MM-dd hh:mm:ss");
		// date = format1.format(new Date());

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime()+ "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"StreamRoundInfoQuery\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"StreamRoundInfoQuery\" Value=\"1\" Desc=\"失败\"/>";
			xml +=	"</Msg>";
			return xml;
		}
		xml +=" <ReturnInfo><StreamRoundInfoQuery>";
		for(int i=0;i<list.size();i++){
			
			xml +="<RoundStream Index=\""+list.get(i).getIndex()+"\" URL=\""+list.get(i).getReturnURL()+"\" />";
		}
		xml += "</StreamRoundInfoQuery></ReturnInfo></Msg>";
		return xml;

	}

}
