package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.models.CatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceAndCatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceModel;

import com.google.common.base.Preconditions;

public class LoadCompetenceCatalogListener implements ActionListener, Runnable {
	private String 		startDir;
	private String 		fileName;
	private CatalogNode parentCatalog;
	private JTree 		tree;
	
	

	public LoadCompetenceCatalogListener(JTree tree) {		
		this.tree = tree;		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( tree.isSelectionEmpty() ) {
			JOptionPane.showMessageDialog(tree, "не выбрана позиция для вставки");
			return;
		}
		TreePath p = tree.getSelectionPath();
		if( !( p.getLastPathComponent() instanceof CatalogNode) ) {
			JOptionPane.showMessageDialog(tree, "не выбрана позиция для вставки");
			return;				
		}
		
		parentCatalog = (CatalogNode) p.getLastPathComponent();
		
		JTextField pathField = new JTextField(100);
		JTextField fileField = new JTextField(100);
		pathField.setText("c:\\tmp\\minos\\");
		fileField.setText("1.txt");

		JComponent[] inputs = new JComponent[] {
				new JLabel("Start catalog"),
				pathField,
				new JLabel("Common file"),
				fileField,
		};
		
		if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "Competence dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) &&
				(!pathField.getText().isEmpty()) && (!fileField.getText().isEmpty()) ) {

			startDir = pathField.getText();
			fileName = fileField.getText();
			Thread thread = new Thread(this);
			thread.start();
		}
			
	}

	@Override
	public void run() {			
		Node node = readDir(Paths.get(startDir));
		if(node == null)
			return;
		printNode(node, 1);
		try {
			catalogLoader(parentCatalog, node, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}			
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
	
	private void catalogLoader(CatalogNode dest, Node node, int level) throws Exception {
		CatalogNode nodeCatalog;
		if(level == 0) {
			nodeCatalog = dest;
		} else {
			nodeCatalog = new CatalogNode();				
			nodeCatalog.catalogName = node.catalogName;
			//nodeCatalog.subCatalogs = Collections.emptyList();
			nodeCatalog.catalogCreate = new java.sql.Date(System.currentTimeMillis());
			nodeCatalog.catalogRemove = BasicModel.endTime;
			BasicModel model = ((CompetenceAndCatalogModel)tree.getModel()).getCatalogModel();
			((CatalogModel)model).add(nodeCatalog, dest, false, 
					CatalogNode.CATALOG_NAME | CatalogNode.CATALOG_PARENT | CatalogNode.CATALOG_ITEM |
					CatalogNode.CATALOG_CREATE | CatalogNode.CATALOG_REMOVE | CatalogNode.CATALOG_VARIETY);
		}

		Preconditions.checkArgument(nodeCatalog.catalogID != -1, "catalogLoader() : cannot load  CatalogNode" + nodeCatalog);
		
		fileLoader(node.filePath, nodeCatalog);
		
		if(node.child != null) {				
			for(Node n : node.child) 
				catalogLoader(nodeCatalog, n, level + 1);
		}
	}

	
	private void fileLoader(Path file, CatalogNode catalog) {
		if(file == null)
			return;
		
		Charset charset = Charset.forName("UTF-8");
		try ( BufferedReader reader = Files.newBufferedReader(file, charset) ) {
		    String line = null;
		    int step = 0;	
		    int itemIndicator = 1;
		    int itemCompetence = 1;
		    ProfileNode profileNode = null;
		    
		    java.util.Date currentDate = new java.util.Date(System.currentTimeMillis()); 

		    // start initialization
		    CompetenceNode nodeCompetence = new CompetenceNode();
		    nodeCompetence.competenceIncarnatio = 0; //
		    nodeCompetence.competenceCatalogID = catalog.catalogID;
		    nodeCompetence.competenceChainNumber = 0;
		    nodeCompetence.competenceCreate = currentDate;
		    nodeCompetence.competenceRemove = BasicModel.endTime;
		    
		    IndicatorNode nodeIndicator = new IndicatorNode();
		    nodeIndicator.indicatorCreate = currentDate;
		    nodeIndicator.indicatorRemove = BasicModel.endTime;   
		    
		    while ((line = reader.readLine()) != null) {
		    	if( line.contains("#") ) { //read division  id and position id
		    		String str = line.substring(line.indexOf("#") + 1, line.length());
		    		int indexColon = str.indexOf(":");
		    		profileNode = new ProfileNode();
		    		profileNode.profileDivisionID = Integer.valueOf(str.substring(0,  indexColon));
		    		profileNode.profilePositionID = Integer.valueOf(str.substring(indexColon + 1,  str.length()));
		    		
		    	}
		    	if ( line.contains("$") ) {
		    			step = 1;
		    			itemIndicator = 1;
		    			itemCompetence++;
		    			continue;
		    	}
		    	if(line.isEmpty()) { 
		    		step++;
		    		itemIndicator = 1;
		    		continue;
		    	}
		    	switch(step) {
		    	case 1:
		    		nodeCompetence.competenceName = line;
		    		nodeCompetence.competenceItem = itemCompetence;
		    		break;
		    	case 2:
		    		nodeCompetence.competenceDescr = line;
		    		try {
						BasicModel model = ((CompetenceAndCatalogModel)tree.getModel()).getCompetenceModel();
						((CompetenceModel)model).add(nodeCompetence, catalog);
						if(profileNode != null) {
							profileNode.profileCompetenceIncarnatio = nodeCompetence.competenceIncarnatio;
							profileNode.profileRemove = BasicModel.endTime;
							profileNode.insert(((BasicModel)tree.getModel()).getDatabaseConnectionKeeper(), 
									ProfileNode.PROFILE_DIVISION | ProfileNode.PROFILE_POSITION | 
									ProfileNode.PROFILE_COMPETENCE_INCARNATIO | ProfileNode.PROFILE_REMOVE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						
					}
		    		break;
		    	case 3:
		    	case 4:
		    	case 5:
		    	case 6:
		    	case 7:
		    		nodeIndicator.indicatorName = line;
		    		nodeIndicator.indicatorItem = itemIndicator++;
		    		nodeIndicator.indicatorLevelID = step - 2;
		    		nodeIndicator.indicatorCompetenceIncarnatio = nodeCompetence.competenceIncarnatio;   		
					
					try {
						BasicModel model = ((CompetenceAndCatalogModel)tree.getModel()).getCompetenceModel();
						((CompetenceModel)model).add(nodeIndicator, nodeCompetence, false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		break;
		    	}			    	
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void printNode(Node node, int level) {
		String s = "";
		for(int i = 0; i < level * 3; i++)
			s += " ";
		System.out.println(s + "<dir> " + node.catalogName);
		if(node.filePath != null ) {
			System.out.println(s + " <file> " + node.filePath);
		}
		
		if(node.child == null) 
			return;
		
		for(Node n : node.child) {
			printNode(n, level + 1);
		}	
	}

	
	private static class Node {
		public List<Node> child;		
		Path filePath;
		String catalogName;
	}
	
	static final int STEP_COUNT = 7;
}
