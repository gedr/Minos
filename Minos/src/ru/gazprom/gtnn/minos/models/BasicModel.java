package ru.gazprom.gtnn.minos.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;

public abstract class BasicModel {
	public BasicModel(DatabaseConnectionKeeper kdb) {
		this.kdb = kdb;
	}

	protected <T, P> List<T> loadChildIDs(String sql, String pattern, P parentID) {
		List<T> lst = Collections.emptyList();
		try {			
			String request = kdb.makeSQLString(sql, pattern, parentID.toString());
			Preconditions.checkNotNull(request, "BasicModel.loadChildIDs() : makeSQLString return null");
			
			TableKeeper tk = kdb.selectRows(request);
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

	protected DatabaseConnectionKeeper kdb;
}
