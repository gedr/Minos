package ru.gazprom.gtnn.minos.models;

import java.util.Map;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;

public class DivisionModel extends BasicModel implements TreeModel {

	public DivisionModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, DivisionNode> cacheDivision,
			String sqlLoadRootDivisionID,
			String sqlLoadSubDivisionIDs,			
			String patternParentID) {			
		super(kdb);		
		this.cacheDivision = cacheDivision;
		this.sqlLoadSubDivisionIDs = sqlLoadSubDivisionIDs;
		this.patternParentID = patternParentID;
		this.sqlLoadRootDivisionID = sqlLoadRootDivisionID;
	}		
	
	@Override
	public Object getRoot() {
		if(root == null) {			
			try {
				TableKeeper tk = kdb.selectRows(sqlLoadRootDivisionID);
				Preconditions.checkState(((tk.getRowCount() == 1) && (tk.getColumnCount() == 1)),						
						"DivisionModel.getRoot() : selectRows() return incorrect row and column count (" + tk.getRowCount() + " , " + tk.getColumnCount() + ")");
								
				root = cacheDivision.get((Integer) tk.getValue(0, 1));				
				root.subDivisions = loadChildIDs(sqlLoadSubDivisionIDs, patternParentID, root.divisionID);
				if(root.subDivisions.size() != 0)
					cacheDivision.getAll(root.subDivisions);
			} catch (Exception e) {
				e.printStackTrace();
				root = null;
			}			
		}
		return root;
	}

	@Override
	public boolean isLeaf(Object arg) {
		assert arg instanceof DivisionNode : "DivisionTreeModel.isLeaf() : arg have incorrect type";
		if(!(arg instanceof DivisionNode))
			return true;		
		
		DivisionNode node = (DivisionNode)arg;
		if(node.subDivisions == null)  {
			node.subDivisions = loadChildIDs(sqlLoadSubDivisionIDs, patternParentID, node.divisionID);
			if(node.subDivisions.size() != 0)
				cacheDivision.getAll(node.subDivisions);
		}
		
		return (node.subDivisions.size() == 0);
	}

	@Override
	public int getChildCount(Object arg) {
		assert arg instanceof DivisionNode : "DivisionTreeModel.getChildCount() : arg have incorrect type";
		if(!(arg instanceof DivisionNode))
			return 0;
		
		DivisionNode node = (DivisionNode)arg;
		if(node.subDivisions == null)  {
			node.subDivisions = loadChildIDs(sqlLoadSubDivisionIDs, patternParentID, node.divisionID);
			if(node.subDivisions.size() != 0)
				cacheDivision.getAll(node.subDivisions);
		}

		return node.subDivisions.size();
	}

	@Override
	public Object getChild(Object arg, int index) {
		assert arg instanceof DivisionNode : "DivisionTreeModel.getChild() : arg have incorrect type";

		if(!(arg instanceof DivisionNode))
			return null;

		DivisionNode node = (DivisionNode)arg;
		if(node.subDivisions == null)  {
			node.subDivisions = loadChildIDs(sqlLoadSubDivisionIDs, patternParentID, node.divisionID);
			if(node.subDivisions.size() != 0)
				cacheDivision.getAll(node.subDivisions);
		}
		
		assert ((0 <= index) && (index < node.subDivisions.size())) : "DivisionTreeModel.getChild() : index out of range";
		if((index < 0) || (index >= node.subDivisions.size()))
			return null;		
		
		Object obj = null;
		try {
			obj = cacheDivision.get(node.subDivisions.get(index));
		} catch (Exception e) {
			e.printStackTrace();
			obj = null;
		}
		return obj;
	}
	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
            return -1;
		
		assert parent instanceof DivisionNode : "DivisionTreeModel.getIndexOfChild() : parent have incorrect type";
		assert child instanceof DivisionNode : "DivisionTreeModel.getIndexOfChild() : parent have incorrect type";
		if( !( (parent instanceof DivisionNode) && (child instanceof DivisionNode) ) )
			return -1;
		
		DivisionNode nodeParent = (DivisionNode)parent;
		DivisionNode nodeChild = (DivisionNode)child;

		if(nodeParent.subDivisions == null)  {
			nodeParent.subDivisions = loadChildIDs(sqlLoadSubDivisionIDs, patternParentID, nodeParent.divisionID);
			if(nodeParent.subDivisions.size() != 0)
				cacheDivision.getAll(nodeParent.subDivisions);
		}
		
		int offs = -1;
		for(int i = 0; i < nodeParent.subDivisions.size(); i++) {
			if(nodeChild.divisionID == nodeParent.subDivisions.get(i)) {
				offs = i;
				break;
			}
		}
		
		return offs;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
		
	}

	private DivisionNode root = null;
	private LoadingCache<Integer, DivisionNode> cacheDivision;
	private String sqlLoadSubDivisionIDs;
	private String patternParentID;
	private String sqlLoadRootDivisionID;
}