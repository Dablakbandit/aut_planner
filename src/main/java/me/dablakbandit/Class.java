package me.dablakbandit;

public class Class{
	
	private String	code, room;
	private int		day, start, end;
	
	public Class(String code, String room, int day, int start, int end){
		this.code = code;
		this.room = room;
		this.day = day;
		this.start = start;
		this.end = end;
		// System.out.println(day + " : " + start + " : " + end);
	}
	
	public int getDay(){
		return day;
	}
	
	public int getStart(){
		return start;
	}
	
	public int getEnd(){
		return end;
	}
	
	public String getCode(){
		return code;
	}
	
	public String getRoom(){
		return room;
	}
	
	public boolean collides(Class check){
		if(day != check.day){ return false; }
		if(check.start == start){ return true; }
		if(check.start > end || end > check.start){ return false; }
		return true;
	}
	
}
