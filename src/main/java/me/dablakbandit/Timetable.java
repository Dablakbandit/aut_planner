package me.dablakbandit;

import java.util.ArrayList;
import java.util.List;

public class Timetable{
	
	private List<Class> classes = new ArrayList<Class>();
	
	public Timetable(){
		
	}
	
	public List<Class> getClasses(){
		return classes;
	}
	
	public boolean tryAdd(Stream s){
		for(Class c : classes){
			for(Class c1 : s.getClasses()){
				if(c.collides(c1)){ return false; }
			}
		}
		classes.addAll(s.getClasses());
		return true;
	}
	
	public List<String> getUnique(){
		List<String> list = new ArrayList<String>();
		for(Class c : classes){
			if(!list.contains(c.getCode())){
				list.add(c.getCode());
			}
		}
		return list;
	}
	
	public Timetable clone(){
		Timetable tt = new Timetable();
		tt.classes.addAll(classes);
		return tt;
	}
	
}
