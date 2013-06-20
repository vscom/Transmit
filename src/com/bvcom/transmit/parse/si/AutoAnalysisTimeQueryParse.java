package com.bvcom.transmit.parse.si;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.util.XMLExt;
import com.bvcom.transmit.vo.si.AutoAnalysisTimeQueryVO;

/**
 * 数据业务时间分析
 * @author Bian Jiang
 * @date 2010.12.30
 *
 */
public class AutoAnalysisTimeQueryParse {
	
	public AutoAnalysisTimeQueryVO getDownObject(Document document){
		
		AutoAnalysisTimeQueryVO vo = new AutoAnalysisTimeQueryVO();
		
        List AutoAnalysisTimeQueryList = XMLExt.getMultiElement("/Msg/AutoAnalysisTimeQuery/AutoAnalysisTime", document);
        
        for(int i=0; i<AutoAnalysisTimeQueryList.size(); i++) {
        	
            try {
            	vo.setStartTime(XMLExt.getElementValue((Element)AutoAnalysisTimeQueryList.get(i), "StartTime"));	
            } catch (Exception ex) {
            }
			
			try {
				vo.setType(XMLExt.getElementValue((Element)AutoAnalysisTimeQueryList.get(i), "Type"));
			} catch (Exception ex) {
				vo.setType("ALL");
			}
			break;
        }
		return vo;
	}
}
