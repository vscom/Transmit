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
     * 1. 解析前端重启协议
     * 2. 发送给对应的重启设备或软件
     */
    public void downXML() {
    	boolean isErr=true;
    	Document document=null;
		try {
			document = utilXML.StringToXML(downString);
		} catch (CommonException e) {
			log.info("字符串转换xml错误："+e.getMessage());
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
            log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
        }
        
    	List<Integer> typeList=parse(document);
    	try {
	    	for(int i=0;i<typeList.size();i++){
	    		//不处理TSGRAB重新启动问题
	    		if(typeList.get(i)==1){
	    			//发送到TSGrab
	    			String url=coreData.getSysVO().getTSGrabURL().trim();
	    			//isErr=utilXML.SendUpXML(downString, url);
	    		}else if(typeList.get(i)==2){
	    			//发送到TSC
	    			List<TSCInfoVO> tscs=(List<TSCInfoVO>)coreData.getTSCList();
	    			for(int j=0;j<tscs.size();j++){
	    				TSCInfoVO tsc=tscs.get(j);
	    				isErr=utilXML.SendUpXML(downString, tsc.getURL());
	    			}
	    			//发送到Rtvm
	    			MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
	    	        // 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
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
	    				log.error("取得RTVM的URL错误: " + e1.getMessage());
	    				isErr=false;
	    			}
	    			isErr=utilXML.SendUpXML(downString, rtvsVO.getRTVSResetURL());
	    			
	    			//发给多画
	    			List<IPMInfoVO> ipms=(List<IPMInfoVO>)coreData.getIPMList();
	    			for(int j=0;j<ipms.size();j++){
	    				isErr=utilXML.SendUpXML(downString, ipms.get(j).getURL());
	    			}
	    			
	    		}else if(typeList.get(i)==3){
	    			//发送到SMS业务类型接口上。
	    			
	    		}else if(typeList.get(i)==4){
	    			//发送到CAS业务类型接口上。
	    			
	    		}else if(typeList.get(i)==5){
	    			//除了板卡都发
	    			//暂不处理TSGRAB重新启动
	    			String url=coreData.getSysVO().getTSGrabURL().trim();
	    			//isErr=utilXML.SendUpXML(downString, url);
	    			
	    			
	    			List<TSCInfoVO> tscs=(List<TSCInfoVO>)coreData.getTSCList();
	    			for(int j=0;j<tscs.size();j++){
	    				TSCInfoVO tsc=tscs.get(j);
	    				isErr=utilXML.SendUpXML(downString, tsc.getURL());
	    			}
	    			
	    			//发送到Rtvm
	    			MonitorProgramQueryVO rtvsVO = new MonitorProgramQueryVO();
	    	        // 0:空闲 1:一对一监视 2:轮播监测使用 3:手动选台 4:自动轮播 5:多画面合成(马赛克)
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
	    				log.error("取得RTVM的URL错误: " + e1.getMessage());
	    				isErr=false;
	    			}
	    			isErr=utilXML.SendUpXML(downString, rtvsVO.getRTVSResetURL());
	    			//发给多画
	    			List<IPMInfoVO> ipms=(List<IPMInfoVO>)coreData.getIPMList();
	    			for(int j=0;j<ipms.size();j++){
	    				isErr=utilXML.SendUpXML(downString, ipms.get(j).getURL());
	    			}
	    			
	    		}else if(typeList.get(i)==6){
	    			//所有硬件设备
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
			//返回失败
    		log.info("前端重启错误："+e.getMessage());
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
//            log.error("上发 "+ bsData.getStatusQueryType() +" 信息失败: " + e.getMessage());
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
//    	< RebootSetRecord Comment="内部错误"/>
//    	</ErrReport>
        strBuf.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\"?>");
        strBuf.append("<Msg Version=\"" + head.getVersion() + "\" MsgID=\"");
        strBuf.append(head.getCenterMsgID() + "\" Type=\"MonUp\" DateTime=\"");
        strBuf.append(CommonUtility.getDateTime() + "\" SrcCode=\"" + head.getDstCode());
        strBuf.append("\" DstCode=\"" + head.getSrcCode() + "\" ReplyID=\""+head.getCenterMsgID()+"\"> \r\n");
        if(0==value){
            strBuf.append("<Return Type=\""+ head.getStatusQueryType() + "\" Value=\"0\" Desc=\"成功\"/>\r\n");
        }else if(1==value){
            strBuf.append("<Return Type=\"" + head.getStatusQueryType() + "\" Value=\"1\" Desc=\"失败\"/>\r\n");
            strBuf.append("<ErrReport>\r\n");
            strBuf.append("<RebootSetRecord Comment=\"内部错误\"/>\r\n");
            strBuf.append("</ErrReport>\r\n");
        }
        strBuf.append("</Msg>");
        return strBuf.toString();
    }
}
