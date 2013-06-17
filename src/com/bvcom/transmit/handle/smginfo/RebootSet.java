package com.bvcom.transmit.handle.smginfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.handle.video.MonitorProgramQueryHandle;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.DaoException;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;
import com.bvcom.transmit.vo.video.MonitorProgramQueryVO;

public class RebootSet {
	
    private static Logger log = Logger.getLogger(RebootSet.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
    public RebootSet(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    /**
     * 1. ����ǰ������Э��
     * 2. ���͸���Ӧ�������豸�����
     */
    public void downXML() {
    	boolean isErr=true;
    	Document document=null;
		try {
			document = utilXML.StringToXML(downString);
		} catch (CommonException e) {
			log.info("�ַ���ת��xml����"+e.getMessage());
			isErr=false;
		}
		
		String returnstr="";
    	if(isErr){
    		returnstr = getReturnXML(this.bsData, 0);
    	}else{
    		returnstr = getReturnXML(this.bsData, 1);
    	}
        try {
            utilXML.SendUpXML(returnstr, bsData);
        } catch (CommonException e) {
            log.error("�Ϸ� "+ bsData.getStatusQueryType() +" ��Ϣʧ��: " + e.getMessage());
        }
        
    	List<Integer> typeList=parse(document);
    	try {
	    	for(int i=0;i<typeList.size();i++){
	    		//������TSGRAB������������
	    		if(typeList.get(i)==1){
	    			//���͵�TSGrab
	    			String url=coreData.getSysVO().getTSGrabURL().trim();
	    			//isErr=utilXML.SendUpXML(downString, url);
	    		}else if(typeList.get(i)==2){
	    			//���͵�TSC
	    			List<TSCInfoVO> tscs=(List<TSCInfoVO>)coreData.getTSCList();
	    			for(int j=0;j<tscs.size();j++){
	    				TSCInfoVO tsc=tscs.get(j);
	    				isErr=utilXML.SendUpXML(downString, tsc.getURL());
	    			}
	    			//���͵�Rtvm
	    			MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
	    	        // 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
	    	        try {
	    				rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 1);
	    				if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	    					rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 2);
	    					if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	        					rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 3);
	        					if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	        						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 4);
	        						if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	            						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 5);
	            						if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	                						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 0);
	            						}
	        						}	
	        					}
	    					}	
	    				}
	    			} catch (DaoException e1) {
	    				log.error("ȡ��RTVM��URL����: " + e1.getMessage());
	    				isErr=false;
	    			}
	    			isErr=utilXML.SendUpXML(downString, rtvsVO.getRTVSResetURL());
	    			
	    			//�����໭
	    			List<IPMInfoVO> ipms=(List<IPMInfoVO>)coreData.getIPMList();
	    			for(int j=0;j<ipms.size();j++){
	    				isErr=utilXML.SendUpXML(downString, ipms.get(j).getURL());
	    			}
	    			
	    		}else if(typeList.get(i)==3){
	    			//���͵�SMSҵ�����ͽӿ��ϡ�
	    			
	    		}else if(typeList.get(i)==4){
	    			//���͵�CASҵ�����ͽӿ��ϡ�
	    			
	    		}else if(typeList.get(i)==5){
	    			//���˰忨����
	    			//�ݲ�����TSGRAB��������
	    			String url=coreData.getSysVO().getTSGrabURL().trim();
	    			//isErr=utilXML.SendUpXML(downString, url);
	    			
	    			
	    			List<TSCInfoVO> tscs=(List<TSCInfoVO>)coreData.getTSCList();
	    			for(int j=0;j<tscs.size();j++){
	    				TSCInfoVO tsc=tscs.get(j);
	    				isErr=utilXML.SendUpXML(downString, tsc.getURL());
	    			}
	    			
	    			//���͵�Rtvm
	    			MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
	    	        // 0:���� 1:һ��һ���� 2:�ֲ����ʹ�� 3:�ֶ�ѡ̨ 4:�Զ��ֲ� 5:�໭��ϳ�(������)
	    	        try {
	    				rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 1);
	    				if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	    					rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 2);
	    					if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	        					rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 3);
	        					if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	        						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 4);
	        						if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	            						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 5);
	            						if(rtvsVO.getRTVSResetURL()==null||rtvsVO.getRTVSResetURL().equals("")){
	                						rtvsVO = MonitorProgramQueryHandle.GetChangeProgramInfo(rtvsVO, 0);
	            						}
	        						}	
	        					}
	    					}	
	    				}
	    			} catch (DaoException e1) {
	    				log.error("ȡ��RTVM��URL����: " + e1.getMessage());
	    				isErr=false;
	    			}
	    			isErr=utilXML.SendUpXML(downString, rtvsVO.getRTVSResetURL());
	    			//�����໭
	    			List<IPMInfoVO> ipms=(List<IPMInfoVO>)coreData.getIPMList();
	    			for(int j=0;j<ipms.size();j++){
	    				isErr=utilXML.SendUpXML(downString, ipms.get(j).getURL());
	    			}
	    			
	    		}else if(typeList.get(i)==6){
	    			//����Ӳ���豸
	    			List<SMGCardInfoVO> smgs=(List<SMGCardInfoVO>)coreData.getSMGCardList();
	    			for(int j=0;j<smgs.size();j++){
	    				String url=smgs.get(j).getURL().trim();
	    				if(url.indexOf("Setup1")!=-1){
	    					isErr=utilXML.SendUpXML(downString, url);
	    				}
	    			}
	    		}
	    	}
    	} catch (Exception e) {
			//����ʧ��
    		log.info("ǰ����������"+e.getMessage());
    		isErr=false;
		}
//    	String returnstr="";
//    	if(isErr){
//    		returnstr = getReturnXML(this.bsData, 0);
//    	}else{
//    		returnstr = getReturnXML(this.bsData, 1);
//    	}
//        try {
//            utilXML.SendUpXML(returnstr, bsData);
//        } catch (CommonException e) {
//            log.error("�Ϸ� "+ bsData.getStatusQueryType() +" ��Ϣʧ��: " + e.getMessage());
//        }
    	
    }
    private List<Integer> parse(Document document){
    	List<Integer> list=new ArrayList<Integer>();
    	Element root=document.getRootElement();
    	for (Iterator<Element> iter=root.elementIterator(); iter.hasNext(); ) {
			Element RebootSet =iter.next();
			String Type=RebootSet.attributeValue("Type");
			list.add(Integer.parseInt(Type.trim()));
		}
    	return list;
    }
    
    private String getReturnXML(MSGHeadVO head, int value) {
        
        StringBuffer strBuf = new StringBuffer();
//    	<ErrReport>
//    	< RebootSetRecord Comment="�ڲ�����"/>
//    	</ErrReport>
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"�ɹ�\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"ʧ��\"/>\r\n");
            strBuf.append("<ErrReport>\r\n");
            strBuf.append("<RebootSetRecord Comment=\"�ڲ�����\"/>\r\n");
            strBuf.append("</ErrReport>\r\n");
        }
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
}
