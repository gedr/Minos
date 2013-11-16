package ru.gazprom.gtnn.minos.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;

import com.google.common.cache.LoadingCache;

public class PersonInDivisionModel extends BasicModel implements TreeModel {

	public PersonInDivisionModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, PersonNode> cachePerson,
			TreeModel division,			
			String sqlLoadPersonIDsForDivision,			
			String pattern,
			boolean flagPersonBeforeSubDivision ) {			
		super(kdb);				
		this.cachePerson = cachePerson;
		this.division = division;
		this.sqlLoadPersonIDsForDivision= sqlLoadPersonIDsForDivision; 
		this.pattern = pattern;
		this.flagPersonBeforeSubDivision = flagPersonBeforeSubDivision;
		personsInDivisions = new HashMap<>();
	}		

	@Override
	public Object getRoot() {
		return division.getRoot();
	}
	
	@Override
	public boolean isLeaf(Object arg) {		
		if(arg == null)
			return true;
		
		assert((arg instanceof DivisionNode) || (arg instanceof PersonNode)) :
			"PersonInDivisionModel.isLeaf() : arg have incorrect type";

		if(arg instanceof PersonNode) 
			return true;

		DivisionNode node = (DivisionNode)arg;
		List<Integer> lst = checkAndLoadPerson(node);
	
		return (division.isLeaf(arg) && (lst.size() == 0));
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent == null)
			return 0;

		assert((parent instanceof DivisionNode) || (parent instanceof PersonNode)) : 
			"PersonInDivisionModel.getChildCount() : parent have incorrect type";

		if(parent instanceof PersonNode) 
			return 0;

		DivisionNode node = (DivisionNode)parent;
		List<Integer> lst = checkAndLoadPerson(node);

		return division.getChildCount(parent) + lst.size();
	}

	@Override
	public Object getChild(Object parent, int index) {
		if(parent == null)
			return null;

		assert(parent instanceof DivisionNode) :
			"PersonInDivisionModel.getChild() : parent have incorrect type";

		DivisionNode node = (DivisionNode)parent;
		List<Integer> lst = checkAndLoadPerson(node);

		System.out.println(node);
		System.out.println(lst);
		Object obj =  null;
		try {
			if(flagPersonBeforeSubDivision) {
				if( (0 <= index) && (index < lst.size()) )  {
					System.out.println("cachePerson.get(" + lst.get(index) + ")");							
					obj = cachePerson.get(lst.get(index));
				} else {
					System.out.println("division.getChild(parent, index - lst.size())");
					obj = division.getChild(parent, index - lst.size()) ;
				}
				
				/*
				obj = ( ((0 <= index) && (index < lst.size())) ? cachePerson.get(lst.get(index))
						: );
						
						*/
			} else { 
				obj = (((0 <= index) && (index < division.getChildCount(parent))) ? division.getChild(parent, index)						
						: cachePerson.get(lst.get(index - division.getChildCount(parent))) );
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
				((child instanceof PersonNode) || (child instanceof DivisionNode)) ) : 
					"PositionInDivisionModel.getIndexOfChild() : parent have incorrect type";

		if(parent instanceof PersonNode)
			return -1;

		DivisionNode node = (DivisionNode)parent;
		List<Integer> lst = checkAndLoadPerson(node);

		int ind = -1;
		if(child instanceof DivisionNode) {
			ind = division.getIndexOfChild(parent, child);
			return (flagPersonBeforeSubDivision ? lst.size() + ind : ind);
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

		return (flagPersonBeforeSubDivision ? ind : ind + division.getChildCount(parent));
	}

	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	private List<Integer> checkAndLoadPerson(DivisionNode dn) {
		List<Integer> lst = personsInDivisions.get(dn.divisionID);
		if(lst == null) {
			lst = loadChildIDs(sqlLoadPersonIDsForDivision, pattern, dn.divisionID);
			personsInDivisions.put(dn.divisionID, lst);
		}	
		return lst;
	}

	private LoadingCache<Integer, PersonNode> cachePerson;
	private TreeModel division;
	private String sqlLoadPersonIDsForDivision;
	private String pattern;
	private boolean flagPersonBeforeSubDivision;	
	private Map<Integer, List<Integer>> personsInDivisions;	
}