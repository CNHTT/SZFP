package com.hiklife.rfidapi;

import java.util.EventObject;

public class InventoryEvent extends EventObject{
	private static final long serialVersionUID = 2L;
	private InventoryTagInfo tagInfo;
	
	public InventoryEvent(Object source , InventoryTagInfo date){
		super(source);
		this.tagInfo = date;
	}
	
	public short[] GetTagEPC(){
		return tagInfo.epc;
	}
	
	public float GetRSSI(){
		return tagInfo.rssi;
	}
	
	public String GetFlagID(){
		return tagInfo.getFlagID();
	}
}
