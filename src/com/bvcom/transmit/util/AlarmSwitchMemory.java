package com.bvcom.transmit.util;

import java.util.ArrayList;
import java.util.List;

import com.bvcom.transmit.parse.alarm.domain.AlarmSwitch;
import com.bvcom.transmit.parse.alarm.domain.AlarmSwitchDao;
/**
 * 
 * @author JI LONG 2011-5-12
 *把报警开关状态放入内存
 */
public class AlarmSwitchMemory {
	public static List<AlarmSwitch> alarmSwitchList=new ArrayList<AlarmSwitch>();
	public static void alarmSwitchToMemory(){
		AlarmSwitchDao asd=new AlarmSwitchDao();
		alarmSwitchList=asd.list();
		//System.out.println("内存中门限开关信息："+alarmSwitchList);
	}
}
