package ru.gazprom.gtnn.minos.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.LevelNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;
import ru.gedr.util.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

public class CompetenceModel extends BasicModel {

	public CompetenceModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, CompetenceNode> cacheCompetence,			
			LoadingCache<Integer, LevelNode> cacheLevel,
			LoadingCache<Integer, IndicatorNode> cacheIndicator,
			String sqlLoadOneCompetenceID,  // for test
						
			String sqlLoadIndicatorIDs,			
			String pattern) {			
		super(kdb);		

		this.cacheCompetence = cacheCompetence;			
		this.cacheLevel = cacheLevel;
		this.cacheIndicator = cacheIndicator;
		
		this.sqlLoadOneCompetenceID = sqlLoadOneCompetenceID;		
		this.sqlLoadIndicatorIDs = sqlLoadIndicatorIDs;
		this.pattern = pattern;
		
		stat = new HashMap<>();
	}		

	
	@Override
	public Object getRoot() {
		// Method wrote for test
		CompetenceNode cn = null;
		try {
			TableKeeper tk = kdb.selectRows(sqlLoadOneCompetenceID);
			Preconditions.checkState(((tk.getRowCount() == 1) && (tk.getColumnCount() == 1)),						
					"CompetenceModel.getRoot() : selectRows() return incorrect row and column count (" + tk.getRowCount() + " , " + tk.getColumnCount() + ")");
			cn = cacheCompetence.get((Integer) tk.getValue(1, 1));
			checkAndLoadIndicators(cn);
		} catch (Exception e) {
			e.printStackTrace();
			cn = null;
		}
		return cn;
	}

	@Override
	public boolean isLeaf(Object arg) {
		if(arg == null)
			return true;
		
		assert ( (arg instanceof CompetenceNode) || (arg instanceof IndicatorNode) || (arg instanceof Pair) ) : 
			"CompetenceModel.isLeaf() : arg have incorrect type";
		
		if(arg instanceof CompetenceNode)
			return false;

		if(arg instanceof IndicatorNode)
			return true;
	
		if(arg instanceof Pair) {
			@SuppressWarnings("unchecked")
			Pair<Integer, LevelNode> p = (Pair<Integer, LevelNode>) arg;
			
			boolean fOk = true;
			try {
				CountIndicatorByLevel cibl = stat.get(p.getFirst());
				if(cibl != null)
					fOk = cibl.level[ p.getSecond().levelID - 1 ] == 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return fOk;
		}		
		return true;
	}
	
	@Override
	public int getChildCount(Object parent) {
		if(parent == null)
			return 0;

		assert ( (parent instanceof CompetenceNode) || (parent instanceof IndicatorNode) || (parent instanceof Pair) ) :
			"CompetenceModel.getChildCount() : parent have incorrect type";
			
		if(parent instanceof IndicatorNode) 
			return 0;
		
		if(parent instanceof CompetenceNode)
			return 5;				
		
		if(parent.getClass() == Pair.class) { // this is level (first is competence.id, second is LevelNode object
			@SuppressWarnings("unchecked")
			Pair<Integer, LevelNode> p = (Pair<Integer, LevelNode>) parent;			

			int ret = 0;
			try {
				CountIndicatorByLevel cibl = stat.get(p.getFirst());
				if(cibl != null)
					ret = cibl.level[ p.getSecond().levelID - 1 ];
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
		}
		return 0;
	}
	
	@Override
	public Object getChild(Object parent, int index) {
		if(parent == null)
			return null;

		assert ( (parent instanceof CompetenceNode) || (parent instanceof Pair) ) :
			"CompetenceModel.getChild() : parent have incorrect type";
		
		if( !((parent instanceof CompetenceNode) || (parent instanceof Pair)) )
			return null;
		
		Object obj = null;
		try {
			if ((parent instanceof CompetenceNode) && (0 <= index) && (index < 5)) 
					obj = new Pair<Integer, LevelNode>(((CompetenceNode) parent).competenceID, cacheLevel.get(index + 1));
			
			if (parent instanceof Pair) {
				@SuppressWarnings("unchecked")
				Pair<Integer, LevelNode> p = (Pair<Integer, LevelNode>) parent;
				CompetenceNode cn = cacheCompetence.get(p.getFirst());
				checkAndLoadIndicators(cn);
				
				int offs = -1;
				for(Integer it : cn.indicators) {
					if(cacheIndicator.get(it).indicatorLevelID == p.getSecond().levelID)
						offs++;
					if(offs == index) {
						obj = cacheIndicator.get(it);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if(parent == null || child == null)
            return -1;

		assert ( ((parent instanceof CompetenceNode) && (child instanceof Pair) ) ||
				((parent instanceof Pair) && (child instanceof IndicatorNode) ) ) :
			"CompetenceModel.getIndexOfChild() : parent or child have incorrect type";
		
		if((parent instanceof CompetenceNode) && (child instanceof Pair)) {
			@SuppressWarnings("unchecked")
			Pair<Integer, LevelNode> p = (Pair<Integer, LevelNode>) child;
			return p.getSecond().levelID - 1;
		}
		
		if((parent instanceof Pair) && (child instanceof IndicatorNode)) {			
			try {
				@SuppressWarnings("unchecked")
				Pair<Integer, LevelNode> p = (Pair<Integer, LevelNode>) parent;
				CompetenceNode cn = cacheCompetence.get(p.getFirst());
				checkAndLoadIndicators(cn);
			
				IndicatorNode nodeChild = (IndicatorNode)child;
				int offs = -1;
				boolean fOk = false;
				for(Integer it : cn.indicators) {
					IndicatorNode inode = cacheIndicator.get(it);
					if(inode.indicatorLevelID == nodeChild.indicatorLevelID)
						offs++;
					if (inode.indicatorID == nodeChild.indicatorID) {
						fOk = true;
						break;				
					}
				}
				return fOk ? offs : -1;
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}		
		return -1;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reload() {
		
		cacheCompetence.invalidateAll();
		cacheIndicator.invalidateAll();
		
		stat.clear();
		super.reload();
	}

	
	@Override
	public void add(Object obj, TreePath path) throws Exception {
		if(obj instanceof CompetenceNode) {
			CompetenceNode cni = (CompetenceNode)obj;		
			Object []nodes = path.getPath();
			for(int i = nodes.length; i > 0; i--) {				
				if(nodes[i - 1] instanceof CatalogNode) {
					CatalogNode cnt = (CatalogNode) nodes[i - 1];					
					cni.competenceCatalogID = cnt.catalogID;
					cni.competenceIncarnatio = 0;
					int key = kdb.insertRow(true, "COMPETENCE", 
							Arrays.asList(new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "name", cni.competenceName),
									new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "description", cni.competenceDescr),
									new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.INTEGER, "item", cni.competenceItem),
									new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.INTEGER, "catalog_id", cni.competenceCatalogID),
									new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.INTEGER, "incarnatio", cni.competenceIncarnatio),
									new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.INTEGER, "chain_number", cni.competenceChainNumber),
									new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.DATE, "date_remove", cni.competenceRemove),
									new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.DATE, "date_create", cni.competenceCreate)
									));
					kdb.updateRow("COMPETENCE", 
							Arrays.asList(new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.INTEGER, "incarnatio", key)), 
							new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.INTEGER, "id", key));
					break;
				}				
			}			
		}
	}
	
	
	private void checkAndLoadIndicators(CompetenceNode cn) {
		if(cn == null)
			return;

		if(cn.indicators != null)
			return;
		
		cn.indicators = loadChildIDs(sqlLoadIndicatorIDs, pattern, cn.competenceIncarnatio);
		try {
		if(cn.indicators.size() != 0) {
			cacheIndicator.getAll(cn.indicators);
			
			CountIndicatorByLevel cibl = stat.get(cn.competenceID);
			if(cibl != null) {
				Arrays.fill(cibl.level, 0);
			} else {
				cibl = new CountIndicatorByLevel();
				stat.put(cn.competenceID, cibl);								
			}
			for(Integer it : cn.indicators) 
				cibl.level[ cacheIndicator.get(it).indicatorLevelID - 1 ] += 1;			
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private String sqlLoadOneCompetenceID;
	private String sqlLoadIndicatorIDs;
	private String pattern;
	
	private LoadingCache<Integer, CompetenceNode> cacheCompetence;			
	private LoadingCache<Integer, LevelNode> cacheLevel;
	private LoadingCache<Integer, IndicatorNode> cacheIndicator;

	private class CountIndicatorByLevel {
		public int level[] = {0, 0, 0, 0, 0};
	}
	private Map<Integer, CountIndicatorByLevel> stat; 

}

