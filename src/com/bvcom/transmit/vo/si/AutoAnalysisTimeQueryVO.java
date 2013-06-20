package com.bvcom.transmit.vo.si;

import com.bvcom.transmit.vo.MSGHeadVO;

/**
 * 数据业务时间分析
 * @author Bian Jiang
 * @date 2010.12.30
 *
 */
public class AutoAnalysisTimeQueryVO {

    private MSGHeadVO MSGHead = new MSGHeadVO();
    
    private String StartTime;
    
    private String Type;

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public String getStartTime() {
		return StartTime;
	}

	public void setStartTime(String startTime) {
		StartTime = startTime;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}
    
}
