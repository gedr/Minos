package ru.gazprom.gtnn.minos.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.cache.LoadingCache;

public class PersonInDivisionModel extends BasicModel {

	public PersonInDivisionModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, PersonNode> cachePerson,
			LoadingCache<Integer, PositionNode> cachePosition,
			TreeModel division,			
			String sqlLoadPersonIDsForDivision,			
			String pattern,
			boolean flagPersonBeforeSubDivision ) {			
		super(kdb);				
		this.cachePerson = cachePerson;
		this.cachePosition = cachePosition;
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
		List<Object> lst = checkAndLoadPerson(node);
	
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
		List<Object> lst = checkAndLoadPerson(node);

		return division.getChildCount(parent) + lst.size();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public Object getChild(Object parent, int index) {
		if(parent == null)
			return null;

		assert(parent instanceof DivisionNode) :
			"PersonInDivisionModel.getChild() : parent have incorrect type";

		DivisionNode node = (DivisionNode)parent;
		List<Object> lst = checkAndLoadPerson(node);

		Object obj =  null;
		try {
			if(flagPersonBeforeSubDivision) {			
				
				obj = ( ((0 <= index) && (index < lst.size())) ? cachePerson.get( ((Pair<Integer, Integer>) lst.get(index)).getFirst() )
						: division.getChild(parent, index - lst.size()) );
			} else {
							
				obj = (((0 <= index) && (index < division.getChildCount(parent))) ? division.getChild(parent, index)						
						: cachePerson.get( ((Pair<Integer, Integer>)lst.get(index - division.getChildCount(parent))).getFirst() ) );
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
		
		if(parent instanceof DivisionNode) {
			DivisionNode node = (DivisionNode)parent;
			List<Object> lst = checkAndLoadPerson(node);

			if(child instanceof DivisionNode) {				
				return (!flagPersonBeforeSubDivision ? division.getIndexOfChild(parent, child)
										: division.getIndexOfChild(parent, child) + lst.size() );
			} 
			
			if(child instanceof PersonNode) {
				PersonNode nodeChild = (PersonNode) child;

				try {
					for (int i = 0; i < lst.size(); i++) {
						@SuppressWarnings("unchecked")
						Pair<Integer, Integer> p = (Pair<Integer, Integer>) lst.get(i);
						if (cachePerson.get(p.getFirst()).personID == nodeChild.personID)
							return (flagPersonBeforeSubDivision ? i : division
									.getIndexOfChild(parent, child) + i);
					}
				} catch (Exception e) {
					e.printStackTrace();					
				}
			}
		}
		return -1;
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	private List<Object> checkAndLoadPerson(DivisionNode dn) {
		List<Object> lst = personsInDivisions.get(dn.divisionID);
		if(lst == null) {
			lst = loadChildrenIDs(sqlLoadPersonIDsForDivision, pattern, dn.divisionID);			
			personsInDivisions.put(dn.divisionID, lst);

			// load person use one request
			if(lst.size() != 0) {
				List<Integer> personsIDs = new ArrayList<Integer>();
				try {
					for (Object obj : lst) {
						@SuppressWarnings("unchecked")
						Pair<Integer, Integer> p = (Pair<Integer, Integer>) obj;
						personsIDs.add(p.getFirst());
						cachePerson.getAll(personsIDs);
						personsIDs.clear();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			 
			
			try {
				for (Object obj : lst) {
					@SuppressWarnings("unchecked")
					Pair<Integer, Integer> p = (Pair<Integer, Integer>) obj;
					cachePerson.get(p.getFirst()).personPosition = cachePosition
							.get(p.getSecond());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		return lst;
	}

	private LoadingCache<Integer, PersonNode> cachePerson;
	private LoadingCache<Integer, PositionNode> cachePosition;

	private TreeModel division;
	private String sqlLoadPersonIDsForDivision;
	private String pattern;
	private boolean flagPersonBeforeSubDivision;	
	private Map<Integer, List<Object>> personsInDivisions;	
}