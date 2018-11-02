package me.dablakbandit;

import java.util.ArrayList;
import java.util.List;

public class Stream{
	
	private String		code;
	private List<Class>	classes	= new ArrayList<Class>();
	
	public Stream(String code){
		this.code = code;
		// System.out.println(code + ":" + code.length());
	}
	
	public String getCode(){
		return code;
	}
	
	public List<Class> getClasses(){
		return classes;
	}
}