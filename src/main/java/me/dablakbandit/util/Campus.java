/*
 * Copyright (c) 2019 Ashley Thew
 */

package me.dablakbandit.util;

public enum Campus{
	City("W"), North("A"), South("M"), Any("");
	
	protected String code;
	
	Campus(String code){
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}
	
	public String toString(){
		return name();
	}
	
	public static Campus getByRoom(String room){
		String check = room.substring(0, 1);
		for(Campus c : values()){
			if(c.code.equals(check)){ return c; }
		}
		return null;
	}
}
