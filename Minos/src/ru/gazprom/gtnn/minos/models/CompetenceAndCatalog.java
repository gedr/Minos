package ru.gazprom.gtnn.minos.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.cache.LoadingCache;

public class CompetenceAndCatalog extends BasicModel {

	public CompetenceAndCatalog(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, CompetenceNode> cacheCompetence,
			BasicModel catalog,			
			BasicModel competence,
			String sqlLoadCompetenceIDsForCatalog,			
			String pattern,
			boolean flagCompetenceBeforeCatalog ) {			
		super(kdb);				
		this.cacheCompetence = cacheCompetence;
		this.catalog = catalog;
		this.competence = competence;
		this.sqlLoadCompetenceIDsForCatalog= sqlLoadCompetenceIDsForCatalog; 
		this.pattern = pattern;
		this.flagCompetenceBeforeCatalog = flagCompetenceBeforeCatalog;
		competencesInCatalog = new HashMap<>();
	}		

	@Override
	public Object getRoot() {		
		return catalog.getRoot();
	}

	@Override
	public boolean isLeaf(Object arg) {		
		if(arg == null)
			return true;

		if((arg instanceof CompetenceNode) || 
				(arg instanceof Pair) || 
				(arg instanceof IndicatorNode)) 
			return competence.isLeaf(arg);

		if(arg instanceof CatalogNode) {
			CatalogNode node = (CatalogNode)arg;
			List<Integer> lst = checkAndLoadCompetence(node);
			return ( catalog.isLeaf(arg) && (lst.size() == 0) );			
		}

		return true;
	}

	@Override
	public int getChildCount(Object parent) {
		if(parent == null)
			return 0;
		
		if((parent instanceof CompetenceNode) || 
				(parent instanceof Pair) || 
				(parent instanceof IndicatorNode)) 
			return competence.getChildCount(parent);
		
		if(parent instanceof CatalogNode) {			
			List<Integer> lst = checkAndLoadCompetence((CatalogNode)parent);
			return lst.size() + catalog.getChildCount(parent);			
		}

		return 0;
	}
	
	@Override
	public Object getChild(Object parent, int index) {
		if(parent == null)
			return null;

		if((parent instanceof CompetenceNode) || 
				(parent instanceof Pair) || 
				(parent instanceof IndicatorNode)) 
			return competence.getChild(parent, index);
		
		Object obj =  null;
		if(parent instanceof CatalogNode) {
			try {				
				List<Integer> lst = checkAndLoadCompetence((CatalogNode)parent);
				
				if(flagCompetenceBeforeCatalog) {
					obj = ( ((0 <= index) && (index < lst.size())) ? cacheCompetence.get(lst.get(index))
							: catalog.getChild(parent, index - lst.size()) );
				} else { 
					int countSubCatalog = catalog.getChildCount(parent);
					obj = (((0 <= index) && (index < countSubCatalog)) ? catalog.getChild(parent, index)						
							: cacheCompetence.get(lst.get(index - countSubCatalog)) );
				}
			} catch(Exception e) {
				e.printStackTrace();
				obj = null;
			}			
		}
		
		return obj;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
			return -1;
		
		if((parent instanceof CompetenceNode) || 
				(parent instanceof Pair) || 
				(parent instanceof IndicatorNode)) 
			return competence.getIndexOfChild(parent, child);
			
		List<Integer> lst = checkAndLoadCompetence((CatalogNode)parent);
		
		if((parent instanceof CatalogNode) && (child instanceof CatalogNode)) 
			return ( flagCompetenceBeforeCatalog ? catalog.getIndexOfChild(parent, child) + lst.size()
												 : catalog.getIndexOfChild(parent, child) );
						
		if((parent instanceof CatalogNode) && (child instanceof CompetenceNode)) {
			CompetenceNode node = (CompetenceNode)child;
			int result = -1;
			boolean fOk = false;
			try {
				for (Integer it : lst) {
					result++;
					if (node.competenceID == cacheCompetence.get(it).competenceID) {
						fOk = true;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (!fOk ? -1 : (result += flagCompetenceBeforeCatalog ? 0 : catalog.getChildCount(parent)));
		}

		return -1;
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void reload() {
		System.out.println("CompetenceAndCatalog.reload()");		
		catalog.reload();
		competencesInCatalog.clear();
		competence.reload();
	}

	@Override
	public void add(Object obj, TreePath path) throws Exception {
		if(obj instanceof CatalogNode) {
			catalog.add(obj, path);			
		}

		if(obj instanceof CompetenceNode) {
			competence.add(obj, path);			
		}

	}


	private List<Integer> checkAndLoadCompetence(CatalogNode cn) {
		List<Integer> lst = competencesInCatalog.get(cn.catalogID);
		if(lst == null) {
			lst = loadChildIDs(sqlLoadCompetenceIDsForCatalog, pattern, cn.catalogID);
			competencesInCatalog.put(cn.catalogID, lst);			
		}	
		return lst;
	}
	
	private LoadingCache<Integer, CompetenceNode> cacheCompetence;
	private BasicModel catalog;
	private BasicModel competence;
	private String sqlLoadCompetenceIDsForCatalog;
	private String pattern;
	private boolean flagCompetenceBeforeCatalog;
	private Map<Integer, List<Integer>> competencesInCatalog;
}
