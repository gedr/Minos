package ru.gazprom.gtnn.minos.handlers;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.LevelNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.entity.StringAttrNode;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.models.CatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceAndCatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceModel;

import com.google.common.base.Preconditions;

class DisplayInfo implements Runnable {
	private int cmd = 0;
	private String str;
	private int 	value;
	private JPanel pan;
	private Component oldGlassPane;
	private JFrame parentFrame;
	private JLabel info;
	private JProgressBar progress;
	
	public DisplayInfo(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		info = new JLabel();
		progress = new JProgressBar();
		
		pan = new JPanel();
		pan.setLayout(new GridBagLayout());
		pan.setOpaque(false);
		GridBagConstraints constr = new GridBagConstraints();
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.gridx = 0;
		constr.gridy = 0;
		pan.add(info, constr);

		constr.gridy = 1;
		pan.add(progress, constr);
		pan.addMouseListener(new MouseAdapter() { });
		pan.addKeyListener(new KeyAdapter() { });		
	}

	@Override
	public void run() {
		switch(cmd) {
		case 1:
			oldGlassPane = parentFrame.getGlassPane();
			parentFrame.setGlassPane(pan);
			pan.setVisible(true);
			break;
		case 2:
			if(oldGlassPane != null)
				parentFrame.setGlassPane(oldGlassPane);
			pan.setVisible(false);
			break;			
		case 3:
			info.setText(str);
			progress.setValue(value);
			break;
		}		
		
	}

	public void show() {
		cmd = 1;
		try {
			EventQueue.invokeAndWait(this);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}		
	
	public void hide() {
		cmd = 2;
		try {
			EventQueue.invokeAndWait(this);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void setText(String txt) {
		cmd = 3;
		str = txt;
		EventQueue.invokeLater(this);
	}
	
	public void setMaxValue(int max) {
		progress.setMaximum(max);
	}
	
	public void setCurrentValue(int value) {
		cmd = 3;
		this.value = value;
		EventQueue.invokeLater(this);
	}

	public int getCurrentValue() {
		return progress.getValue();
	}
}


public class LoadCompetenceCatalogListener implements ActionListener, Runnable {
	private String 		startDir;
	private String 		fileName;
	private CatalogNode parentCatalog;
	private JTree 		tree;
	private JFrame 		parentFrame;
	private DisplayInfo displayInfo;

	
	public LoadCompetenceCatalogListener(JTree tree, JFrame parentFrame) {		
		this.tree = tree;		
		this.parentFrame = parentFrame;
		
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

		int dlgResult = JOptionPane.showOptionDialog(null, inputs, "Competence dialog", 
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
		displayInfo = new DisplayInfo(parentFrame);
		displayInfo.show();
		displayInfo.setText("Загрузка структуры каталогов");
		Node node = readDir(Paths.get(startDir));
		if(node == null)
			return;
		int countDir = calculateNode(node);
		displayInfo.setMaxValue(countDir);
		try {
			catalogLoader(parentCatalog, node, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		displayInfo.hide();
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
			nodeCatalog.catalogRemove = BasicModel.endTime;
			BasicModel model = ((CompetenceAndCatalogModel)tree.getModel()).getCatalogModel();
			((CatalogModel)model).add(nodeCatalog, dest, false, 
					CatalogNode.CATALOG_NAME | CatalogNode.CATALOG_PARENT | CatalogNode.CATALOG_ITEM |
					CatalogNode.CATALOG_REMOVE | CatalogNode.CATALOG_VARIETY);
		}
		
		displayInfo.setText("<html><font size=5 color=red> <b>загружается каталог : " + node.catalogName + "</b>");
		displayInfo.setCurrentValue(displayInfo.getCurrentValue() + 1);

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
		    int itemStringAttr = 1;
		    
		    List<ProfileNode> profileNodes = null;
		    int profileNodeCount = 0;

		    // start initialization
		    CompetenceNode nodeCompetence = new CompetenceNode();
		    nodeCompetence.competenceIncarnatio = 0; //
		    nodeCompetence.competenceCatalogID = catalog.catalogID;
		    nodeCompetence.competenceVariety = catalog.catalogVariety;
		    nodeCompetence.competenceChainNumber = 0;
		    nodeCompetence.competenceRemove = BasicModel.endTime;
		    
		    IndicatorNode nodeIndicator = new IndicatorNode();
		    nodeIndicator.indicatorRemove = BasicModel.endTime;   

		    StringAttrNode stringAttrNode = new StringAttrNode();
			stringAttrNode.stringAttrVariety = StringAttrNode.VARIETY_PROFILE;
			stringAttrNode.stringAttrRemove = BasicModel.endTime;

		    
		    while ((line = reader.readLine()) != null) {
		    	/*
		    	if( line.contains("#") && !line.contains("$")) { //read division  id and position id
		    		String str = line.substring(line.indexOf("#") + 1, line.length());
		    		int indexColon = str.indexOf(":");
		    		flagUseProfile = true;		    		
		    		profileNode.profileDivisionID = Integer.valueOf(str.substring(0,  indexColon));
		    		profileNode.profilePositionID = Integer.valueOf(str.substring(indexColon + 1,  str.length()));
		    		continue;
		    	}
		    	*/

		    	if ( line.contains("$") ) {
		    			step = 1;
		    			itemIndicator = 1;
		    			itemStringAttr = 1;
		    			itemCompetence++;
		    			profileNodeCount = 0;
		    			
		    			if(line.contains("$#")) {
		    				int rsh = line.indexOf("#");
		    				List<Cmd> cmds = parseCmdString(line.substring(rsh + 1));
		    				
		    				profileNodeCount = (cmds == null ? 0 : cmds.size() ); 
		    				
		    				if((cmds != null) && (cmds.size() > 0)) {
		    					if(profileNodes == null) { 
		    						profileNodes = new ArrayList<>();
		    					}
		    					if(profileNodes.size() < profileNodeCount) {
		    						int dif = cmds.size() - profileNodes.size();
		    						for(int i = 0; i < dif; i++)
		    							profileNodes.add(new ProfileNode());
		    					}
		    					for(int i = 0; i < profileNodeCount; i++) {
		    						profileNodes.get(i).profileID = -1;
		    						profileNodes.get(i).profileVariety = ProfileNode.VARIETY__DIVISION_AND_POSITION;
		    						profileNodes.get(i).profileDivisionID = cmds.get(i).divisionID;
		    						profileNodes.get(i).profilePositionID = cmds.get(i).positionID;
		    						profileNodes.get(i).profileRemove = BasicModel.endTime;
		    						profileNodes.get(i).profileMinLevel = 
		    								( ((1 <= cmds.get(i).minLevel) && (cmds.get(i).minLevel <= LevelNode.LEVEL_COUNT)) ? cmds.get(i).minLevel : 1);
		    					}
		    				}		    				
		    				cmds.clear();
		    			}
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
		    		break;
		    	case 2:
		    		nodeCompetence.competenceDescr = line;
		    		nodeCompetence.competenceItem = itemCompetence;
		    		try {
						BasicModel model = ((CompetenceAndCatalogModel)tree.getModel()).getCompetenceModel();
						((CompetenceModel)model).add(nodeCompetence, catalog); // save to DB and TreeModel
						if(profileNodeCount > 0) {
	    					for(int i = 0; i < profileNodeCount; i++) {
	    						profileNodes.get(i).profileCompetenceIncarnatio = nodeCompetence.competenceIncarnatio;
	    						profileNodes.get(i).insert(((BasicModel)tree.getModel()).getDatabaseConnectionKeeper(), 
										ProfileNode.PROFILE_DIVISION | ProfileNode.PROFILE_POSITION | 
										ProfileNode.PROFILE_MIN_LEVEL | ProfileNode.PROFILE_VARIETY |
										ProfileNode.PROFILE_COMPETENCE_INCARNATIO | ProfileNode.PROFILE_REMOVE);
	    					}
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
		    		nodeIndicator.indicatorCompetenceIncarnatio = nodeCompetence.competenceIncarnatio; // save to DB and TreeModel   		
					try {
						BasicModel model = ((CompetenceAndCatalogModel)tree.getModel()).getCompetenceModel();
						((CompetenceModel)model).add(nodeIndicator, nodeCompetence, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		break;
		    	case 8:
		    		if(profileNodeCount > 0) {
    					for(int i = 0; i < profileNodeCount; i++) {    						
    						stringAttrNode.stringAttrExternalID1 = profileNodes.get(i).profileID;
    						stringAttrNode.stringAttrValue = line;
    						stringAttrNode.stringAttrItem = itemStringAttr++;
    						try {
    							BasicModel model = ((CompetenceAndCatalogModel)tree.getModel()).getCompetenceModel();
    							stringAttrNode.insert(model.getDatabaseConnectionKeeper(), 
    									StringAttrNode.STRING_ATTR_ITEM | StringAttrNode.STRING_ATTR_EXTERNAL_ID1 |
    									StringAttrNode.STRING_ATTR_VALUE | StringAttrNode.STRING_ATTR_REMOVE |
    									StringAttrNode.STRING_ATTR_VARIETY);
    						} catch (Exception e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    					}		    		
		    		}
		    		break;
		    	}			    	
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class Cmd {
		public int divisionID;
		public int positionID;
		public int minLevel;		
	}

	private List<Cmd> parseCmdString(String str) {		
		int step = 1;
		List<Cmd> lst = null;
		Cmd cmd = null;
		StringTokenizer st = new StringTokenizer(str, ":");
		
		while (st.hasMoreTokens()){
			switch(step) { 
			case 1:
				if(cmd == null)
					cmd = new Cmd();
				cmd.divisionID = Integer.valueOf(st.nextToken());
				break;
			case 2:
				cmd.positionID = Integer.valueOf(st.nextToken());
				break;
			case 3:
				cmd.minLevel = Integer.valueOf(st.nextToken());
				break;
			}
			
			step++;
			
			if(step == 4) {
				step = 1;
				if(lst == null) {
					lst = new ArrayList<>();
				}
				lst.add(cmd);
				cmd = null;					
			}		   
		}
		
		return lst;
	}

	private int calculateNode(Node node) {
		if(node == null)
			return 0;
		
		int sum = 1;
		if(node.child != null)
			for(Node n : node.child) {
				sum += calculateNode(n);
			}	
		
		return sum;
	}

	
	private static class Node {
		public List<Node> child;		
		Path filePath;
		String catalogName;
	}	
}
