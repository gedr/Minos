package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.util.CompetenceFileLoader;
import ru.gazprom.gtnn.minos.util.DisplayInfo;

public class LoadCompetenceFileListener implements ActionListener, Runnable{
	private String 		fileName;
	private CatalogNode parentCatalog;
	private JTree 		tree;
	private JFrame parentFrame;

	public LoadCompetenceFileListener(JTree tree, JFrame parentFrame) {		
		this.tree = tree;
		this.parentFrame = parentFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {		
		if( tree.isSelectionEmpty() ) {
			JOptionPane.showMessageDialog(parentFrame, "не выбрана позиция для вставки");
			return;
		}
		TreePath p = tree.getSelectionPath();
		if( !( p.getLastPathComponent() instanceof CatalogNode) ) {
			JOptionPane.showMessageDialog(parentFrame, "не выбрана позиция для вставки");
			return;				
		}
		
		parentCatalog = (CatalogNode) p.getLastPathComponent();
		
		JTextField fileField = new JTextField(100);
		fileField.setText("c:\\tmp\\minos\\uprav.txt");

		JComponent[] inputs = new JComponent[] {
				new JLabel("полный путь и название файла"),
				fileField,
		};
		
		if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "Competence dialog", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) &&
				(!fileField.getText().isEmpty()) ) {
			fileName = fileField.getText();
			Thread thread = new Thread(this);
			thread.start();
		}			
	}

	@Override
	public void run() {
		DisplayInfo di = new DisplayInfo(parentFrame);
		di.show();
		di.setText("Загрузка файла");
		try {
			CompetenceFileLoader.load(Paths.get(fileName), parentCatalog, 
					((BasicModel)tree.getModel()).getDatabaseConnectionKeeper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		di.hide();
	}	
}
