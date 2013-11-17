package ru.gazprom.gtnn.minos.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;

import com.google.common.cache.LoadingCache;

public class PositionInDivisionModel extends BasicModel implements TreeModel {

	public PositionInDivisionModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, PositionNode> cachePosition,
			TreeModel division,			
			String sqlLoadPositionIDsForDivision,			
			String patternParentID,
			boolean flagPositionBeforeSubDivision ) {			
		super(kdb);				
		this.cachePosition = cachePosition;
		this.division = division;
		this.sqlLoadPositionIDsForDivision = sqlLoadPositionIDsForDivision;
		this.patternParentID = patternParentID;
		this.flagPositionBeforeSubDivision = flagPositionBeforeSubDivision;
		positionsInDivision = new HashMap<>();
	}		
	
	@Override
	public Object getRoot() {		
		return division.getRoot();
	}

	@Override
	public boolean isLeaf(Object arg) {
		if(arg == null)
			return true;
		
		assert((arg instanceof DivisionNode) || (arg instanceof PositionNode)) :
			"PositionInDivisionModel.isLeaf() : arg have incorrect type";

		if(arg instanceof PositionNode) 
			return true;

		DivisionNode node = (DivisionNode)arg;
		List<Integer> lst = checkAndLoadPosition(node);
	

		return (division.isLeaf(arg) && (lst.size() == 0));
	}
	
	@Override
	public int getChildCount(Object parent) {
		if(parent == null)
			return 0;
		
		assert((parent instanceof DivisionNode) || (parent instanceof PositionNode)) : 
					"PositionInDivisionModel.getChildCount() : parent have incorrect type";

		if(parent instanceof PositionNode) 
			return 0;

		DivisionNode node = (DivisionNode)parent;
		List<Integer> lst = checkAndLoadPosition(node);
		
		return division.getChildCount(parent) + lst.size();
	}
	
	@Override
	public Object getChild(Object parent, int index) {
		if(parent == null)
			return null;
		
		assert(parent instanceof DivisionNode) :
			"PositionInDivisionModel.getChild() : parent have incorrect type";
		
		DivisionNode node = (DivisionNode)parent;
		List<Integer> lst = checkAndLoadPosition(node);

		Object obj =  null;
		try {
			if(flagPositionBeforeSubDivision) {
				obj = ( ((0 <= index) && (index < lst.size())) ? cachePosition.get(lst.get(index))
						: division.getChild(parent, index - lst.size()) );
			} else { 
				obj = (((0 <= index) && (index < division.getChildCount(parent))) ? division.getChild(parent, index)						
						: cachePosition.get(lst.get(index - division.getChildCount(parent))) );
			}
		} catch(Exception e) {
			e.printStackTrace();
			obj = null;
		}
		return obj;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
	        return -1;

		assert((parent instanceof DivisionNode) && 
				((child instanceof PositionNode) || (child instanceof DivisionNode)) ) : 
					"PositionInDivisionModel.getIndexOfChild() : parent have incorrect type";

		if(parent instanceof PositionNode)
			return -1;
		
		DivisionNode node = (DivisionNode)parent;
		List<Integer> lst = checkAndLoadPosition(node);
		
		int ind = -1;
		if(child instanceof DivisionNode) {
			ind = division.getIndexOfChild(parent, child);
			return (flagPositionBeforeSubDivision ? lst.size() + ind : ind);
		} 
		
		PositionNode nodeChild = (PositionNode) child;		
		for(int i = 0; i < lst.size(); i++) {
			if(lst.get(i) == nodeChild.positionID) {
				ind = i;
				break;
			}
		}
		if(ind == -1)
			return ind;		
		
		return (flagPositionBeforeSubDivision ? ind : ind + division.getChildCount(parent));
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
	
	private List<Integer> checkAndLoadPosition(DivisionNode dn) {
		List<Integer> lst = positionsInDivision.get(dn.divisionID);
		if(lst == null) {
			lst = loadChildIDs(sqlLoadPositionIDsForDivision, patternParentID, dn.divisionID);
			positionsInDivision.put(dn.divisionID, lst);
		}		
		return lst;
	}
	
	private LoadingCache<Integer, PositionNode> cachePosition;
	private TreeModel division;
	private String sqlLoadPositionIDsForDivision;
	private String patternParentID;	
	private boolean flagPositionBeforeSubDivision;
	private Map<Integer, List<Integer>> positionsInDivision;
}