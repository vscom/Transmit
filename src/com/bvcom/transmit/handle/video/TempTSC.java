package com.bvcom.transmit.handle.video;

import java.util.List;


//封装每个tsc的 通道
public class TempTSC {
private int key;
private List<Integer> value;
public int getKey() {
	return key;
}
public void setKey(int key) {
	this.key = key;
}
public List<Integer> getValue() {
	return value;
}
public void setValue(List<Integer> value) {
	this.value = value;
}

}
