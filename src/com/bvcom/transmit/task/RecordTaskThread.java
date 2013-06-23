package com.bvcom.transmit.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.handle.video.ProvisionalRecordTaskSetHandle;
import com.bvcom.transmit.handle.video.SetAutoRecordChannelHandle;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;
import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

/**
 * 任务录像处理, 删除已经过期的任务录像
 * @author Bian Jiang
 * @data 2010.09.27
 *
 */
public class RecordTaskThread extends Thread {
    
    private static Logger log = Logger.getLogger(RecordTaskThread.class.getSimpleName());
    
    SetAutoRecordChannelHandle setAutoRecordChannelHandle = new SetAutoRecordChannelHandle();
    
    public void run() {
    	recordTaskProcess();
    }
    
    /**
     * 任务录像处理
     */
    @SuppressWarnings("unchecked")
	private void recordTaskProcess() {
    	
    	log.info("任务录像线程开始");
    	ProvisionalRecordTaskSetHandle RecordTaskSetHandle = new ProvisionalRecordTaskSetHandle();
    	
    	try {
    		
			while(true) {
				//List FreqList = new ArrayList();
				
				List recordTaskList = RecordTaskSetHandle.selectRunTaskList();
				for(int i=0; i<recordTaskList.size(); i++) {
					ProvisionalRecordTaskSetVO vo = (ProvisionalRecordTaskSetVO)recordTaskList.get(i);
					boolean isExpireDays = checkRecordTaskExpireDays(vo);
						
					if(isExpireDays) {
			       		try {
			       		// 已经过期任务不删除，只更新表状态
							SetAutoRecordChannelVO recordVO = new SetAutoRecordChannelVO();
			       			//FreqList.add(vo.getFreq());
			       			recordVO.setFreq(vo.getFreq());
			       			recordVO.setServiceID(vo.getServiceID());
			       			
			       			//一对一节目映射表：更新对应的任务录制的节目状态
			       			recordVO = setAutoRecordChannelHandle.delRecordTaskIndex(recordVO);
			       			//任务录像表：statusFlag =0 标识无效任务、过期任务
			       			RecordTaskSetHandle.updateRecordTaskIndex(vo);
			       			
						} catch (DaoException e) {
							log.error("任务录像删除出错: " + e.getMessage());
						}
					}
				}
				//在一对一节目映射表中清除任务录制
				if(recordTaskList.size() == 0) {
					setAutoRecordChannelHandle.updateRecordTaskIndex();
				}
				
        		try {
					Thread.sleep(CommonUtility.RECORD_TASK_WAIT_TIME);
				} catch (InterruptedException e) {
					
				}
				
			}
			
		} catch (DaoException e) {
			log.error("任务录像处理出错: " + e.getMessage());
			try {
				Thread.sleep(CommonUtility.RECORD_TASK_WAIT_TIME);
			} catch (Exception ex) {
			}
			recordTaskProcess();
		}
    }
    
    /**
     * true: 已经过去 flase:没有过期
     * @param vo
     * @return
     */
    private boolean checkRecordTaskExpireDays(ProvisionalRecordTaskSetVO vo) {
    	
    	String startTime = vo.getLasttime();
    	String nowTime = CommonUtility.getDateTime();
    	boolean ret = false;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    	try {
			long startData = dateFormat.parse(startTime).getTime();
			long nowData = dateFormat.parse(nowTime).getTime();

			// 分钟为单位
			long dataTime = (nowData - startData)/(1000*60);

			if (dataTime <= 0 || dataTime > (vo.getExpireDays() * 24 * 60)) {
				ret = true;
			} if(vo.getEndDateTime() != null && !vo.getEndDateTime().equals("") && !vo.getEndDateTime().equals("null") && vo.getExpireDays() <= 1) {
				long endData = dateFormat.parse(vo.getEndDateTime()).getTime();
				if ((nowData - startData)/1000 > 0) {
					ret = true;
				}
			} else {
				ret = false;
			}
		} catch (Exception ex) {
    		log.error("Date Parse Error: " + ex.getMessage());
    	}
		return ret;
    }
    
}
