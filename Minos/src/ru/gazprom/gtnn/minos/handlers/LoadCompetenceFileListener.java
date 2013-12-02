package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

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
		if( tree.isSelectionEmpty() || !( tree.getSelectionPath().getLastPathComponent() instanceof CatalogNode)) {
			JOptionPane.showMessageDialog(parentFrame, "не выбрана позиция для вставки");
			return;
		}
		
		parentCatalog = (CatalogNode) tree.getSelectionPath().getLastPathComponent();
		
		
		JFileChooser fileChooser = new JFileChooser();
		if(JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(parentFrame)) {
			fileName = fileChooser.getSelectedFile().getAbsolutePath();
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
