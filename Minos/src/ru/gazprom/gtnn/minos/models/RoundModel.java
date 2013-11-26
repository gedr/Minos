package ru.gazprom.gtnn.minos.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.RoundNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;

public class RoundModel extends DefaultComboBoxModel<RoundNode> {
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


		
	private List<Integer> loadRoundIDs() {		
		List<Integer> lst = Collections.emptyList();

		try {			
			Preconditions.checkNotNull(sqlLoadRoundIDs, "RoundModel.loadRoundIDs() : sqlLoadRoundIDs is null");
			
			TableKeeper tk = kdb.selectRows(sqlLoadRoundIDs);
			if(tk == null)
				return lst;
			
			Preconditions.checkState(tk.getColumnCount() == 1, 
					"RoundModel.loadRoundIDs() : selectRows() return incorrect column count (" + tk.getColumnCount() + ")");
						
			if(tk.getRowCount() > 0) {
				lst = new ArrayList<>();
				for(int i = 1; i <= tk.getRowCount(); i++) {					
					lst.add((Integer) tk.getValue(i, 1));			
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			lst = Collections.emptyList();
		}		
		return lst;
	}	

}
