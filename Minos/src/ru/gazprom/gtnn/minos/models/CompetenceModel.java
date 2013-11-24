package ru.gazprom.gtnn.minos.models;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;
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
						
			return getCountIndicatorByLevelID(p.getFirst(), p.getSecond().levelID) == 0;
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
			return LevelNode.LEVEL_COUNT;				
		
		if(parent.getClass() == Pair.class) { // this is level (first is competence.id, second is LevelNode object
			@SuppressWarnings("unchecked")
			Pair<Integer, LevelNode> p = (Pair<Integer, LevelNode>) parent;
			return getCountIndicatorByLevelID(p.getFirst(), p.getSecond().levelID);
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
				
		super.reload();
	}

	public void add(CompetenceNode source, CatalogNode dest) throws Exception{
		source.competenceCatalogID = dest.catalogID;
		source.competenceVariant = dest.catalogVariant;
		try {
			source.insert(kdb,
					CompetenceNode.COMPETENCE_NAME | CompetenceNode.COMPETENCE_DESCR | 
					CompetenceNode.COMPETENCE_ITEM | CompetenceNode.COMPETENCE_CATALOG | 
					CompetenceNode.COMPETENCE_INCARNATIO | CompetenceNode.COMPETENCE_CHAIN_NUMBER |
					CompetenceNode.COMPETENCE_REMOVE | CompetenceNode.COMPETENCE_CREATE, 
					true);
			  
		} catch (Exception e) {
			e.printStackTrace();			
			source.competenceID = -1;
			throw e;
		}
	}

	public void add(IndicatorNode source, CompetenceNode dest, boolean flagLoadIndicator) throws Exception {
		source.indicatorCompetenceIncarnatio = dest.competenceIncarnatio;		

		if (flagLoadIndicator) // need load sub catalogs ids
			loadIndicators(dest, false);
		
		boolean fEmpty = false; // indicators may be empty
		if( (dest.indicators == null) || (dest.indicators.equals(Collections.emptyList())) )
			fEmpty = true;
		
		try {
			int num = 0;
			if (!fEmpty) {
				for (Integer it : dest.indicators) { // search max item indicators by level
					IndicatorNode inode = cacheIndicator.get(it);
					if ((source.indicatorLevelID == inode.indicatorLevelID)
							&& (num < inode.indicatorItem))
						num = inode.indicatorItem;
				}
			}
			source.indicatorItem = num + 1;
			source.insert(kdb, 
					IndicatorNode.INDICATOR_NAME | IndicatorNode.INDICATOR_ITEM |
					IndicatorNode.INDICATOR_LEVEL | IndicatorNode.INDICATOR_COMPETENCE |
					IndicatorNode.INDICATOR_CHILD |
					IndicatorNode.INDICATOR_CREATE | IndicatorNode.INDICATOR_REMOVE);
			if (fEmpty) {
				dest.indicators = new ArrayList<>();
			}
			dest.indicators.add(source.indicatorID);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void add(Object obj, TreePath path) throws Exception {
		if( path == null) {
			JOptionPane.showMessageDialog(null, "не выбрана позиция для вставки");
			return;
		}

		Object []nodes = path.getPath();
		if( (nodes == null) || (nodes.length == 0) ) {
			JOptionPane.showMessageDialog(null, "не выбрана позиция для вставки");
			return;
		}
	
		if(obj instanceof CompetenceNode) {				
			for(int i = nodes.length; i > 0; i--) { // search CatalogNode in tree 				
				if(nodes[i - 1] instanceof CatalogNode) {					
					add((CompetenceNode)obj, (CatalogNode) nodes[i - 1]);
					return;
				}				
			}			
		}
		
		if(obj instanceof IndicatorNode) {
			for(int i = nodes.length; i > 0; i--) {
				if( (nodes[i - 1] instanceof CatalogNode) ||
						(nodes[i - 1] instanceof CompetenceNode) ){ // incorrect element
					JOptionPane.showMessageDialog(null, "не выбрана позиция для вставки");
					return;
				}
				
				if(nodes[i - 1] instanceof IndicatorNode) { // if select someone indicator
					Preconditions.checkNotNull(nodes[i - 3] instanceof CompetenceNode, "CompetenceModel.add() : incorrect hierarchy");
					((IndicatorNode)obj).indicatorLevelID = ((IndicatorNode)nodes[i - 1]).indicatorLevelID;
					add((IndicatorNode)obj, (CompetenceNode)nodes[i - 3], true);
					return;					
				}
				
				if(nodes[i - 1] instanceof Pair<?, ?>) { // if select someone level
					Preconditions.checkNotNull(nodes[i - 2] instanceof CompetenceNode, "CompetenceModel.add() : incorrect hierarchy");					
					((IndicatorNode)obj).indicatorLevelID = ((Pair<? extends Integer, ? extends LevelNode>)nodes[i - 1]).getSecond().levelID;
					add((IndicatorNode)obj, (CompetenceNode)nodes[i - 2], true);
					return;					
				}
			}			
		}
	}
	
	
	private void checkAndLoadIndicators(CompetenceNode cn) {		
		if(cn == null)
			return;

		if(cn.indicators != null)
			return;

		loadIndicators(cn, true);
	}

	private void loadIndicators(CompetenceNode cn, boolean flagPreload) {		
		if(cn == null)
			return;

		cn.indicators = loadChildIDs(sqlLoadIndicatorIDs, pattern, cn.competenceIncarnatio);
		try {
			if(flagPreload && (cn.indicators.size() != 0) ) {
				cacheIndicator.getAll(cn.indicators);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private int getCountIndicatorByLevelID(Integer competenceID, int levelID) {
		int val = 0;
		try {
			val = getCountIndicatorByLevelID(cacheCompetence.get(competenceID), levelID);
		} catch(Exception e) {
			e.printStackTrace();
			val = 0;
		}
		return val;
	}
	
	private int getCountIndicatorByLevelID(CompetenceNode cn, int levelID) {
		if(cn == null)
			return 0;
		if(cn.indicators == null)
			checkAndLoadIndicators(cn);

		int sum = 0;
		try {
			for (Integer it : cn.indicators) {
				if (cacheIndicator.get(it).indicatorLevelID == levelID)
					sum++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			sum = 0;
		}
		return sum;
	}

	
	private String sqlLoadOneCompetenceID;
	private String sqlLoadIndicatorIDs;
	private String pattern;
	
	private LoadingCache<Integer, CompetenceNode> cacheCompetence;			
	private LoadingCache<Integer, LevelNode> cacheLevel;
	private LoadingCache<Integer, IndicatorNode> cacheIndicator;
}

