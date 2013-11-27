package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import ru.gazprom.gtnn.minos.models.CompetenceAndCatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceModel;


public class LoadCompetenceFileListener implements ActionListener, Runnable{
	private String 		fileName;
	private CatalogNode parentCatalog;
	private JTree 		tree;

	public LoadCompetenceFileListener(JTree tree) {		
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
		
		JTextField fileField = new JTextField(100);
		fileField.setText("c:\\tmp\\minos\\uprav.txt");

		JComponent[] inputs = new JComponent[] {
				new JLabel("полный путь и название файла"),
				fileField,
		};
		
		if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "Competence dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) &&
				(!fileField.getText().isEmpty()) ) {

			fileName = fileField.getText();
			Thread thread = new Thread(this);
			thread.start();
		}
			
	}

	@Override
	public void run() {			
		try {
			fileLoader(Paths.get(fileName), parentCatalog);

		} catch (Exception e) {
			e.printStackTrace();
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
		    
		    //java.util.Date currentDate = new java.util.Date(System.currentTimeMillis()); 

		    // start initialization
		    CompetenceNode nodeCompetence = new CompetenceNode();
		    nodeCompetence.competenceIncarnatio = 0; //
		    nodeCompetence.competenceCatalogID = catalog.catalogID;
		    nodeCompetence.competenceChainNumber = 0;
		    nodeCompetence.competenceRemove = BasicModel.endTime;
		    
		    IndicatorNode nodeIndicator = new IndicatorNode();
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
}
