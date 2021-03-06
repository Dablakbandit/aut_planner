/*
 * Copyright (c) 2019 Ashley Thew
 */

package me.dablakbandit;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import me.dablakbandit.util.Day;

public class TimetableSelection extends JFrame{
	private JTable			timetable;
	private JPanel			panel;
	private JButton			nextButton;
	private JComboBox		boxPossibleStreams;
	private JButton			btnAdd;
	private JComboBox		boxSelected;
	private JButton			btnRemove;
	private JButton			btnBefore;
	private JButton			btnRemoveTimetable;
	private JButton			btnClear;
	private JButton			btnExport;
	private List<Timetable>	allTimetables;
	private List<Timetable>	filteredTimetables	= new ArrayList<>();
	private List<Timetable>	removedTimetables	= new ArrayList<>();
	private int				index				= 0;
	
	public TimetableSelection(List<Timetable> timetables){
		this.allTimetables = timetables;
		
		add(panel);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(700, 400);
		
		DefaultTableModel model = (DefaultTableModel)timetable.getModel();
		model.addColumn("Time");
		for(Day d : Day.values()){
			model.addColumn(d.getName());
		}
		Set<String> set = new TreeSet<>();
		for(Timetable timetable : timetables){
			for(Class clas : timetable.getClasses()){
				set.add(clas.getCode());
			}
		}
		for(String code : set){
			boxPossibleStreams.addItem(code);
		}
		
		update();
		
		nextButton.addActionListener(e -> {
			if(index < filteredTimetables.size() - 1){
				index++;
			}
			showTimetable();
		});
		btnAdd.addActionListener(e -> {
			boxSelected.insertItemAt(boxPossibleStreams.getSelectedItem(), 0);
			boxSelected.setSelectedIndex(0);
			update();
		});
		btnRemove.addActionListener(e -> {
			boxSelected.removeItem(boxSelected.getSelectedItem());
			update();
		});
		btnBefore.addActionListener(e -> {
			if(index > 0){
				index--;
			}
			showTimetable();
		});
		btnRemoveTimetable.addActionListener(e -> {
			removedTimetables.add(filteredTimetables.get(index));
			update();
		});
		btnClear.addActionListener(e -> {
			removedTimetables.clear();
			update();
		});
		btnExport.addActionListener(e -> export());
	}
	
	public void update(){
		filter();
		showTimetable();
	}
	
	public void showTimetable(){
		setTitle((index + 1) + "/" + filteredTimetables.size());
		DefaultTableModel model = (DefaultTableModel)timetable.getModel();
		while(model.getRowCount() != 0){
			model.removeRow(0);
		}
		if(filteredTimetables.size() > 0){
			Timetable t = filteredTimetables.get(index);
			for(String[] s : t.generateData()){
				model.addRow(s);
			}
		}
	}
	
	public void filter(){
		index = 0;
		this.filteredTimetables.clear();
		for(Timetable t : this.allTimetables){
			boolean add = !removedTimetables.contains(t);
			for(int i = 0; i < boxSelected.getItemCount(); i++){
				if(!t.hasClass((String)boxSelected.getItemAt(i))){
					add = false;
					break;
				}
			}
			if(add){
				this.filteredTimetables.add(t);
			}
		}
	}
	
	public void export(){
		printToFile(filteredTimetables);
	}
	
	// Method to print timetables to file
	public void printToFile(List<Timetable> timetables){
		// Create a file chooser
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
		// In response to a button click:
		int returnVal = fc.showOpenDialog(btnExport);
		if(returnVal != JFileChooser.APPROVE_OPTION){ return; }
		// The file we want to write to
		File f = fc.getSelectedFile();
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
				int min_hour = w.getLowestHour();
				// Get the highest hour in timetable
				int max_hour = w.getMaxHour();
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
		panel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(13, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel.add(panel1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		nextButton = new JButton();
		nextButton.setText("Next");
		panel1.add(nextButton, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel1.add(spacer1, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		boxPossibleStreams = new JComboBox();
		panel1.add(boxPossibleStreams, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Specify Stream");
		panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnAdd = new JButton();
		btnAdd.setText("Add");
		btnAdd.setToolTipText("Add specific stream");
		panel1.add(btnAdd, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxSelected = new JComboBox();
		panel1.add(boxSelected, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnRemove = new JButton();
		btnRemove.setText("Remove");
		btnRemove.setToolTipText("Remove specified stream");
		panel1.add(btnRemove, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnBefore = new JButton();
		btnBefore.setText("Before");
		panel1.add(btnBefore, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
		panel1.add(toolBar$Separator1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnRemoveTimetable = new JButton();
		btnRemoveTimetable.setText("Remove");
		btnRemoveTimetable.setToolTipText("Remove this timetable from view");
		panel1.add(btnRemoveTimetable, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Timetables");
		panel1.add(label2, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnClear = new JButton();
		btnClear.setText("Clear");
		btnClear.setToolTipText("Clear removed timetables");
		panel1.add(btnClear, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnExport = new JButton();
		btnExport.setText("Export");
		btnExport.setToolTipText("Export timetables to csv");
		panel1.add(btnExport, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel.add(panel2, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
		panel.add(toolBar$Separator2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		panel.add(	scrollPane1,
					new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		timetable = new JTable();
		scrollPane1.setViewportView(timetable);
		final JToolBar.Separator toolBar$Separator3 = new JToolBar.Separator();
		panel.add(toolBar$Separator3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}
	
	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$(){
		return panel;
	}
	
}
