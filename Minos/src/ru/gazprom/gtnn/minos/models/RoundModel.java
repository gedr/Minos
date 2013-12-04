package ru.gazprom.gtnn.minos.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;

import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.RoundNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;

public class RoundModel extends DefaultComboBoxModel<RoundNode> {
	private static final long serialVersionUID = 1L;
	
	private DatabaseConnectionKeeper kdb;
	private LoadingCache<Integer, RoundNode> cacheRound;
	private String sqlLoadRoundIDs;
	private List<Integer> roundIDs;
	private int selectedRoundID = 0;
	private boolean flagHaveSelectedRound = false;
	private StringBuilder sb;

	private JLabel roundLabel;
	private JTable actorsTable;

	public RoundModel(DatabaseConnectionKeeper kdb,
			LoadingCache<Integer, RoundNode> cacheRound,
			JLabel roundLabel,
			JTable actorsTable,
			String sqlLoadRoundIDs) {			
		this.kdb = kdb;
		this.cacheRound = cacheRound;
		this.sqlLoadRoundIDs = sqlLoadRoundIDs;
		this.roundLabel = roundLabel;
		sb = new StringBuilder();
		roundIDs = loadRoundIDs();
		this.actorsTable = actorsTable;
	}		

	public DatabaseConnectionKeeper getDatabaseConnectionKeeper() {
		return kdb;
	}	
	
	
	@Override
	public void addElement(RoundNode arg) {
		cacheRound.put(arg.roundID, arg);
		selectedRoundID = arg.roundID;
		flagHaveSelectedRound = true;
		roundIDs.add(arg.roundID);		
	}

	@Override
	public int getIndexOf(Object arg) {
		if( !(arg instanceof RoundNode) )
			return 0;
		try {
			for(int i = 0; i < roundIDs.size(); i++) {
				if(cacheRound.get(roundIDs.get(i)).roundID == ((RoundNode)arg).roundID)
					return i;
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	public RoundNode getElementAt(int arg) {
		if( (0 <= arg) && (arg < roundIDs.size()) ) {
			RoundNode node = null;
			try {
				node = cacheRound.get( roundIDs.get(arg) );
			} catch (ExecutionException e) {
				e.printStackTrace();
				node = null;				
			}
			return node;
		}	

		return null;
	}

	@Override
	public int getSize() {
		return roundIDs.size();
	}


	@Override
	public Object getSelectedItem() {		
		if(flagHaveSelectedRound) {
			RoundNode node = null;
			try {
				node = cacheRound.get(selectedRoundID);
			} catch (ExecutionException e) {
				e.printStackTrace();
				node = null;				
			}
			return node;
		}
		return null;
	}

	
	@Override
	public void setSelectedItem(Object arg) {
		if(arg == null)
			return;
		if( !(arg instanceof RoundNode) )
			return;
		
		flagHaveSelectedRound = true;
		RoundNode node = (RoundNode)arg;
		selectedRoundID = node.roundID;		
		
		sb.delete(0, sb.length());
		sb.append("Раунд оценки: ").append(node.roundName).append("     описание: ").append(node.roundDescr).
		append("     [ ").append(node.roundStart).append(" ]  -  [ ").append(node.roundStop).append(" ] ");		
		roundLabel.setText(sb.toString());
		((RoundActorsTableModel)actorsTable.getModel()).load(node.roundID);
		actorsTable.updateUI();
	}
	
	public void refresh() {
		if(roundIDs != null) 
			roundIDs.clear();
		//cacheRound.cleanUp();
		roundIDs = loadRoundIDs();		
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