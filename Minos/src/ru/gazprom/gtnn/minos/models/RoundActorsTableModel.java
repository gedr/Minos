package ru.gazprom.gtnn.minos.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.RoundActorsNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;
import ru.gedr.util.tuple.Pair;
import ru.gedr.util.tuple.Unit;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class RoundActorsTableModel implements TableModel{
	
	public RoundActorsTableModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, RoundActorsNode> cacheRoundActors, 
			LoadingCache<Integer, PersonNode> cachePerson,
			String sqlLoadRoundActorsIDs,
			String pattern) {
	
		this.cacheRoundActors = cacheRoundActors;
		this.cachePerson = cachePerson;
		this.sqlLoadRoundActorsIDs = sqlLoadRoundActorsIDs;
		this.pattern = pattern;
		this.kdb = kdb;
		lstRounActorsIDs = Collections.emptyList();
	}

	@Override
	public Class<?> getColumnClass(int arg) {
		return String.class;
	}
	
	@Override
	public int getColumnCount() {		
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {		
		return columnIndex == 0 ? "эксперт" : "тестируемый";
	}

	@Override
	public int getRowCount() {
		return lstRounActorsIDs.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Preconditions.checkState( (0 <= rowIndex) && (rowIndex < lstRounActorsIDs.size()), 
				"RoundActorsTableModel.getValueAt() : rowIndex out of bounds");

		String result = "not find";
		try {
			RoundActorsNode nodeActors = cacheRoundActors.get(lstRounActorsIDs.get(rowIndex));
			PersonNode nodePerson = (columnIndex == 0 ? cachePerson.get(nodeActors.roundActorsMinosID)
														: cachePerson.get(nodeActors.roundActorsSinnerID));
			result = nodePerson.personSurname + " " + nodePerson.personName + " "  + nodePerson.personPatronymic;  

		} catch (Exception e) {
			result = "not find";
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		
	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void load(int roundID) {
		if( (lstRounActorsIDs != null) && (lstRounActorsIDs.size() > 0) ) {
			lstRounActorsIDs.clear();
			lstRounActorsIDs = Collections.emptyList();
			cacheRoundActors.cleanUp();
		}
		
		try {			
			String request = kdb.makeSQLString(sqlLoadRoundActorsIDs, pattern, String.valueOf(roundID));
			Preconditions.checkNotNull(request, "RoundActorsTableModel.load() : makeSQLString return null");

			TableKeeper tk = kdb.selectRows(request);
			if( (tk == null) || (tk.getRowCount() <= 0) )
				lstRounActorsIDs = Collections.emptyList();

			Preconditions.checkState(tk.getColumnCount() == 1, 
					"RoundActorsTableModel.load() : selectRows() return incorrect column count (" + tk.getColumnCount() + ")");

			lstRounActorsIDs = new ArrayList<>();
			for(int i = 1; i <= tk.getRowCount(); i++) {
				lstRounActorsIDs.add((Integer)tk.getValue(i, 1));
			}
			cacheRoundActors.getAll(lstRounActorsIDs);
		} catch(Exception e) {
			e.printStackTrace();
			lstRounActorsIDs = Collections.emptyList();
		}		
	}	

	private List<Integer> lstRounActorsIDs;
	private LoadingCache<Integer, RoundActorsNode> cacheRoundActors;
	private LoadingCache<Integer, PersonNode> cachePerson;
	private String sqlLoadRoundActorsIDs;
	private String pattern;
	private DatabaseConnectionKeeper kdb;
}