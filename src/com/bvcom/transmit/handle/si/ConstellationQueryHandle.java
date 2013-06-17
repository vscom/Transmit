package com.bvcom.transmit.handle.si;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.parse.si.ChannelScanQueryParse;
import com.bvcom.transmit.parse.si.ConstellationQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.SysInfoVO;
import com.bvcom.transmit.vo.si.ChannelScanQueryVO;
import com.bvcom.transmit.vo.si.ConstellationQueryVO;

/**
 * 星座图  广州监测 Add By Bian Jiang 2010.10.12
 * @author Bian Jiang
 *
 */
public class ConstellationQueryHandle {
	
    private static Logger log = Logger.getLogger(ConstellationQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    MemCoreData coreData = MemCoreData.getInstance();
    
	private static int[] tempIdata ={
		-103,-121,97,110,98,-125,-117,114,-111,-106,124,-115,
		-128,-102,115,-128,-108,87,108,115,120,-117,-119,-123,
		-94,-127,114,-110,114,-123,127,85,103,-127,-121,-115,
		-126,-91,-100,100,125,-101,-92,-120,108,127,124,118,
		-115,76,127,-122,102,-110,111,125,-108,-123,127,114,
		-115,-92,-127,-124
	};
	private static int[] tempQdata = {
		119,103,112,123,-100,-124,111,-116,-127,116,112,-102,
		-128,114,-101,119,-100,123,-115,-79,122,-121,117,-123,
		120,-125,-114,102,-125,-121,114,119,-118,95,97,-90,
		-105,-126,-120,-128,108,81,-125,112,116,-128,122,124,
		-126,103,-114,113,-108,-128,115,-116,123,-100,-89,121,
		-124,122,-112,113
	};
	
    public ConstellationQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    
    public void downXML() {
		List SMGSendList = new ArrayList();

		// 3:GetIndexSet(性能指标和星座图)
		CommonUtility.checkSMGChannelType("GetIndexSet", SMGSendList);
		UtilXML xmlUtil = new UtilXML();

		String returnStr = "";
		
        SysInfoVO sysVO = coreData.getSysVO();

        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("星座图StringToXML Error: " + e.getMessage());
        }
        
        ConstellationQueryParse ConstellationQueryParse = new ConstellationQueryParse();

        ConstellationQueryVO vo = ConstellationQueryParse.getDownObject(document);
        
        String smgDownString = ConstellationQueryParse.createDownXML(vo, bsData);
        
        List voList = new ArrayList();
        
		for (int i = 0; i < SMGSendList.size(); i++) {
			SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
			try {
				// 频道扫描信息下发 timeout 1000*60*3 三分钟
				returnStr = utilXML.SendDownXML(smgDownString, smg
						.getURL(), CommonUtility.CONN_WAIT_TIMEOUT,
						bsData);
				if(returnStr == null || returnStr.equals("")) {
					log.error("取得星座图失败: " + smg.getURL());
					continue;
				}
				
				break;
			} catch (CommonException e) {
				log.error("向SMG下发星座图出错信息：" + e.getMessage());
				log.error("向SMG下发星座图出错URL：" + smg.getURL());
			}
		}
		
		if (returnStr != null && !returnStr.equals("")) {
	        try {
	            document = utilXML.StringToXML(returnStr);
	        } catch (CommonException e) {
	            log.error("星座图StringToXML Error: " + e.getMessage());
	        }
	        
			voList = ConstellationQueryParse.getReturnObject(document);
			
			voList = getIQValues(voList);
			
			returnStr = ConstellationQueryParse.createReturnXML(voList, bsData, 0);
			
		} else {
			returnStr = ConstellationQueryParse.createReturnXML(voList, bsData, 1);
		}
		
    	if(returnStr == null || returnStr.equals("")) {
    		log.error("取得星座图失败, 请过几分钟再试。");
    		returnStr = ConstellationQueryParse.createReturnXML(voList, bsData, 1);
    	}
        	
		try {
			utilXML.SendUpXML(returnStr, bsData);
		} catch (CommonException e) {
			log.error("上发频道扫描信息失败: " + e.getMessage());
		}

		bsData = null;
		downString = null;
		SMGSendList = null;
		utilXML = null;
		
    }
    
    private List<ConstellationQueryVO> getIQValues(List<ConstellationQueryVO> voList) {
		int flag = 64;	
		int mer = 0;
		
		for(int i=0; i<voList.size(); i++) {
			ConstellationQueryVO vo = voList.get(i);
			mer = vo.getMER();
			break;
		}
		
		if(mer >= 36)
		{
			for(int i=0;i<voList.size();i++){
				ConstellationQueryVO vo = voList.get(i);
				double temp = 0;
				double strIDate = 0;
				double strQDate = 0;
				
				//I值
				//if(cmdInfo.getIData()[i].charAt(0) != '-')
				if(vo.getValueI() > 0)
					temp = vo.getValueI() - flag - ((i%4)*16);
				else
					temp = vo.getValueI() + flag + ((i%4)*16);						
				
				if((temp >= 0 && temp < 8) || (temp > 10 && temp < 18))
				{
					temp = 9;
				}
				else if((temp >= 18 && temp < 26) || (temp > 28 && temp < 36))
				{
					temp = 27;
				}
				else if((temp >= 36 && temp < 44) || (temp > 46 && temp < 54))
				{
					temp = 45;
				}
				else if((temp >= 54 && temp < 62) || (temp > 64 && temp <= 72))
				{
					temp = 63;
				}
				else if((temp > -8 && temp < 0) || (temp > -18 && temp < -10))
				{
					temp = -9;
				}
				else if((temp > -26  && temp <= -18) || (temp > -36 && temp < -28))
				{
					temp = -27;
				}
				else if((temp > -44 && temp <= -36) || (temp > -54 && temp < -46))
				{
					temp = -45;
				}
				else if((temp >= -62 && temp <= -54) || (temp >= -72 && temp < -64))
				{
					temp = -63;
				}
				strIDate = temp;
				
				//Q值
				//if(cmdInfo.getQData()[i].charAt(0) != '-')
				if(vo.getValueQ() > 0)
					temp = vo.getValueQ() - flag - (((i/4)-(i/16)*4 )*16);
				else
					temp = vo.getValueQ() + flag +(((i/4)-(i/16)*4 )*16);
				if((temp >= 0 && temp < 8) || (temp > 10 && temp < 18))
				{
					temp = 9;
				}
				else if((temp >= 18 && temp < 26) || (temp > 28 && temp < 36))
				{
					temp = 27;
				}
				else if((temp >= 36 && temp < 44) || (temp > 46 && temp < 54))
				{
					temp = 45;
				}
				else if((temp >= 54 && temp < 62) || (temp > 64 && temp <= 72))
				{
					temp = 63;
				}
				else if((temp > -8 && temp < 0) || (temp > -18 && temp < -10))
				{
					temp = -9;
				}
				else if((temp > -26  && temp <= -18) || (temp > -36 && temp < -28))
				{
					temp = -27;
				}
				else if((temp > -44 && temp <= -36) || (temp > -54 && temp < -46))
				{
					temp = -45;
				}
				else if((temp >= -62 && temp <= -54) || (temp >= -72 && temp < -64))
				{
					temp = -63;
				}
				strQDate =temp;
				
				vo.setValueI(strIDate);
				vo.setValueQ(strQDate);
				
			}
		}
		else if(mer > 20 && mer < 36)
		{
			for(int i=0;i<voList.size();i++){
				ConstellationQueryVO vo = voList.get(i);
				double temp = 0;
				double strIDate = 0;
				double strQDate = 0;
				
				//I值
				//if(cmdInfo.getIData()[i].charAt(0) != '-')
				if(vo.getValueI() > 0)
					temp = vo.getValueI() - flag - ((i%4)*16);
				else
					temp = vo.getValueI() + flag + ((i%4)*16);					
				
				if((temp >= 0 && temp < 5) || (temp >= 18 && temp < 23) || (temp >= 36 && temp < 41) || (temp >= 54 && temp < 59))
				{
					temp = temp + 5;
				}
				else if((temp > 13 && temp <= 18) || (temp > 31 && temp <= 36) || (temp > 49 && temp <= 54) || (temp > 67 && temp <= 72))
				{
					temp = temp - 5;
				}
				else if((temp < 0 && temp > -5) || (temp <= -18 && temp > -23) || (temp <= -36 && temp > -41) || (temp <= -54 && temp > -59))
				{
					temp = temp - 5;
				}
				else if((temp < -13 && temp > -18) || (temp < -31 && temp > -36) || (temp < -49 && temp > -54) || (temp < -67 && temp >= -72))
				{
					temp = temp + 5;
				}
				
				strIDate = temp;
				
				//Q值
				//if(cmdInfo.getQData()[i].charAt(0) != '-')
				if(vo.getValueQ() > 0)
					temp = vo.getValueQ() - flag - (((i/4)-(i/16)*4 )*16);
				else
					temp = vo.getValueQ() + flag +(((i/4)-(i/16)*4 )*16);
				
				if((temp >= 0 && temp < 5) || (temp >= 18 && temp < 23) || (temp >= 36 && temp < 41) || (temp >= 54 && temp < 59))
				{
					temp = temp + 5;
				}
				else if((temp > 13 && temp <= 18) || (temp > 31 && temp <= 36) || (temp > 49 && temp <= 54) || (temp > 67 && temp <= 72))
				{
					temp = temp - 5;
				}
				else if((temp < 0 && temp > -5) || (temp <= -18 && temp > -23) || (temp <= -36 && temp > -41) || (temp <= -54 && temp > -59))
				{
					temp = temp - 5;
				}
				else if((temp < -13 && temp > -18) || (temp < -31 && temp > -36) || (temp < -49 && temp > -54) || (temp < -67 && temp >= -72))
				{
					temp = temp + 5;
				}
				strQDate = temp;
				
				vo.setValueI(strIDate);
				vo.setValueQ(strQDate);
//				loger.info("********iData["+i+"] = "+iData[i]+"***********\n");
//				loger.info("********qData["+i+"] = "+qData[i]+"***********\n");

			}
		}
		else if(mer <= 20)
		{
			for(int i=0;i<voList.size();i++){
				ConstellationQueryVO vo = voList.get(i);
				double temp = 0;
				double strIDate = 0;
				double strQDate = 0;
				
													
				//if(cmdInfo.getIData()[i].charAt(0) != '-')
				if(vo.getValueI() > 0)
					temp = vo.getValueI() - flag - ((i%4)*16);
				else
					temp = vo.getValueI() + flag - ((i%4)*16);
				
				if(temp >= 128)
					temp = temp - 128;
				else if(temp <= -128)
					temp = temp + 128;
				else if(temp > 36 && temp < 128) 
					temp = temp - 72;
				else if(temp < -36 && temp > -128)
					temp = temp + 72;
				else if(temp > 27 && temp <= 36)
					temp = temp - 36;
				else if(temp >= -36 && temp < -27)
					temp = temp + 36;
				else if(temp == 0)
				{
					if(tempIdata[i] > 0)
						temp = tempIdata[i] - 100;
					else 
						temp = tempIdata[i] + 100;
				}									
				strIDate = temp;
				
				//if(cmdInfo.getQData()[i].charAt(0) != '-')
				if(vo.getValueQ() > 0)
					temp = vo.getValueQ() - flag - (((i/4)-(i/16)*4 )*16);
				else
					temp = vo.getValueQ() + flag - (((i/4)-(i/16)*4 )*16);
				
				if(temp >= 128)
					temp = temp - 128;
				else if(temp <= -128)
					temp = temp + 128;
				else if(temp > 36 && temp < 128) 
					temp = temp - 72;
				else if(temp < -36 && temp > -128)
					temp = temp + 72;
				else if(temp > 27 && temp <= 36)
					temp = temp - 36;
				else if(temp >= -36 && temp < -27)
					temp = temp + 36;
				else if(temp == 0)
				{
					if(tempQdata[i] > 0)
						temp = tempQdata[i] - 100;
					else 
						temp = tempQdata[i] + 100;
				}	
				
				strQDate = temp;
				
				vo.setValueI(strIDate);
				vo.setValueQ(strQDate);
				//loger.info("********iData["+i+"] = "+iData[i]+"***********\n");
				//loger.info("********qData["+i+"] = "+qData[i]+"***********\n");

			}								
		}
		return voList;
    }
}
