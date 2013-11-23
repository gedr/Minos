package ru.gazprom.gtnn.minos.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.entity.RoundNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;

public class RoundModel implements ComboBoxModel<RoundNode> {
	private DatabaseConnectionKeeper kdb;
	private LoadingCache<Integer, RoundNode> cacheRound;
	private String sqlLoadRoundIDs;

	public RoundModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, RoundNode> cacheRound,			
			String sqlLoadRoundIDs) {			
		this.kdb = kdb;
		this.cacheRound = cacheRound;
		this.sqlLoadRoundIDs = sqlLoadRoundIDs;
	}		

	@Override
	public RoundNode getElementAt(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Object getSelectedItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelectedItem(Object arg0) {
		// TODO Auto-generated method stub
		
	}


	
	
	
	
	@Override
	public void addListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub
		
	}

	
	private List<Integer> loadChildIDs() {		
		List<Integer> lst = Collections.emptyList();
		/*
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
		*/
		return null;
	}	

}
