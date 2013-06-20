/**
 * transmit (java转发)
 * 
 * MemCoreData.java    2009.11.12
 * 
 * Copyright 2009 BVCOM. All Rights Reserved.
 * 
 */
package com.bvcom.transmit.core;

import java.util.ArrayList;
import java.util.List;

import com.bvcom.transmit.vo.SysInfoVO;

/**
 * 
 *  系统配置信息
 * 
 * @version  V1.0
 * @author Bian Jiang
 * @Date 2009.11.12
 */
public class MemCoreData {
    
    private static MemCoreData coreData; 
    
    private List SMGCardList = new ArrayList();
    
    private List IPMList = new ArrayList();
    
    private List TSCList = new ArrayList();
    
    private SysInfoVO sysVO = new SysInfoVO();

    /*
     * 取得系统配置信息
     */
    public static MemCoreData getInstance() {
        if (coreData == null)
            coreData = new MemCoreData();
        return coreData;
    }
    
    public List getIPMList() {
        return IPMList;
    }

    public void setIPMList(List list) {
        IPMList = list;
    }

    public List getSMGCardList() {
        return SMGCardList;
    }

    public void setSMGCardList(List cardList) {
        SMGCardList = cardList;
    }

    public List getTSCList() {
        return TSCList;
    }

    public void setTSCList(List list) {
        TSCList = list;
    }

    public SysInfoVO getSysVO() {
        return sysVO;
    }

    public void setSysVO(SysInfoVO sysVO) {
        this.sysVO = sysVO;
    }
    
    
}
