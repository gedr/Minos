package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.models.CatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceAndCatalogModel;
import ru.gazprom.gtnn.minos.util.CompetenceFileLoader;
import ru.gazprom.gtnn.minos.util.DisplayInfo;

import com.google.common.base.Preconditions;

public class LoadCompetenceCatalogListener implements ActionListener, Runnable {
	private String 		startDir;
	private String 		fileName;
	private CatalogNode parentCatalog;
	private JTree 		tree;
	private JFrame 		parentFrame;
	
	public LoadCompetenceCatalogListener(JTree tree, JFrame parentFrame) {		
		this.tree = tree;		
		this.parentFrame = parentFrame;		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( tree.isSelectionEmpty() || 
				( !(tree.getSelectionPath().getLastPathComponent() instanceof CatalogNode)) ) {
			JOptionPane.showMessageDialog(tree, "Необходимо выбрать каталог");
			return;
		}
		
		parentCatalog = (CatalogNode) tree.getSelectionPath().getLastPathComponent();
		
		JTextField pathField = new JTextField(100);
		JTextField fileField = new JTextField(100);
		pathField.setText("c:\\tmp\\minos\\");
		fileField.setText("1.txt");

		JComponent[] inputs = new JComponent[] {
				new JLabel("Корневой каталог"),
				pathField,
				new JLabel("Общий файл"),
				fileField,
		};

		int dlgResult = JOptionPane.showOptionDialog(null, inputs, "Загузка каталогов с компетенциями", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
				null, null, null);
		if( !(dlgResult == JOptionPane.OK_OPTION) || (pathField.getText().isEmpty()) || (fileField.getText().isEmpty()) ) {
			return;
		}

		startDir = pathField.getText();
		fileName = fileField.getText();

		Thread thread = new Thread(this);		
		thread.start();
	}	
	
	@Override
	public void run() {
		DisplayInfo di = new DisplayInfo(parentFrame);
		di.show();
		di.setText("<html><font size=5 color=red> <b>Загрузка структуры каталогов</b>");
		
		Node node = readDir(Paths.get(startDir));
		if(node != null) {
			try {
				catalogLoader(parentCatalog, node, di);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		di.hide();
	}	
	
	private Node readDir(Path startPath) {
		Node node = new Node();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(startPath)) {					
			node.catalogName = startPath.getFileName().toString();
			
		    for (Path file: stream) {		    	
		        if(Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {		        	
		        	if(node.child == null)
		        		node.child = new ArrayList<>();
		        	node.child.add(readDir(file));
		        }

		        if(file.getFileName().toString().equalsIgnoreCase(fileName))
		        	node.filePath = file;
		    }
		} catch (IOException | DirectoryIteratorException e) {
			e.printStackTrace();
		}		
		return node;
	}

	private void catalogLoader(CatalogNode dest, Node node, DisplayInfo di) throws Exception {
		CatalogNode nodeCatalog;
		nodeCatalog = new CatalogNode();				
		nodeCatalog.catalogName = node.catalogName;
		nodeCatalog.catalogRemove = BasicModel.endTime;
		BasicModel model = ((CompetenceAndCatalogModel)tree.getModel()).getCatalogModel();
		((CatalogModel)model).add(nodeCatalog, dest, false, 
				CatalogNode.CATALOG_NAME | CatalogNode.CATALOG_PARENT | CatalogNode.CATALOG_ITEM |
				CatalogNode.CATALOG_REMOVE | CatalogNode.CATALOG_VARIETY);
		
		di.setText("<html><font size=5 color=red> <b>Загружается каталог : " + node.catalogName + "</b>");

		Preconditions.checkArgument(nodeCatalog.catalogID != -1, "catalogLoader() : cannot load  CatalogNode" + nodeCatalog);

		CompetenceFileLoader.load(node.filePath, nodeCatalog, model.getDatabaseConnectionKeeper());
	
		if(node.child != null) {				
			for(Node n : node.child) 
				catalogLoader(nodeCatalog, n, di);
		}
	}

	private static class Node {
		public List<Node> child;		
		Path filePath;
		String catalogName;
	}	
}