package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;

import ru.gazprom.gtnn.minos.models.BasicModel;

public class ReloadListener implements ActionListener{
	private JTree t;
	public ReloadListener(JTree t) {
		this.t = t;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		((BasicModel)t.getModel()).reload();
		t.updateUI();
		
	}	
}
