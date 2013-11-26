package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;

import ru.gazprom.gtnn.minos.entity.RoundNode;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;

public class AddRoundListener  implements ActionListener {

	private DatabaseConnectionKeeper kdb;

	public AddRoundListener(DatabaseConnectionKeeper kdb) {
		this.kdb = kdb;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JTextField nameField = new JTextField(100);
		JTextField descrField = new JTextField(100);
		nameField.setText("round name");
		descrField.setText("round description");

	    // редактирование даты - формат даты
	    DateFormat date = new SimpleDateFormat("dd.MM.yyyy");
	    // настройка форматирующего объекта
	    DateFormatter formatter = new DateFormatter(date);
	    formatter.setAllowsInvalid(false);
	    formatter.setOverwriteMode(true);
	    // настройка текстового поля
	    JFormattedTextField dateField1 = new JFormattedTextField(formatter);
	    dateField1.setColumns(15);
	    dateField1.setValue(new Date());
	    JFormattedTextField dateField2 = new JFormattedTextField(formatter);
	    dateField2.setColumns(15);
	    dateField2.setValue(new Date());

		
		JComponent[] inputs = new JComponent[] {
				new JLabel("round name"),
				nameField,
				new JLabel("round description"),
				descrField,
				new JLabel("begin date"),
				dateField1,
				new JLabel("end date"),
				dateField2
		};
		
		
		if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "round dialog", 
				JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, null, null)) &&
				(!nameField.getText().isEmpty()) ) {
			
			RoundNode round = new RoundNode();
			round.roundName = nameField.getText();
			round.roundDescr = descrField.getText();
			round.roundRemove = BasicModel.endTime;
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			try {
				round.roundStart = df.parse( dateField1.getText() );
				round.roundStop = df.parse( dateField2.getText() );
				if( (round.roundStop.getTime() - round.roundStart.getTime()) < 0) {
					JOptionPane.showMessageDialog(null, "Ошибочный временой промежуток");
					return;
				}
				round.insert(kdb, 
						RoundNode.ROUND_NAME | RoundNode.ROUND_DESCR | RoundNode.ROUND_REMOVE |
						RoundNode.ROUND_START | RoundNode.ROUND_STOP);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
