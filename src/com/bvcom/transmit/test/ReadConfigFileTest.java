package com.bvcom.transmit.test;

import java.util.List;

import com.bvcom.transmit.config.ReadConfigFile;
import com.bvcom.transmit.core.MemCoreData;
import com.bvcom.transmit.vo.IPMInfoVO;
import com.bvcom.transmit.vo.SMGCardInfoVO;
import com.bvcom.transmit.vo.TSCInfoVO;

public class ReadConfigFileTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        ReadConfigFile configFile = new ReadConfigFile();
        
        configFile.initConfig();
        
        MemCoreData coreData = MemCoreData.getInstance();
        
        List SMGCardList = coreData.getSMGCardList();
        
        List IPMList = coreData.getIPMList();
        
        List TSCList = coreData.getTSCList();
        
        for (int i=0; i< SMGCardList.size(); i++) {
            SMGCardInfoVO smg = (SMGCardInfoVO) SMGCardList.get(i);
            smg.printSMGInfoList();
        }
        for (int i=0; i< IPMList.size(); i++) {
            IPMInfoVO ipm = (IPMInfoVO) IPMList.get(i);
            ipm.printIPMInfoList();
        }
        for (int i=0; i< TSCList.size(); i++) {
            TSCInfoVO tsc = (TSCInfoVO) TSCList.get(i);
            tsc.printTSCInfoList();
        }
        
    }

}
