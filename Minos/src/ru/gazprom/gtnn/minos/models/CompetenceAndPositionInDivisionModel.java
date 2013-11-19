package ru.gazprom.gtnn.minos.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

public class CompetenceAndPositionInDivisionModel  extends BasicModel {

	private TreeModel positionInDivision;
	private String sqlLoadRefCompetence;
	private String[] pattern;
	
	private LoadingCache<Integer, CompetenceNode> cacheCompetence;
	private TreeModel competence;
	
	public CompetenceAndPositionInDivisionModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, CompetenceNode> cacheCompetence,
			TreeModel positionInDivision,			
			TreeModel competence,
			String sqlLoadRefCompetence,			
			String[] pattern ) {			
		super(kdb);				
		this.cacheCompetence = cacheCompetence;
		this.positionInDivision = positionInDivision;
		this.competence = competence;
		this.sqlLoadRefCompetence = sqlLoadRefCompetence;
		this.pattern = pattern;
		
		map = new HashMap<>();
	}		

	
	@Override
	public Object getRoot() {		
		return positionInDivision.getRoot();
	}

	@Override
	public boolean isLeaf(Object arg) {
		if(arg == null)
			return true;
		
		if(arg instanceof DivisionNode) 
			return positionInDivision.isLeaf(arg);
		
		if(arg instanceof CompetenceNode)
			return competence.isLeaf(arg);
		
		if(arg instanceof Pair<?, ?>) {
			@SuppressWarnings("unchecked")
			Pair<Integer, PositionNode> p = (Pair<Integer, PositionNode>)arg;
			List<Integer> lst = loadRefCompetence(p.getFirst(), p.getSecond().positionID);
			return lst.size() == 0;
		}

		return true;
	}
	
	@Override
	public int getChildCount(Object parent) {
		if(parent == null)
			return 0;

		if(parent instanceof DivisionNode) 
			return positionInDivision.getChildCount(parent);

		if(parent instanceof CompetenceNode)
			return competence.getChildCount(parent);

		
		if(parent instanceof Pair<?, ?>) { 
			@SuppressWarnings("unchecked")
			Pair<Integer, PositionNode> p = (Pair<Integer, PositionNode>)parent;
			List<Integer> lst = loadRefCompetence(p.getFirst(), p.getSecond().positionID);
			return lst.size();
		}

		return  0;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if(parent == null)
			return null;

		if(parent instanceof DivisionNode) 
			return positionInDivision.getChild(parent, index);

		if(parent instanceof CompetenceNode)
			return competence.getChild(parent, index);
		
		if(parent instanceof Pair<?, ?>) { 
			@SuppressWarnings("unchecked")
			Pair<Integer, PositionNode> p = (Pair<Integer, PositionNode>)parent;
			List<Integer> lst = loadRefCompetence(p.getFirst(), p.getSecond().positionID);
			Object ob = null;
			try {
				ob = cacheCompetence.get( lst.get(index) );
				
			} catch(Exception e) {
				e.printStackTrace();
				ob = null;
			}
				
			return ob;
		}
		
		return null;
	}
	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
	        return -1;

		if(parent instanceof DivisionNode) 
			return positionInDivision.getIndexOfChild(parent, child);

		if(parent instanceof CompetenceNode)
			return competence.getIndexOfChild(parent, child);

		if( (parent instanceof Pair<?, ?>) && (child instanceof CompetenceNode) ) {
			@SuppressWarnings("unchecked")
			Pair<Integer, PositionNode> p = (Pair<Integer, PositionNode>)parent;
			List<Integer> lst = loadRefCompetence(p.getFirst(), p.getSecond().positionID);
			
			CompetenceNode node = (CompetenceNode)child;
			int ind = 0;
			try {
				for (Integer it : lst) {
					if (node.competenceID == cacheCompetence.get(it).competenceID)
						break;

					ind++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ind < lst.size() ? ind : -1;	
		}
		
		return -1;		
	}
	
	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}	

	private List<Integer> loadRefCompetence(int divisionID, int positionID) {
		Pair<Integer, Integer> p = new Pair<>(divisionID, positionID);
		List<Integer> lst = map.get(p);
		if(lst == null) {
			String request = kdb.makeSQLString(sqlLoadRefCompetence, pattern[0], String.valueOf(divisionID));
			Preconditions.checkNotNull(request, "CompetenceAndPositionInDivisionModel.loadRefCompetence() : makeSQLString return null");
			lst = loadChildIDs(request, pattern[1], String.valueOf(positionID));
			map.put(p,  lst);
		}

		return lst;
	}
	
	private Map<Pair<Integer, Integer>, List<Integer>> map;

}







