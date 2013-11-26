package ru.gazprom.gtnn.minos.entity;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

public class MakeProfileAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private JTree competenceTree;
	private JTree profileTree;
	
	public MakeProfileAction(JTree competenceTree, JTree profileTree) {
		this.competenceTree = competenceTree;
		this.profileTree = profileTree;		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if( competenceTree.isSelectionEmpty() || profileTree.isSelectionEmpty() ) 
			return;

		TreePath[] competencePaths = competenceTree.getSelectionPaths();
		List<Integer> lstCompetenceID = new ArrayList<>();
		
		// load selected competence node
		for(int i = 0; i < competencePaths.length; i++) {
			if(competencePaths[i].getLastPathComponent() instanceof CompetenceNode) {
				CompetenceNode node = (CompetenceNode) competencePaths[i].getLastPathComponent();
				lstCompetenceID.add(node.competenceIncarnatio);
			}
		}
		
		if (lstCompetenceID.size() == 0)
			return;
		
		TreePath[] positionPaths = profileTree.getSelectionPaths();
		// load selected position node
		ProfileNode profileNode = new ProfileNode();
		profileNode.profileRemove = BasicModel.endTime;

		DatabaseConnectionKeeper kdb = ((BasicModel)competenceTree.getModel() ).getDatabaseConnectionKeeper();
		for(int i = 0; i < positionPaths.length; i++) {
			if(positionPaths[i].getLastPathComponent() instanceof Pair<?, ?>) {
				@SuppressWarnings("unchecked")
				Pair<Integer, PositionNode> p = (Pair<Integer, PositionNode> ) positionPaths[i].getLastPathComponent() ;
				profileNode.profileDivisionID = p.getFirst();
				profileNode.profilePositionID = p.getSecond().positionID;

				for(Integer it : lstCompetenceID) {
					profileNode.profileCompetenceIncarnatio = it;

					try {
						profileNode.insert(kdb, 
								ProfileNode.PROFILE_DIVISION | ProfileNode.PROFILE_POSITION | 
								ProfileNode.PROFILE_COMPETENCE_INCARNATIO | ProfileNode.PROFILE_REMOVE);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		
		

		
		
		

	}
	
	

}
