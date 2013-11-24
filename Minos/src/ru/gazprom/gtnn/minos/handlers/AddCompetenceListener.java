package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;

import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.models.BasicModel;

public class AddCompetenceListener implements ActionListener {
	private JTree t;
	public AddCompetenceListener(JTree t) {
		this.t = t;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField nameField = new JTextField();
		JTextArea descrField = new JTextArea(10, 100);
		
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Name"),
				nameField,
				new JLabel("Descr"),
				descrField,
		};
		
		if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "Competence dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) &&
				(!nameField.getText().isEmpty()) ) {
			
			CompetenceNode cn = new CompetenceNode();
			cn.competenceName = nameField.getText(); 
			cn.competenceDescr = descrField.getText();
			cn.competenceItem = 1;
			cn.competenceCreate = new Date(System.currentTimeMillis());
			cn.competenceRemove = new Date(BasicModel.endTime.getTime());
			
			try {
				((BasicModel) t.getModel()).add(cn, t.getSelectionPath());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
			
		//JOptionPane.showMessageDialog(null, inputs, "Competence dialog", JOptionPane.QUESTION_MESSAGE);
		return ;
	}		
}