package com.bvcom.transmit.parse.rec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.bvcom.transmit.handle.video.NVRVideoHistoryDownInquiryHandle;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

//任务录像设置
public class ProvisionalRecordTaskSetParse {
	
	private static Logger log = Logger.getLogger(ProvisionalRecordTaskSetParse.class.getSimpleName());
	
	//任务录像设置解析xml得到通道号的数组,该list对象只含有Index, TaskID, Action 属性
	@SuppressWarnings("unchecked")
	public List<ProvisionalRecordTaskSetVO> getIndexByDownXml(Document document){
		List<ProvisionalRecordTaskSetVO> indexlist = new ArrayList();
		
		Element root = document.getRootElement();
		Element StreamRoundInfoQuery = root.element("ProvisionalRecordTaskSet");
		for(Iterator<Element> iter=StreamRoundInfoQuery.elementIterator();iter.hasNext();){
			Element element = iter.next();
			int index = 0;
			Node node0 = null;
			try {
				node0 =element.selectSingleNode("@Index");
				index = Integer.parseInt(node0.getText());
			} catch (Exception ex) {
				index = 0;
			}
			Node node1 =element.selectSingleNode("@TaskID");
			if(node1==null)
				continue;
			String taskID = node1.getText();
			
			Node node2 =element.selectSingleNode("@Action");
			if(node2==null)
				continue;
			String action = node2.getText();

			
			try {
				for(Iterator<Element> timeIter=element.elementIterator();timeIter.hasNext();){
					Element timeEle = timeIter.next();
					ProvisionalRecordTaskSetVO vo =new ProvisionalRecordTaskSetVO();
					vo.setAction(action);
					vo.setIndex(index);
					vo.setTaskID(taskID);
					node0 =timeEle.selectSingleNode("@Freq");
					vo.setFreq(Integer.parseInt(node0.getText()));
					
					node0 =timeEle.selectSingleNode("@ServiceID");
					vo.setServiceID(Integer.parseInt(node0.getText()));
					
					node0 =timeEle.selectSingleNode("@VideoPID");
					vo.setVideoPID(Integer.parseInt(node0.getText()));
				
					node0 =timeEle.selectSingleNode("@AudioPID");
					vo.setAudioPID(Integer.parseInt(node0.getText()));
					
					node0 =timeEle.selectSingleNode("@ExpireDays");
					vo.setExpireDays(Integer.parseInt(node0.getText()));
					
					try {
						node0 =timeEle.selectSingleNode("@StartDateTime");
						vo.setStartDateTime(node0.getText());
					} catch (Exception ex) {
						vo.setStartDateTime(null);
					}
					
					try {
						node0 =timeEle.selectSingleNode("@EndDateTime");
						vo.setEndDateTime(node0.getText());
					} catch (Exception ex) {
						vo.setEndDateTime("");
					}
					
	                try {
	                	node0 = timeEle.selectSingleNode("@SymbolRate");
	                	int symbolRate = Integer.parseInt(node0.getText());
	                	if(symbolRate == 0) {
	                		vo.setSymbolRate(6875);
	                	} else {
	                		vo.setSymbolRate(symbolRate);
	                	}
	                	
	                } catch (Exception ex) {
	                	vo.setSymbolRate(6875);
	                }
					try {
						node0 = timeEle.selectSingleNode("@QAM");
						int qam = 0;
						try {
							qam = Integer.parseInt(node0.getText());
						} catch (Exception ex) {
							vo.setQAM(node0.getText().substring(3));
						}
						
	                } catch (Exception ex) {
	                	vo.setQAM("64");
	                }
					
					indexlist.add(vo);
				}
			} catch (Exception ex) {
				log.error("解析任务录像失败: " + ex.getMessage());
			}
			
			
		}
		return indexlist;
	}
	
	// 任务录像设置回复xml打包
	public String ReturnXMLByURL(MSGHeadVO head,int value) {

		String xml = null;
		xml = "<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>";
		xml += "<Msg Version=\"" + head.getVersion() + "\" MsgID=\""
		+ head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\""
		+ CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode()
		+ "\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\">";
		if(0==value){
			xml += "<Return Type=\"ProvisionalRecordTaskSet\" Value=\"0\" Desc=\"成功\"/>";
		}else if(1==value){
			xml += "<Return Type=\"ProvisionalRecordTaskSet\" Value=\"1\" Desc=\"失败 - 没有可用资源\"/>";
			xml +=	"</Msg>";
			return xml;
		}
		xml += "</Msg>";
		return xml;

	}
}
