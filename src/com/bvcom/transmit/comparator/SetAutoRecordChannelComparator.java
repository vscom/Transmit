package com.bvcom.transmit.comparator;

import java.util.Comparator;

import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

/**
 * 自动录像设置列表按照频点排序
 */
public class SetAutoRecordChannelComparator implements Comparator{
	
	/**
	 * 自动录像设置列表按照频点排序
	 */
	public int compare(Object o1,Object o2) {
		SetAutoRecordChannelVO p1=(SetAutoRecordChannelVO)o1;
		SetAutoRecordChannelVO p2=(SetAutoRecordChannelVO)o2; 
		if(p1.getFreq() >= p2.getFreq())
		  return 1;
		else
		  return 0;
		}
}
