package com.bvcom.transmit.comparator;

import java.util.Comparator;

import com.bvcom.transmit.vo.rec.SetAutoRecordChannelVO;

/**
 * �Զ�¼�������б���Ƶ������
 */
public class SetAutoRecordChannelComparator implements Comparator{
	
	/**
	 * �Զ�¼�������б���Ƶ������
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
