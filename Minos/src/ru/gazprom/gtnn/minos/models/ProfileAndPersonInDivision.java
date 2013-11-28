package ru.gazprom.gtnn.minos.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

public class ProfileAndPersonInDivision extends BasicModel {

	public ProfileAndPersonInDivision(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, ProfileNode> cacheProfile,
			TreeModel personInDivision,			
			TreeModel profile,
			String sqlLoadProfileForPerson,			
			String[] pattern ) {			
		super(kdb);				
		this.cacheProfile = cacheProfile;
		this.personInDivision = personInDivision;
		this.profile = profile;
		this.sqlLoadProfileForPerson = sqlLoadProfileForPerson;
		this.pattern = pattern;
		
		map = new HashMap<>();
	}		

	@Override
	public Object getRoot() {		
		return personInDivision.getRoot();
	}

	@Override
	public boolean isLeaf(Object arg) {
		if(arg == null)
			return true;
		
		if(arg instanceof DivisionNode) 
			return personInDivision.isLeaf(arg);
		
		if( (arg instanceof ProfileNode) ||
				(arg instanceof IndicatorNode) ||
				(arg instanceof Pair<?, ?>) )
			return profile.isLeaf(arg);
		
		if(arg instanceof PersonNode) {
			PersonNode node = (PersonNode)arg;
			List<Integer> lst = cheackAndloadProfile(node.personDivisionID, node.personPositionID);
			return lst.size() == 0;			
		}

		return true;
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent == null)
			return 0;

		if(parent instanceof DivisionNode) 
			return personInDivision.getChildCount(parent);
		
		if( (parent instanceof ProfileNode) ||
				(parent instanceof IndicatorNode) ||
				(parent instanceof Pair<?, ?>) )
			return profile.getChildCount(parent);
		
		if(parent instanceof PersonNode) {
			PersonNode node = (PersonNode)parent;
			List<Integer> lst = cheackAndloadProfile(node.personDivisionID, node.personPositionID);
			return lst.size();			
		}
		
		return 0;
	}

	@Override
	public Object getChild(Object parent, int index) {
		System.out.println("getChild" + parent);
		if(parent == null)
			return null;

		if(parent instanceof DivisionNode) 
			return personInDivision.getChild(parent, index);
		
		if( (parent instanceof ProfileNode) ||
				(parent instanceof IndicatorNode) ||
				(parent instanceof Pair<?, ?>) )
			return profile.getChild(parent, index);
		
		if(parent instanceof PersonNode) {
			PersonNode node = (PersonNode)parent;
			List<Integer> lst = cheackAndloadProfile(node.personDivisionID, node.personPositionID);
			if( (0 <= index) && (index < lst.size()) ) {
				ProfileNode tmp = null;
				try {						
					tmp = cacheProfile.get(lst.get(index));
				} catch (ExecutionException e) {
					tmp = null;
					e.printStackTrace();
				}
				return tmp;
			}			
		}
		
		return null;
	}
	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
			return -1;

		if(parent instanceof DivisionNode) 
			return personInDivision.getIndexOfChild(parent, child);
		
		if( (parent instanceof ProfileNode) ||
				(parent instanceof IndicatorNode) ||
				(parent instanceof Pair<?, ?>) )
			return profile.getIndexOfChild(parent, child);
		
		if(parent instanceof PersonNode) {
			Preconditions.checkArgument(child instanceof ProfileNode, 
					"ProfileAndPersonInDivision.getIndexOfChild() child node must be ProfileNode");
			PersonNode nodePerson = (PersonNode)parent;
			ProfileNode nodeProfile = (ProfileNode)child;
			List<Integer> lst = cheackAndloadProfile(nodePerson.personDivisionID, nodePerson.personPositionID);
			int ind = 0;				
			for(Integer it : lst) {
				if(it == nodeProfile.profileID) 
					break;
				ind++;
			}
			return ( (ind < lst.size()) ? ind : -1);		
		}

		return -1;		
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub
	}

	private List<Integer> cheackAndloadProfile(int divisionID, int positionID) {
		Pair<Integer, Integer> p = new Pair<>(divisionID, positionID);
		List<Integer> lst = map.get(p);
		if(lst == null) {
			String request = kdb.makeSQLString(sqlLoadProfileForPerson, pattern[0], String.valueOf(divisionID));
			Preconditions.checkNotNull(request, "ProfileAndPersonInDivision.cheackAndloadProfile() : makeSQLString return null");
			lst = loadChildIDs(request, pattern[1], String.valueOf(positionID));
			map.put(p, lst);
		}

		return lst;
	}	
	
	private Map<Pair<Integer, Integer>, List<Integer>> map;
	private String sqlLoadProfileForPerson;
	private TreeModel profile;
	private TreeModel personInDivision;
	private String[] pattern;
	private LoadingCache<Integer, ProfileNode> cacheProfile;
}