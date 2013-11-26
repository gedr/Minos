package ru.gazprom.gtnn.minos.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;
import ru.gedr.util.tuple.Pair;
import ru.gedr.util.tuple.Unit;

public abstract class BasicModel implements TreeModel{
	public static java.util.Date endTime;
	static {		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			endTime = (java.util.Date) df.parse("9999-12-31");
		} catch(ParseException e) {
			e.printStackTrace();
			endTime = new java.util.Date();
		}
	}

	public BasicModel(DatabaseConnectionKeeper kdb) {
		super();
		this.kdb = kdb;
		listenerList = new EventListenerList();
	}

	public DatabaseConnectionKeeper getDatabaseConnectionKeeper() {
		return kdb;
	}
	
	public void reload() { }
	public void add(Object obj, TreePath path) throws Exception{}
	
	public <T, P> List<T> loadChildIDs(String sql, String pattern, P parentID) {		
		List<T> lst = Collections.emptyList();
		try {			
			String request = kdb.makeSQLString(sql, pattern, parentID.toString());
			Preconditions.checkNotNull(request, "BasicModel.loadChildIDs() : makeSQLString return null");
			
			TableKeeper tk = kdb.selectRows(request);
			if(tk == null)
				return lst;
			
			Preconditions.checkState(tk.getColumnCount() == 1, 
					"BasicModel.loadChildIDs() : selectRows() return incorrect column count (" + tk.getColumnCount() + ")");
						
			if(tk.getRowCount() > 0) {
				lst = new ArrayList<>();
				for(int i = 1; i <= tk.getRowCount(); i++) { 
					@SuppressWarnings("unchecked")
					T val = (T)tk.getValue(i, 1);
					lst.add(val);			
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			lst = Collections.emptyList();
		}		
		return lst;
	}	

	protected <P> List<Object> loadChildrenIDs(String sql, String pattern, P parentID) {		
		List<Object> lst = Collections.emptyList();
		try {			
			String request = kdb.makeSQLString(sql, pattern, parentID.toString());
			Preconditions.checkNotNull(request, "BasicModel.loadChildrenIDs() : makeSQLString return null");
			
			TableKeeper tk = kdb.selectRows(request);
			if(tk == null)
				return lst;

			if(tk.getRowCount() > 0) {
				lst = new ArrayList<>();
				for(int i = 1; i <= tk.getRowCount(); i++) {
					
					switch( tk.getColumnCount() ) {
					case 1: 
						lst.add( new Unit<Object>(tk.getValue(i, 1)) );
						break;
					case 2: 
						lst.add( new Pair<Object, Object>(tk.getValue(i, 1), tk.getValue(i, 2)) );
						break;
					default:
						// not supported more dimension
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			lst = Collections.emptyList();
		}		
		return lst;
	}	

	  /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     *
     * @see     #removeTreeModelListener
     * @param   l       the listener to add
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     *
     * @see     #addTreeModelListener
     * @param   l       the listener to remove
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

	protected DatabaseConnectionKeeper kdb;
	protected EventListenerList listenerList;
}
