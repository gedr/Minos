package ru.gazprom.gtnn.minos.models;

import java.util.concurrent.ExecutionException;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode.ProfileType;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.cache.LoadingCache;

public class ProfileModel extends BasicModel {
	  
	private TreeModel competence;
	private LoadingCache<Integer, CompetenceNode> cacheCompetence;
	
	
	public ProfileModel(DatabaseConnectionKeeper kdb,
			TreeModel competence,
			LoadingCache<Integer, CompetenceNode> cacheCompetence) {			
			
		super(kdb);		
		this.competence = competence;
		this.cacheCompetence = cacheCompetence;
	}
	
	@Override
	public Object getRoot() {
		// not implemented
		assert false : "ProfileModel.getRoot() not implemented";
		return null;
	}

	@Override
	public boolean isLeaf(Object arg) {
		if(arg == null) 
			return true;	
		
		if(arg instanceof ProfileNode) {
			ProfileNode node = (ProfileNode)arg;
			if(node.getProfileType() == ProfileType.POSITION_DIVISION) {
				// this profile have competence, always
				CompetenceNode nodeCompetence = getCompetenceByID(node.profileCompetenceID);
				// check competence reference
				assert nodeCompetence != null : "ProfileModel.isLeaf cannot find competence : " ;
				if(nodeCompetence == null) 
					return true;
				
				//profile name equal competence
				node.profileName = nodeCompetence.competenceName;	
								
				return competence.isLeaf(nodeCompetence);
			}
		}
		
		if( (arg instanceof Pair<?, ?>) ||
				(arg instanceof IndicatorNode) ) {
			return competence.isLeaf(arg);			
		}
		
		return true;
	}
	
	@Override
	public int getChildCount(Object arg) {
		if(arg == null) 
			return 0;

		
		if(arg instanceof ProfileNode) {
			ProfileNode node = (ProfileNode)arg;
			if(node.getProfileType() == ProfileType.POSITION_DIVISION) {
				// this profile have competence, always
				CompetenceNode nodeCompetence = getCompetenceByID(node.profileCompetenceID);
				// check competence reference
				assert nodeCompetence != null : "ProfileModel.getChildCount() cannot find competence : " ;
				if(nodeCompetence == null) 
					return 0;
				
				//profile name equal competence
				node.profileName = nodeCompetence.competenceName;	
								
				return competence.getChildCount(nodeCompetence);
			}
		}

		
		if( (arg instanceof Pair<?, ?>) ||
				(arg instanceof IndicatorNode) ) {
			return competence.getChildCount(arg);			
		}
		
		return 0;
	}

	@Override
	public Object getChild(Object arg, int index) {		
		if(arg == null)
			return null;

		if(arg instanceof ProfileNode) {
			ProfileNode node = (ProfileNode)arg;
			if(node.getProfileType() == ProfileType.POSITION_DIVISION) {
				// this profile have competence, always
				CompetenceNode nodeCompetence = getCompetenceByID(node.profileCompetenceID);
				// check competence reference
				assert nodeCompetence != null : "ProfileModel.getChildCount() cannot find competence : " ;
				if(nodeCompetence == null) 
					return 0;
				
				//profile name equal competence
				node.profileName = nodeCompetence.competenceName;	
								
				return competence.getChild(nodeCompetence, index);
			}
		}
		
		if( (arg instanceof Pair<?, ?>) ||
				(arg instanceof IndicatorNode) ) {
			return competence.getChild(arg, index);			
		}

		return null;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
            return -1;

		
		if(parent instanceof ProfileNode) {
			ProfileNode node = (ProfileNode)parent;
			if(node.getProfileType() == ProfileType.POSITION_DIVISION) {
				// this profile have competence, always
				CompetenceNode nodeCompetence = getCompetenceByID(node.profileCompetenceID);
				// check competence reference
				assert nodeCompetence != null : "ProfileModel.getChildCount() cannot find competence : " ;
				if(nodeCompetence == null) 
					return 0;
				
				//profile name equal competence
				node.profileName = nodeCompetence.competenceName;	
								
				return competence.getIndexOfChild(nodeCompetence, child);
			}
		}

		if( (parent instanceof Pair<?, ?>) ||
				(parent instanceof IndicatorNode) ) {
			return competence.getIndexOfChild(parent, child);			
		}
		
		return -1;
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reload() {		
		// TODO
	}

	private CompetenceNode getCompetenceByID(Integer id) {
		CompetenceNode node = null;
		try {
			node = cacheCompetence.get(id);
		} catch (ExecutionException e) {
			node = null;
			e.printStackTrace();
		}
		return node;
	}
}