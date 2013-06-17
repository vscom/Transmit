package com.bvcom.transmit.handle;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.bvcom.transmit.parse.MatrixQueryParse;
import com.bvcom.transmit.util.CommonException;
import com.bvcom.transmit.util.CommonUtility;
import com.bvcom.transmit.util.UtilXML;
import com.bvcom.transmit.vo.MSGHeadVO;
import com.bvcom.transmit.vo.MatrixQueryVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;

public class MatrixQueryHandle {
	private static Logger log = Logger.getLogger(MatrixQueryHandle.class.getSimpleName());
    
    private MSGHeadVO bsData = new MSGHeadVO();
    
    private String downString = new String();
    
    private UtilXML utilXML = new UtilXML();
    
    public MatrixQueryHandle(String centerDownStr, MSGHeadVO bsData) {
        this.downString = centerDownStr;
        this.bsData = bsData;
    }
    
    /**
     * 矩阵切换
     * 目前直接返回成功
     */
	@SuppressWarnings("unchecked")
	public void downXML(){
		
		// 返回数据
		@SuppressWarnings("unused")
		String upString = "";
		List SMGSendList = new ArrayList();
        List SMGSendList2 = new ArrayList();
        
        Document document = null;
        try {
            document = utilXML.StringToXML(this.downString);
        } catch (CommonException e) {
            log.error("矩阵切换StringToXML Error: " + e.getMessage());
        };
        MatrixQueryParse matrixQuery = new MatrixQueryParse();
        List<MatrixQueryVO> matrixlist = matrixQuery.getIndexByDownXml(document);
        
        for(int i=0;i<matrixlist.size();i++)
        {
        	CommonUtility.checkSMGChannelIndex(matrixlist.get(i).getIndex1(), SMGSendList);
        	for(int j=0;j<SMGSendList.size();j++){
        		SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList.get(i);
        		try {
                    // 矩阵切换下发 timeout 1000*30 三十秒
                    utilXML.SendDownNoneReturn(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                    //break;
                } catch (CommonException e) {
                    log.error("下发矩阵切换到SMG出错：" + smg.getURL());
                }
        	}
        	CommonUtility.checkSMGChannelIndex(matrixlist.get(i).getIndex2(), SMGSendList2);
        	for(int j=0;j<SMGSendList2.size();j++){
        		SMGCardInfoVO smg = (SMGCardInfoVO) SMGSendList2.get(i);
        		try {
                    // 矩阵切换下发 timeout 1000*30 三十秒
                    utilXML.SendDownNoneReturn(this.downString, smg.getURL(), CommonUtility.CONN_WAIT_TIMEOUT, bsData);
                    //break;
                } catch (CommonException e) {
                    log.error("下发矩阵切换到SMG出错：" + smg.getURL());
                }
        	}
        }
        
      //上报回复的xml给中心,自己返回成功
        upString = matrixQuery.ReturnXMLByURL(this.bsData,0);
        
        try {
            utilXML.SendUpXML(upString, bsData);
        } catch (CommonException e) {
            log.error("矩阵切换回复失败: " + e.getMessage());
        }
        
        bsData = null;
        downString = null;
        SMGSendList = null;
        SMGSendList2 = null;
        utilXML = null;
		
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
