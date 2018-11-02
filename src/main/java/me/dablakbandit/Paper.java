package me.dablakbandit;

public class Paper{
	
	private String code, name, url;
	
	public Paper(String code, String name, String url){
		this.code = code;
		this.name = name;
		this.url = "https://arion.aut.ac.nz/ArionMain/CourseInfo/Information/Qualifications/" + url;
		// System.out.println(code + " : " + name + " : " + this.url);
	}
	
	public String getName(){
		return name;
	}
	
	public String getCode(){
		return code;
	}
	
	public String getUrl(){
		return url;
	}
	
}