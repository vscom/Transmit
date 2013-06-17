package com.bvcom.transmit.handle.video;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.rec.NVRVideoHistoryDownInquiryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.rec.ProvisionalRecordTaskSetVO;

public class NVRVideoHistoryDownInquiryHandle {
	
	private static Logger log = Logger.getLogger(NVRVideoHistoryDownInquiryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public NVRVideoHistoryDownInquiryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }

    /**
     * TSC �ϲ��ļ����ظ�java���ϲ��ȴ�30��󷵻�URL��Ȼ������ϲ�
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		// ��������
		@SuppressWarnings("unused")
		String upString = "";

        
        Document document = null;
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        List TSCSendList = coreData.getTSCList();//tsc���б���Ϣ
        
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("��ʷ��Ƶ�鿴StringToXML Error: " + e.getMessage());
        }
        NVRVideoHistoryDownInquiryParse nvrHistoryd = new NVRVideoHistoryDownInquiryParse();
        List<ProvisionalRecordTaskSetVO> nvrHistoryDlist = nvrHistoryd.getIndexByDownXml(document);
        
        String url = "";
        for(int i=0; i< nvrHistoryDlist.size(); i++) 
        {
        	ProvisionalRecordTaskSetVO vo = nvrHistoryDlist.get(i);

        	int index = 0;
        	
        	// Del By Bian Jiang ��Ŀ��Ϣ���ӽ�Ŀӳ���ȡ�� 2010.9.8
//        	if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_3)) {
//        		try {
//					index = NVRVideoHistoryInquiryHandle.getIndexByProgram(vo);
//					this.downString = this.downString.replaceAll("Index=\"0\"", "Index=\"" + index + "\"");
//				} catch (DaoException e) {
//					
//				}
//        	} else if (this.bsData.getVersion().equals(CommonUtility.XML_VERSION_2_0)) {
//        		// ͨ��ӳ��ȡ�õ�ǰ��Ŀ��Ϣ
//        		try {
//					index = NVRVideoHistoryInquiryHandle.getIndexByProgramForChannelRemap(vo);
//					this.downString = this.downString.replaceAll("Index=\"" + vo.getIndex() + "\"", "Index=\"" + index + "\"");
//				} catch (DaoException e) {
//				}
//        	}
        	
			try {
				vo = NVRVideoHistoryInquiryHandle.getIndexByProgramForChannelRemap(vo);
				index = vo.getIndex();
			} catch (DaoException e1) {

			}
			/**
			 * ���ݼ��������Ŀ, TSC����ҪIndex��
			 * By: Bian Jiang
			 * 2011.4.7
			 */
//			this.downString = this.downString.replaceAll("Index=\"" + vo.getIndex() + "\"", "Index=\"" + index + "\"");
			
        	//CommonUtility.checkTSCChannelIndex(index, TSCSendList);
        	for(int j=0;j<TSCSendList.size();j++)
        	{
        		TSCInfoVO tsc = (TSCInfoVO) TSCSendList.get(j);
    			try {
    				int tscIndex=NVRVideoHistoryInquiryHandle.getTscIndex(vo);
    				if(tscIndex >= tsc.getIndexMin() && tscIndex <= tsc.getIndexMax() ){
    					if(!url.equals(tsc.getURL())) {
    						// ��ʷ��Ƶ�����·� timeout 1000*20 ��ʮ��
    						upString = utilXML.SendDownXML(this.downString, tsc.getURL(), CommonUtility.TASK_WAIT_TIMEOUT, bsData);
    						//break;
    						url = tsc.getURL().trim();
    						if(upString.equals("")) {
    							log.info("������ϢΪ��: " + tsc.getURL());
    							continue;
    						} else {
    							break;
    						}
    					}
    				}
                } catch (CommonException e) {
                    log.error("�·���ʷ��Ƶ���ص�TSC����" + tsc.getURL());
                    upString = "";
                }
        	}
        	
        }
      //�ϱ��ظ���xml������
        try {
        	if(upString == null || upString.equals("")) {
        		upString = utilXML.getReturnXML(bsData, 1);
        	}
//    		// �ȴ�10��
//    		try {
//    			int random = 10000+(int)(Math.random()*5000);
//    			log.info("��ʷ��Ƶ����Sleepʱ�䣺" + (random/1000) + "s");
//				Thread.sleep(random);
//			} catch (InterruptedException e) {
//			}
        	
    		utilXML.SendUpXML(upString, bsData);	
            
        } catch (CommonException e) {
            log.error("��ʷ��Ƶ���ػظ�ʧ��: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        utilXML = null;
        nvrHistoryd = null;
    }

	public MSGHeadVO getBsData() {
		return bsData;
	}

	public void setBsData(MSGHeadVO bsData) {
		this.bsData = bsData;
	}

	public String getDownString() {
		return downString;
	}

	public void setDownString(String downString) {
		this.downString = downString;
	}

	public UtilXML getUtilXML() {
		return utilXML;
	}

	public void setUtilXML(UtilXML utilXML) {
		this.utilXML = utilXML;
	}

}
