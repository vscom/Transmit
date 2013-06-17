package com.bvcom.transmit.vo.index;

import java.util.List;

import com.bvcom.transmit.vo.MSGHeadVO;
//性能指标查询
public class GetIndexSetVO {
	
	private MSGHeadVO MSGHead = new MSGHeadVO();

	private int Index;
	
	private int Freq;
	
	private List<Integer> Type;
	
	private List<String> Desc;//对应type的描述
	
	private List<Integer> Value;//对应type的value
	
	private int ReturnValue;//成功或失败
	
	private String Comment;//失败原因

	public MSGHeadVO getMSGHead() {
		return MSGHead;
	}

	public void setMSGHead(MSGHeadVO head) {
		MSGHead = head;
	}

	public int getIndex() {
		return Index;
	}

	public void setIndex(int index) {
		Index = index;
	}

	public int getFreq() {
		return Freq;
	}

	public void setFreq(int freq) {
		Freq = freq;
	}

	public List<Integer> getType() {
		return Type;
	}

	public void setType(List<Integer> type) {
		Type = type;
	}

	public List<String> getDesc() {
		return Desc;
	}

	public void setDesc(List<String> desc) {
		Desc = desc;
	}

	public List<Integer> getValue() {
		return Value;
	}

	public void setValue(List<Integer> value) {
		Value = value;
	}

	public int getReturnValue() {
		return ReturnValue;
	}

	public void setReturnValue(int returnValue) {
		ReturnValue = returnValue;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

}
