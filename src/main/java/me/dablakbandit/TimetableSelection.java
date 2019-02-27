/*
 * Copyright (c) 2019 Ashley Thew
 */

package me.dablakbandit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import me.dablakbandit.util.Day;

public class TimetableSelection extends JFrame{
	private JTable			timetable;
	private JPanel			panel;
	private JButton			nextButton;
	private JComboBox		boxPossibleStreams;
	private JButton			btnAdd;
	private JComboBox		boxSelected;
	private JButton			btnRemove;
	private JButton			button1;
	private List<Timetable>	allTimetables;
	private List<Timetable>	filteredTimetables	= new ArrayList<>();
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
		
		nextButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(index < filteredTimetables.size() - 1){
					index++;
				}
				showTimetable();
			}
		});
		btnAdd.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				boxSelected.insertItemAt(boxPossibleStreams.getSelectedItem(), 0);
				boxSelected.setSelectedIndex(0);
				update();
			}
		});
		btnRemove.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				boxSelected.removeItem(boxSelected.getSelectedItem());
				update();
			}
		});
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
			boolean add = true;
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
		panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel.add(	panel1,
					new com.intellij.uiDesigner.core.GridConstraints(	1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		nextButton = new JButton();
		nextButton.setText("Next");
		panel1.add(	nextButton,
					new com.intellij.uiDesigner.core.GridConstraints(	7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
		panel1.add(	spacer1,
					new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		boxPossibleStreams = new JComboBox();
		panel1.add(boxPossibleStreams, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
																						com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Specify Stream");
		panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
																			com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnAdd = new JButton();
		btnAdd.setText("Add");
		panel1.add(	btnAdd,
					new com.intellij.uiDesigner.core.GridConstraints(	2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		boxSelected = new JComboBox();
		panel1.add(boxSelected, new com.intellij.uiDesigner.core.GridConstraints(	3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
																					com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		btnRemove = new JButton();
		btnRemove.setText("Remove");
		panel1.add(	btnRemove,
					new com.intellij.uiDesigner.core.GridConstraints(	4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		button1 = new JButton();
		button1.setText("Button");
		panel1.add(	button1,
					new com.intellij.uiDesigner.core.GridConstraints(	6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel.add(	panel2,
					new com.intellij.uiDesigner.core.GridConstraints(	2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
		panel.add(toolBar$Separator1, new com.intellij.uiDesigner.core.GridConstraints(	0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
																						com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JScrollPane scrollPane1 = new JScrollPane();
		panel.add(	scrollPane1,
					new com.intellij.uiDesigner.core.GridConstraints(	1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
																		com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		timetable = new JTable();
		scrollPane1.setViewportView(timetable);
	}
	
	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$(){
		return panel;
	}
}