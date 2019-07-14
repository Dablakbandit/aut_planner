/*
 * Copyright (c) 2019 Ashley Thew
 */

package me.dablakbandit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.*;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import me.dablakbandit.util.Campus;

public class SelectionGUI extends JFrame{
	private JPanel		panel;
	private JButton		btnSpecialisation;
	private JComboBox	boxSpecialisation;
	private JButton		btnQualification;
	private JComboBox	boxQualification;
	private JButton		btnLevel;
	private JComboBox	boxLevel;
	private JComboBox	boxClasses;
	private JButton		btnClasses;
	private JButton		btnRemove;
	private JComboBox	boxSelected;
	private JButton		btnGenerate;
	private JComboBox	boxCampus;
	private JComboBox	boxSemester;
	private JButton		btnCampus;
	
	public SelectionGUI(){
		setTitle("AUT Planner - By Ashley Thew");
		setSize(700, 400);
		add(panel);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		for(Specialisation s : AutPlanner.getInstance().getSpecialisations()){
			boxSpecialisation.addItem(s);
		}
		for(Campus c : Campus.values()){
			boxCampus.addItem(c);
		}
		btnSpecialisation.addActionListener(e -> {
			Specialisation s = (Specialisation)boxSpecialisation.getSelectedItem();
			s.populateQualifications(boxQualification);
		});
		btnQualification.addActionListener(e -> {
			Course c = (Course)boxQualification.getSelectedItem();
			c.populateLevel(boxLevel);
		});
		btnLevel.addActionListener(e -> {
			Level l = (Level)boxLevel.getSelectedItem();
			l.populate(boxClasses);
		});
		btnClasses.addActionListener(e -> {
			Paper paper = (Paper)boxClasses.getSelectedItem();
			boxSelected.insertItemAt(boxClasses.getSelectedItem(), 0);
			boxSelected.setSelectedIndex(0);
			AutPlanner.getInstance().showMessageBox("Fetching Streams", () -> {
				paper.getSemesters();
				checkSemesters();
			});
		});
		btnRemove.addActionListener(e -> {
			boxSelected.removeItem(boxSelected.getSelectedItem());
			checkSemesters();
		});
		btnCampus.addActionListener(e -> {
			String semester = (String)boxSemester.getSelectedItem();
			Campus campus = (Campus)boxCampus.getSelectedItem();
			for(int i = 0; i < boxSelected.getItemCount(); i++){
				Paper paper = (Paper)boxSelected.getItemAt(i);
				Semester sem = paper.getSemester(semester);
				if(sem == null || !sem.hasCampus(campus)){
					AutPlanner.getInstance().showMessageBox("Invalid campus", 2000, () -> {
						
					});
					return;
				}
			}
			AutPlanner.getInstance().showMessageBox("Valid campus", 1200, () -> {
			});
		});
		btnGenerate.addActionListener(e -> {
			String semester = (String)boxSemester.getSelectedItem();
			List<Semester> semesters = new ArrayList<>();
			for(int i = 0; i < boxSelected.getItemCount(); i++){
				Paper paper = (Paper)boxSelected.getItemAt(i);
				Semester sem = paper.getSemester(semester);
				semesters.add(sem);
			}
			Campus campus = (Campus)boxCampus.getSelectedItem();
			List<List<Stream>> streams = new ArrayList<>();
			for(Semester sem : semesters){
				streams.add(sem.getStreams(campus));
			}
			
			List<Timetable> timetables = getPossibleTimetables(streams);
			
			System.out.println("Found " + timetables.size() + "/" + getTotal(streams) + " possible timetables");
			AutPlanner.getInstance().showTimetable(timetables);
		});
	}
	
	public void checkSemesters(){
		AutPlanner.getInstance().showMessageBox("Checking semesters", () -> {
			boxSemester.removeAllItems();
			List<Paper> papers = new ArrayList<Paper>();
			Set<String> semesters = new TreeSet<>();
			for(int i = 0; i < boxSelected.getItemCount(); i++){
				Paper paper = (Paper)boxSelected.getItemAt(i);
				for(Semester semester : paper.getSemesters()){
					semesters.add(semester.getSemester());
				}
				papers.add(paper);
			}
			for(String s : new ArrayList<>(semesters)){
				for(Paper paper : papers){
					if(!paper.hasSemester(s)){
						semesters.remove(s);
					}
				}
			}
			if(semesters.isEmpty()){
				AutPlanner.getInstance().showMessageBox("No possible semesters found", 2000, () -> {
				});
			}else{
				for(String semester : semesters){
					boxSemester.addItem(semester);
				}
				AutPlanner.getInstance().showMessageBox("Found valid semesters", 1200, () -> {
				});
			}
		});
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
	
	{
		// GUI initializer generated by IntelliJ IDEA GUI Designer
		// >>> IMPORTANT!! <<<
		// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}
	
	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$(){
		panel = new JPanel();
		panel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(15, 3, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Specialisation:");
		panel2.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnSpecialisation = new JButton();
		btnSpecialisation.setText("Fetch");
		btnSpecialisation.setToolTipText("Fetch Qualifications");
		panel2.add(btnSpecialisation, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxSpecialisation = new JComboBox();
		panel2.add(boxSpecialisation, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnQualification = new JButton();
		btnQualification.setText("Fetch");
		btnQualification.setToolTipText("Fetch Levels");
		panel2.add(btnQualification, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxQualification = new JComboBox();
		panel2.add(boxQualification, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Qualification:");
		panel2.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnLevel = new JButton();
		btnLevel.setText("Select");
		btnLevel.setToolTipText("Select Level");
		panel2.add(btnLevel, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("Level:");
		panel2.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxLevel = new JComboBox();
		panel2.add(boxLevel, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("Classes:");
		panel2.add(label4, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxClasses = new JComboBox();
		panel2.add(boxClasses, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnClasses = new JButton();
		btnClasses.setText("Add");
		btnClasses.setToolTipText("Add selected class");
		panel2.add(btnClasses, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSeparator separator1 = new JSeparator();
		panel2.add(separator1, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		btnRemove = new JButton();
		btnRemove.setText("Remove");
		btnRemove.setToolTipText("Remove selected class");
		panel2.add(btnRemove, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxSelected = new JComboBox();
		panel2.add(boxSelected, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSeparator separator2 = new JSeparator();
		panel2.add(separator2, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		btnGenerate = new JButton();
		btnGenerate.setText("Generate");
		panel2.add(btnGenerate, new GridConstraints(14, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label5 = new JLabel();
		label5.setText("Semester:");
		panel2.add(label5, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxCampus = new JComboBox();
		panel2.add(boxCampus, new GridConstraints(12, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxSemester = new JComboBox();
		panel2.add(boxSemester, new GridConstraints(11, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSeparator separator3 = new JSeparator();
		panel2.add(separator3, new GridConstraints(13, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label6 = new JLabel();
		label6.setText("Selected Classes:");
		panel2.add(label6, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label7 = new JLabel();
		label7.setText("Select semester:");
		panel2.add(label7, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JSeparator separator4 = new JSeparator();
		panel2.add(separator4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JLabel label8 = new JLabel();
		label8.setText("Select specialisation first:");
		panel2.add(label8, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label9 = new JLabel();
		label9.setText("Campus:");
		panel2.add(label9, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnCampus = new JButton();
		btnCampus.setText("Check");
		btnCampus.setToolTipText("Check campus valid");
		panel2.add(btnCampus, new GridConstraints(12, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel1.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label10 = new JLabel();
		Font label10Font = this.$$$getFont$$$(null, -1, 16, label10.getFont());
		if(label10Font != null)
			label10.setFont(label10Font);
		label10.setText("AUT Planner");
		panel3.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
		panel.add(toolBar$Separator1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
		panel.add(toolBar$Separator2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}
	
	/**
	 * @noinspection ALL
	 */
	private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont){
		if(currentFont == null)
			return null;
		String resultName;
		if(fontName == null){
			resultName = currentFont.getName();
		}else{
			Font testFont = new Font(fontName, Font.PLAIN, 10);
			if(testFont.canDisplay('a') && testFont.canDisplay('1')){
				resultName = fontName;
			}else{
				resultName = currentFont.getName();
			}
		}
		return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
	}
	
	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$(){
		return panel;
	}
	
}
