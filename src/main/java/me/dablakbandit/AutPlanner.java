package me.dablakbandit;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AutPlanner{
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		new AutPlanner();
	}
	
	private AutPlanner(){
		// Scanner for reading input from user
		Scanner reader = new Scanner(System.in);
		
		// Get codes for our course at a specicified level
		// This should be doable with any course
		List<Paper> papers = getCodes("https://arion.aut.ac.nz/ArionMain/CourseInfo/Information/Qualifications/PaperTable.aspx?id=3765", "Level 5");
		
		// List of all possible streams
		List<List<Stream>> all_streams = new ArrayList<List<Stream>>();
		
		// For each paper
		for(Paper paper : papers){
			// Ask user question
			System.out.println("Are you taking " + paper.getName() + "? (y/n) ");
			// Get input from user
			char input = reader.next().charAt(0);
			// If is taking paper
			if(input == 'y'){
				// Get possible semesters
				Map<String, Element> times = getTimetables(paper.getUrl());
				// Ask user question
				System.out.println("Which semester? ");
				// Counter for question
				int i = 1;
				// For each semester
				for(Map.Entry<String, Element> t : times.entrySet()){
					// Print semester
					System.out.println(i++ + ") " + t.getKey());
				}
				// Get input from user
				int sem = reader.nextInt();
				// Counter for check
				int k = 0;
				for(Map.Entry<String, Element> t : times.entrySet()){
					// Add to counter
					k++;
					// If is the same as user input
					if(k == sem){
						// Get classes for that semester
						List<Stream> streams = getClasses(t.getValue());
						// Add to out list of all possible
						all_streams.add(streams);
					}
				}
			}
		}
		
		// Generate possible timetables
		List<Timetable> timetables = getPossibleTimetables(all_streams);
		
		// Ask user question
		System.out.println("What is the latest time u want to stay at uni? (pm) ");
		
		int input = reader.nextInt();
		
		// Calculate in 24 hour clock
		int latest = input != 12 ? 12 + input : input;
		
		// Ask user question
		System.out.println("City/South/Both? (C/S/B) ");
		
		char loc = reader.next().charAt(0);
		
		Location l = Location.BOTH;
		switch(loc){
		case 'C':
		case 'c':
			l = Location.CITY;
			break;
		case 'S':
		case 's':
			l = Location.SOUTH;
			break;
		}
		
		// Create list for removal
		Set<Timetable> remove = new HashSet<Timetable>();
		
		// For each timetable
		for(Timetable timetable : timetables){
			// For each class in the timetable
			for(Class c : timetable.getClasses()){
				// If ends later than we want
				if(c.getEnd() > latest){
					// Add to remove list
					remove.add(timetable);
				}else{
					// Switch Location
					switch(l){
					case CITY:
						// Not city
						if(!c.getRoom().startsWith("W")){
							// Add to remove list
							remove.add(timetable);
						}
						break;
					case SOUTH:
						// Not south
						if(!c.getRoom().startsWith("M")){
							// Add to remove list
							remove.add(timetable);
						}
						break;
					}
				}
			}
		}
		
		// Remove all unwanted from timetables
		timetables.removeAll(remove);
		
		// Close scanner
		reader.close();
		
		// Print timetables to file
		printToFile(timetables);
		
		// Print amount written to file out of possible
		System.out.println("Printed " + timetables.size() + "/" + getTotal(all_streams) + " possible timetables to file.");
	}
	
	// Method to print timetables to file
	public void printToFile(List<Timetable> timetables){
		// The file we want to write to
		File f = new File(".", "out.csv");
		// Print file name
		System.out.println(f.getAbsolutePath());
		// If file exists
		if(f.exists()){
			// Delete file
			f.delete();
		}
		try{
			// Open printwriter to write to file
			PrintWriter pw = new PrintWriter(f);
			// For each timetable
			for(Timetable w : timetables){
				// Print days seperated
				pw.println(",monday,tuesday,wednesday,thursday,friday");
				// All possible time slots for each day
				String poss[][] = new String[5][24];
				// For each class
				for(Class c : w.getClasses()){
					// For each hour in each class
					for(int hour = c.getStart(); hour < c.getEnd(); hour++){
						// Put code into time slot
						poss[c.getDay() - 1][hour] = c.getCode();
					}
				}
				// Get the lowest hour in timetable
				int min_hour = getLowestHour(w);
				// Get the highest hour in timetable
				int max_hour = getMaxHour(w);
				// For each hour in timetable
				for(int hour = min_hour; hour < max_hour; hour++){
					// Calculate hour to print
					int print = hour > 12 ? hour - 12 : hour;
					// Print to file the hour
					pw.print(print);
					// For each day
					for(int day = 0; day < 5; day++){
						// Print seperator
						pw.print(",");
						// Get timeslot for this day and hour
						String b = poss[day][hour];
						// If isnt null
						if(b != null){
							// Print code to file
							pw.print(b);
						}
					}
					// Print new line
					pw.println();
				}
				// Print new line
				pw.println();
			}
			// Close print writer
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// Method to get the lowest hour from a timetable
	public int getLowestHour(Timetable w){
		// Init low to highest number
		int low = 24;
		// For each class
		for(Class c : w.getClasses()){
			// Reassign low to the lowest
			low = Math.min(low, c.getStart());
		}
		return low;
	}
	
	// Method to get the highest hour from a timetable
	public int getMaxHour(Timetable w){
		// Init max to lowest number
		int max = 0;
		// For each class
		for(Class c : w.getClasses()){
			// Reassign max to the highest
			max = Math.max(max, c.getEnd());
		}
		return max;
	}
	
	// Returns the total amount timetables
	public int getTotal(List<List<Stream>> streams){
		// If empty return 0
		if(streams.size() == 0){ return 0; }
		// Init total to 1 so calculations work
		int total = 1;
		// For each list
		for(List<Stream> s : streams){
			// Times total by the size of the list
			total *= s.size();
		}
		return total;
	}
	
	// Returns all possible timetables from streams
	public List<Timetable> getPossibleTimetables(List<List<Stream>> streams){
		// Create list to add to
		List<Timetable> list = new ArrayList<Timetable>();
		// Call method to generate timetables
		loop(list, new Timetable(), streams, 0);
		return list;
	}
	
	// Loops over each Stream and adds valid Timetables to the list
	public void loop(List<Timetable> timetables, Timetable current, List<List<Stream>> streams, int depth){
		// Get Streams at current depth
		List<Stream> list = streams.get(depth);
		// For each stream
		for(Stream s : list){
			// Clone timetable
			Timetable timetable = current.clone();
			// Try add the current stream to the timetable
			if(timetable.tryAdd(s)){
				// If maximum depth
				if(depth + 1 == streams.size()){
					// Add timetable to list
					timetables.add(timetable);
				}else{
					// Loop deeper
					loop(timetables, timetable, streams, depth + 1);
				}
			}
		}
	}
	
	// Gets all possible classes for a semester
	public List<Stream> getClasses(Element semester){
		// Init list
		List<Stream> list = new ArrayList<Stream>();
		try{
			// Current Stream
			Stream s = null;
			// For each row in table
			for(Element tr : semester.getElementsByClass("BackgroundLight")){
				// For each column
				Elements tds = tr.getElementsByAttribute("width");
				// Check size
				if(tds.size() > 0){
					// Get possible stream name
					String stream = tds.get(0).text();
					// If stream name not empty
					if(stream.length() > 1){
						// If current Stream isnt null
						if(s != null){
							// Add to list
							list.add(s);
						}
						// New Stream
						s = new Stream(stream);
					}
					// Day of class
					int day = -1;
					// Check day
					switch(tds.get(4).text().substring(0, 3)){
					case "MON":
						day = 1;
						break;
					case "TUE":
						day = 2;
						break;
					case "WED":
						day = 3;
						break;
					case "THU":
						day = 4;
						break;
					case "FRI":
						day = 5;
						break;
					}
					// Get time for class
					String time = tds.get(5).text();
					// Split to get start and end times
					// 4:00 p.m. - 6:00 p.m.
					String split[] = time.split(". - ");
					// Gets start time
					int start = Integer.parseInt(split[0].split(":")[0]);
					// If is pm
					if(split[0].contains("p") && start != 12){
						// Convert to 24 hour
						start += 12;
					}
					// Gets end time
					int end = Integer.parseInt(split[1].split(":")[0]);
					// If is pm
					if(split[1].contains("p") && end != 12){
						// Convert to 24 hour
						end += 12;
					}
					// Add class to Stream
					s.getClasses().add(new Class(s.getCode(), tds.get(6).text(), day, start, end));
				}
			}
			// If Stream isnt null
			if(s != null){
				// Add Stream to list
				list.add(s);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	// Gets all possible semesters and timetable element from url
	public Map<String, Element> getTimetables(String url){
		// Init map
		Map<String, Element> map = new TreeMap<String, Element>();
		try{
			// Get data from url
			Document doc = Jsoup.connect(url).get();
			// Find Timetable element and go up a few elements
			Element tbody = doc.getElementsByAttributeValue("name", "#Timetable").first().parent().parent().parent();
			// Get the table for the timetables
			Elements tables = tbody.getElementsByTag("table");
			// Check size
			if(tables.size() > 0){
				// Get the table
				Element table = tables.get(0);
				// Get all table rows
				Elements trs = table.getElementsByTag("tr");
				// For each td element
				for(Element e1 : table.getElementsByTag("td")){
					// Get span elements
					Elements spans = e1.getElementsByTag("span");
					// If this element is a semester
					if(spans.size() == 2){
						// Get the parent element
						Element tr = e1.parent();
						// Gets semester from spans
						String name = spans.get(0).text() + ", " + spans.get(1).text();
						// Gets index of parent element
						int index = trs.indexOf(tr) + 1;
						// This is the classes element
						Element s_table = trs.get(index);
						// Add to map
						map.put(name, s_table);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	// Get all paper codes from url and level
	public List<Paper> getCodes(String url, String level){
		// Init list
		List<Paper> list = new ArrayList<Paper>();
		try{
			// Get data from url
			Document doc = Jsoup.connect(url).get();
			// Search for heading
			Elements e = doc.getElementsByClass("TextHeading");
			// For each heading
			for(Element e1 : e){
				// If is our level
				if(e1.text().equals(level)){
					// Go up one element
					Element tr = e1.parent();
					// Go up another element
					Element tbody = tr.parent();
					// All table rows
					Elements trs = tbody.getElementsByTag("tr");
					// This is the paper codes index
					int in = trs.indexOf(tr) + 2;
					// The paper codes table
					Element e2 = trs.get(in);
					// For each paper code
					for(Element e3 : e2.getElementsByClass("BackgroundLight")){
						// Paper code elements
						Elements e4 = e3.getElementsByTag("a");
						// Paper code
						Element code = e4.first();
						// Paper name
						Element name = e4.get(1);
						// Paper url
						Paper c = new Paper(code.text(), name.text(), code.attr("href"));
						// Add to list
						list.add(c);
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
}
