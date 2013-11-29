package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.entity.RoundNode;
import ru.gazprom.gtnn.minos.entity.RoundActorsNode;
import ru.gazprom.gtnn.minos.entity.RoundProfileNode;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.models.RoundModel;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;

public class AddMinos_SinnerListener implements ActionListener {

	private JTree sinnerTree;
	private JTree minosTree;
	private RoundModel roundModel;
	private DatabaseConnectionKeeper kdb;


	public AddMinos_SinnerListener(JTree minosTree, JTree sinnerTree, RoundModel roundModel,
			DatabaseConnectionKeeper kdb) {
		this.minosTree = minosTree;
		this.sinnerTree = sinnerTree;
		this.roundModel = roundModel;
		this.kdb = kdb;
	}	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(minosTree.isSelectionEmpty() || sinnerTree.isSelectionEmpty())
			return;
		
		RoundNode round = (RoundNode)roundModel.getSelectedItem();
		if( (round == null) || (System.currentTimeMillis() >=  round.roundStop.getTime()) ) {
			return;
		} 		
		
		TreePath[] minosesPath = minosTree.getSelectionPaths();
		TreePath[] sinnersPath = sinnerTree.getSelectionPaths();
		
		// begin create and initialized objects
		RoundActorsNode roundActors = new RoundActorsNode();
		roundActors.roundActorsRoundID = round.roundID;
		roundActors.roundActorsRemove = BasicModel.endTime;
		RoundProfileNode roundProfile = new RoundProfileNode();
				
		try {
			for (TreePath minosPath : minosesPath) {
				if (minosPath.getLastPathComponent() instanceof PersonNode) {
					PersonNode minos = (PersonNode) minosPath.getLastPathComponent();
					
					for (TreePath sinnerPath : sinnersPath) {
						if ((sinnerPath.getLastPathComponent() instanceof PersonNode) &&
								(sinnerTree.getModel().getChildCount(sinnerPath.getLastPathComponent()) > 0)) { // у выделеного элемента есть профили
							
							PersonNode sinner = (PersonNode) sinnerPath.getLastPathComponent();
							
							if (sinner.personID != minos.personID) {
								// save round actors
								roundActors.roundActorsMinosID = minos.personID;
								roundActors.roundActorsSinnerID = sinner.personID;
								roundActors.insert(kdb,
												RoundActorsNode.ROUND_ACTORS_MINOS | RoundActorsNode.ROUND_ACTORS_SINNER | 
												RoundActorsNode.ROUND_ACTORS_REMOVE | RoundActorsNode.ROUND_ACTORS_ROUND);
								int profilCount = sinnerTree.getModel().getChildCount(sinner);

								// save profile for round actors
								for(int i = 0; i < profilCount; i++) {
									Object obj = sinnerTree.getModel().getChild(sinner, i);
								
									if(!(obj instanceof ProfileNode))
										continue;
									
									roundProfile.roundProfileProfileID = ((ProfileNode)obj).profileID;
									roundProfile.roundProfileRoundActorsID = roundActors.roundActorsID;
									roundProfile.insert(kdb, 
											RoundProfileNode.ROUND_PROFILE_ACTORS | RoundProfileNode.ROUND_PROFILE_PROFILE);
									
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}