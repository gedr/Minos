package ru.gazprom.gtnn.minos.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.LevelNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

public class ProfileAndPositionInDivision extends BasicModel {

	public ProfileAndPositionInDivision(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, ProfileNode> cacheProfile,
			TreeModel positionInDivision,			
			TreeModel profile,
			String sqlLoadProfileForPosition,			
			String[] pattern ) {			
		super(kdb);				
		this.cacheProfile = cacheProfile;
		this.positionInDivision = positionInDivision;
		this.profile = profile;
		this.sqlLoadProfileForPosition = sqlLoadProfileForPosition;
		this.pattern = pattern;
		
		map = new HashMap<>();
	}		

	@Override
	public Object getRoot() {		
		// System.out.println("getRoot");
		return positionInDivision.getRoot();
	}

	@Override
	public boolean isLeaf(Object arg) {
		// System.out.println("isLeaf" + arg);
		if(arg == null)
			return true;
		
		if(arg instanceof DivisionNode) 
			return positionInDivision.isLeaf(arg);
		
		if( (arg instanceof ProfileNode) ||
				(arg instanceof IndicatorNode) )
			return profile.isLeaf(arg);
		
		if(arg instanceof Pair<?, ?>) {
			@SuppressWarnings("unchecked")
			Pair<Integer, Object> p = (Pair<Integer, Object>)arg;
			if(p.getSecond() instanceof LevelNode) // this is Level of competence 
				return profile.isLeaf(arg);
			
			if(p.getSecond() instanceof PositionNode) {
				PositionNode pn = (PositionNode) p.getSecond(); 
				List<Integer> lst = cheackAndloadProfile(p.getFirst(), pn.positionID);
				return lst.size() == 0;
			}			
		}

		return true;
	}

	@Override
	public int getChildCount(Object parent) {
		// System.out.println("getChildCount" + parent);
		if(parent == null)
			return 0;

		if(parent instanceof DivisionNode) 
			return positionInDivision.getChildCount(parent);
		
		if( (parent instanceof ProfileNode) ||
				(parent instanceof IndicatorNode) )
			return profile.getChildCount(parent);
		
		if(parent instanceof Pair<?, ?>) {
			@SuppressWarnings("unchecked")
			Pair<Integer, Object> p = (Pair<Integer, Object>)parent;
			if(p.getSecond() instanceof LevelNode) // this is Level of competence 
				return profile.getChildCount(parent);
			
			if(p.getSecond() instanceof PositionNode) {
				PositionNode pn = (PositionNode) p.getSecond(); 
				List<Integer> lst = cheackAndloadProfile(p.getFirst(), pn.positionID);				
				return lst.size();
			}			
		}
		
		return 0;
	}

	@Override
	public Object getChild(Object parent, int index) {
		System.out.println("getChild" + parent);
		if(parent == null)
			return null;

		if(parent instanceof DivisionNode) 
			return positionInDivision.getChild(parent, index);
		
		if( (parent instanceof ProfileNode) ||
				(parent instanceof IndicatorNode) )
			return profile.getChild(parent, index);
		
		if(parent instanceof Pair<?, ?>) {
			@SuppressWarnings("unchecked")
			Pair<Integer, Object> p = (Pair<Integer, Object>)parent;
			if(p.getSecond() instanceof LevelNode) // this is Level of competence 
				return profile.getChild(parent, index);
			
			if(p.getSecond() instanceof PositionNode) {
				PositionNode pn = (PositionNode) p.getSecond(); 
				List<Integer> lst = cheackAndloadProfile(p.getFirst(), pn.positionID);
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
		}
		
		return null;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		System.out.println("getIndexOfChild" + parent);
		if(parent == null || child == null)
			return -1;

		if(parent instanceof DivisionNode) 
			return positionInDivision.getIndexOfChild(parent, child);
		
		if( (parent instanceof ProfileNode) ||
				(parent instanceof IndicatorNode) )
			return profile.getIndexOfChild(parent, child);
		
		if(parent instanceof Pair<?, ?>) {
			@SuppressWarnings("unchecked")
			Pair<Integer, Object> p = (Pair<Integer, Object>)parent;
			if(p.getSecond() instanceof LevelNode) // this is Level of competence 
				return profile.getIndexOfChild(parent, child);
			
			if(p.getSecond() instanceof PositionNode) {
				if( !(child instanceof ProfileNode) )
					return -1;
				ProfileNode nodeProfile = (ProfileNode)child; 
				PositionNode poitionNode = (PositionNode) p.getSecond(); 
				List<Integer> lst = cheackAndloadProfile(p.getFirst(), poitionNode.positionID);

				int ind = 0;				
				for(Integer it : lst) {
					if(it == nodeProfile.profileID) 
						break;
					ind++;
				}
				return ( (ind < lst.size()) ? ind : -1);
			}
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
			String request = kdb.makeSQLString(sqlLoadProfileForPosition, pattern[0], String.valueOf(divisionID));
			Preconditions.checkNotNull(request, "ProfileAndPositionInDivision.cheackAndloadProfile() : makeSQLString return null");
			lst = loadChildIDs(request, pattern[1], String.valueOf(positionID));
			map.put(p, lst);
		}

		return lst;
	}

	private LoadingCache<Integer, ProfileNode> cacheProfile;
	private String sqlLoadProfileForPosition;
	private TreeModel profile;
	private TreeModel positionInDivision;
	private String[] pattern;
	private Map<Pair<Integer, Integer>, List<Integer>> map;
}