package com.bvcom.transmit.comparator;

import java.util.Comparator;

import com.bvcom.transmit.vo.si.ChannelScanQueryVO;

/**
 * 频点扫描列表按照频点排序
 */
public class ChannelScanQueryComparator implements Comparator{
	
	/**
	 * 频点扫描列表按照频点排序
	 */
	public int compare(Object o1,Object o2) {
		ChannelScanQueryVO p1=(ChannelScanQueryVO)o1;
		ChannelScanQueryVO p2=(ChannelScanQueryVO)o2; 
		if(p1.getFreq() >= p2.getFreq())
		  return 1;
		else
		  return 0;
		}
}
