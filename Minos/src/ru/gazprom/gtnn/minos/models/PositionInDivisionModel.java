package ru.gazprom.gtnn.minos.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.cache.LoadingCache;

public class PositionInDivisionModel extends BasicModel {

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
		
		assert((arg instanceof DivisionNode) || (arg instanceof Pair<?, ?>)) :
			"PositionInDivisionModel.isLeaf() : arg have incorrect type";

		if(arg instanceof Pair<?, ?>) 
			return true;

		if(arg instanceof DivisionNode) {
			List<Integer> lst = checkAndLoadPosition( (DivisionNode)arg );
			return (division.isLeaf(arg) && (lst.size() == 0));
		}

		return true;
	}
	
	@Override
	public int getChildCount(Object parent) {
		if(parent == null)
			return 0;
		
		assert((parent instanceof DivisionNode) || (parent instanceof Pair<?, ?>)) : 
					"PositionInDivisionModel.getChildCount() : parent have incorrect type";

		if(parent instanceof Pair<?, ?>) 
			return 0;

		if(parent instanceof DivisionNode) {
			List<Integer> lst = checkAndLoadPosition( (DivisionNode)parent );
			return division.getChildCount(parent) + lst.size() ;
		}
		
		return  0;
	}
	
	@Override
	public Object getChild(Object parent, int index) {
		if(parent == null)
			return null;
		
		assert(parent instanceof DivisionNode) :
			"PositionInDivisionModel.getChild() : parent have incorrect type";
		
		if(parent instanceof DivisionNode) {
			DivisionNode node = (DivisionNode)parent;
			List<Integer> lst = checkAndLoadPosition(node);

			Object obj =  null;
			try {
				if(flagPositionBeforeSubDivision) {
					obj = ( ((0 <= index) && (index < lst.size())) ? new Pair<Integer, PositionNode>(node.divisionID, cachePosition.get(lst.get(index)))
							: division.getChild(parent, index - lst.size()) );
				} else { 
					obj = (((0 <= index) && (index < division.getChildCount(parent))) ? division.getChild(parent, index)						
							: new Pair<Integer, PositionNode>( node.divisionID, cachePosition.get(lst.get(index - division.getChildCount(parent))) ) );
				}
			} catch(Exception e) {
				e.printStackTrace();
				obj = null;
			}
			return obj;			
		}
		
		return null;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
	        return -1;

		assert((parent instanceof DivisionNode) && 
				((child instanceof Pair<?, ?>) || (child instanceof DivisionNode)) ) : 
					"PositionInDivisionModel.getIndexOfChild() : parent have incorrect type";

		if(parent instanceof Pair<?, ?>)
			return -1;
		
		if(parent instanceof DivisionNode) {
			DivisionNode node = (DivisionNode)parent;
			List<Integer> lst = checkAndLoadPosition(node);
			
			
			if(child instanceof DivisionNode) {
				int ind = division.getIndexOfChild(parent, child);
				return (flagPositionBeforeSubDivision ? lst.size() + ind : ind);
			} 
			
			if(child instanceof Pair<?, ?>) {
				@SuppressWarnings("unchecked")
				Pair<Integer, PositionNode> p = (Pair<Integer, PositionNode>)child;

				int ind = -1;
				for(int i = 0; i < lst.size(); i++) {
					if(lst.get(i) == p.getSecond().positionID) {
						ind = i;
						break;
					}
				}
				if(ind == -1)
					return ind;		
				
				return (flagPositionBeforeSubDivision ? ind : ind + division.getChildCount(parent));
			}			
		}

		return -1;		
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