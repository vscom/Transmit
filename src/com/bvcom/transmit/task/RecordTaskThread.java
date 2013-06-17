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
 * ����¼����, ɾ���Ѿ����ڵ�����¼��
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
     * ����¼����
     */
    @SuppressWarnings("unchecked")
	private void recordTaskProcess() {
    	
    	log.info("����¼���߳̿�ʼ");
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
			       		// �Ѿ���������ɾ����ֻ���±�״̬
							SetAutoRecordChannelVO recordVO = new SetAutoRecordChannelVO();
			       			//FreqList.add(vo.getFreq());
			       			recordVO.setFreq(vo.getFreq());
			       			recordVO.setServiceID(vo.getServiceID());
			       			
			       			//һ��һ��Ŀӳ������¶�Ӧ������¼�ƵĽ�Ŀ״̬
			       			recordVO = setAutoRecordChannelHandle.delRecordTaskIndex(recordVO);
			       			//����¼���statusFlag =0 ��ʶ��Ч���񡢹�������
			       			RecordTaskSetHandle.updateRecordTaskIndex(vo);
			       			
						} catch (DaoException e) {
							log.error("����¼��ɾ������: " + e.getMessage());
						}
					}
				}
				//��һ��һ��Ŀӳ������������¼��
				if(recordTaskList.size() == 0) {
					setAutoRecordChannelHandle.updateRecordTaskIndex();
				}
				
        		try {
					Thread.sleep(CommonUtility.RECORD_TASK_WAIT_TIME);
				} catch (InterruptedException e) {
					
				}
				
			}
			
		} catch (DaoException e) {
			log.error("����¼�������: " + e.getMessage());
			recordTaskProcess();
		}
    	
    }
    
    /**
     * true: �Ѿ���ȥ flase:û�й���
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

			// ����Ϊ��λ
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
