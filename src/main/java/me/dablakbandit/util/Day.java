/*
 * Copyright (c) 2019 Ashley Thew
 */

package me.dablakbandit.util;

public enum Day{
	MON("Monday"), TUE("Tuesday"), WED("Wednesday"), THU("Thursday"), FRI("Friday");
	
	String name;
	
	Day(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
