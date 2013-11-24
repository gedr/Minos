package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

import javax.swing.JOptionPane;
import javax.swing.JTree;

import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.models.BasicModel;

public class AddIndicatorListener implements ActionListener {
	private JTree t;
	public AddIndicatorListener(JTree t) {
		this.t = t;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String inputValue = JOptionPane.showInputDialog("Please input a indicator");     
		System.out.println(inputValue);
		if(inputValue != null) {
			IndicatorNode source = new IndicatorNode();								
			source.indicatorName = inputValue;
			source.indicatorCreate = new Date(System.currentTimeMillis());
			source.indicatorRemove = new Date(BasicModel.endTime.getTime());
			try {
				((BasicModel) t.getModel()).add(source, t.getSelectionPath());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		//t.updateUI();
	}	
}
