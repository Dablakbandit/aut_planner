/*
 * Copyright (c) 2019 Ashley Thew
 */

package me.dablakbandit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AutPlanner{
	
	private static AutPlanner autPlanner;
	
	public static AutPlanner getInstance(){
		return autPlanner;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		autPlanner = new AutPlanner();
	}
	
	protected List<Specialisation> specialisations = new ArrayList<Specialisation>();
	
	public List<Specialisation> getSpecialisations(){
		return specialisations;
	}
	
	private AutPlanner(){
		showLoading(() -> {
			loadSpecialistions();
			showPlanner();
		});
	}
	
	protected void loadSpecialistions(){
		try{
			// Get data from url
			Document doc = Jsoup.connect("https://arion.aut.ac.nz/ArionMain/CourseInfo/Information/Qualifications/Subjects.aspx").get();
			Elements navigations = doc.getElementsByClass("Navigation");
			for(Element navigation : navigations){
				String specialisation = navigation.text();
				String url = "https://arion.aut.ac.nz/ArionMain/CourseInfo/Information/Qualifications/" + navigation.attr("href");
				System.out.println("Found: " + specialisation);
				specialisations.add(new Specialisation(specialisation, url));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void showMessageBox(String message, Runnable runnable){
		showMessageBox(message, 1, runnable);
	}
	
	public void showMessageBox(String message, int length, Runnable runnable){
		SwingUtilities.invokeLater(() -> {
			final JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
			
			final JDialog dialog = new JDialog();
			dialog.setTitle("AUT Planner");
			dialog.setModal(true);
			dialog.setContentPane(optionPane);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.pack();
			
			Timer timer = new Timer(length, new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent ae){
					runnable.run();
					dialog.dispose();
				}
			});
			timer.setRepeats(false);
			timer.start();
			
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		});
	}
	
	protected void showPlanner(){
		SwingUtilities.invokeLater(() -> {
			SelectionGUI p = new SelectionGUI();
			p.setVisible(true);
		});
	}
	
	protected void showTimetable(List<Timetable> timetables){
		SwingUtilities.invokeLater(() -> {
			TimetableSelection p = new TimetableSelection(timetables);
			p.setVisible(true);
		});
	}
	
	protected void showLoading(Runnable runnable){
		showMessageBox("Loading...", runnable);
	}
	
}
