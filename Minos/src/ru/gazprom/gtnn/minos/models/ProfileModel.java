package ru.gazprom.gtnn.minos.models;

import java.util.concurrent.ExecutionException;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.entity.StringAttrNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

public class ProfileModel extends BasicModel {
	  
	public ProfileModel(DatabaseConnectionKeeper kdb,
			TreeModel competence,
			LoadingCache<Integer, CompetenceNode> cacheCompetence,
			LoadingCache<Integer, StringAttrNode> cacheStringAttr,
			String sqlLoadStringAttrIDs, String pattern) {			
			
		super(kdb);		
		this.competence = competence;
		this.cacheCompetence = cacheCompetence;
		this.cacheStringAttr = cacheStringAttr;	
		this.sqlLoadStringAttrIDs = sqlLoadStringAttrIDs;
		this.pattern = pattern;
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
	
	public int getStringAttrCount(ProfileNode node) {
		checkAndLoadStringAtrs(node);		
		return node.lstStringAttr.size();
	}
	
	public void reloadStringAttrs(ProfileNode node) {
		if( (node.lstStringAttr != null) && (node.lstStringAttr.size() != 0) )
			node.lstStringAttr.clear();
		node.lstStringAttr = null;
		checkAndLoadStringAtrs(node);
	}
	
	public StringAttrNode getStringAttr(ProfileNode node, int index) {
		if( (node.lstStringAttr == null) || (node.lstStringAttr.size() == 0) ) {
			node.lstStringAttr = null;
			checkAndLoadStringAtrs(node);
		}
		
		Preconditions.checkArgument( (0 <= index) && (index < node.lstStringAttr.size()) , "ProfileModel.getStringAttr() : index out of bound");
		StringAttrNode obj = null;
		try {
			obj = cacheStringAttr.get(node.lstStringAttr.get(index));
		} catch (ExecutionException e) {
			e.printStackTrace();
			obj = null;
		}
		return obj;		
	}
	
	private void checkAndLoadStringAtrs(ProfileNode node) {
		if (node == null)
			return;
		if(node.lstStringAttr == null)  {
			loadStringAtrs(node, true);
		}
	}
	
	private void loadStringAtrs(ProfileNode node, boolean flagPreload) {
		if (node == null)
			return;
		node.lstStringAttr = loadChildIDs(sqlLoadStringAttrIDs, pattern, node.profileID);
		if( flagPreload && (node.lstStringAttr.size() != 0) )
			try {
				cacheStringAttr.getAll(node.lstStringAttr);
			} catch (ExecutionException e) {					
				e.printStackTrace(); 
			}
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
	
	private TreeModel competence;
	private LoadingCache<Integer, CompetenceNode> cacheCompetence;
	private LoadingCache<Integer, StringAttrNode> cacheStringAttr;
	private String sqlLoadStringAttrIDs;
	private String pattern;
}