package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

import javax.swing.JOptionPane;
import javax.swing.JTree;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.models.BasicModel;

public class AddCatalogListener implements ActionListener{
	private JTree t;
	public AddCatalogListener(JTree t) {
		this.t = t;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String inputValue = JOptionPane.showInputDialog("Please input a value");     
		if(inputValue != null) {
			CatalogNode cn = new CatalogNode();
			cn.catalogID = 1;
			cn.catalogName = inputValue;
			cn.catalogItem = 1;
			cn.catalogCreate = new Date(System.currentTimeMillis());
			cn.catalogRemove = new Date(BasicModel.endTime.getTime());
			
			try {
				((BasicModel) t.getModel()).add(cn, t.getSelectionPath());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		t.updateUI();		
	}
}
