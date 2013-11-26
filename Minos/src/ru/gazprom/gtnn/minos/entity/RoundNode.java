package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

import com.google.common.base.Preconditions;

@TableName(name = "RoundTable")
public class RoundNode extends BasicNode<Integer> {
	@TableColumn
	public int roundID;
	@TableColumn
	public String roundName;
	@TableColumn
	public String roundDescr;
	@TableColumn
	public String roundHost;
	@TableColumn
	public java.util.Date roundCreate;
	@TableColumn
	public java.util.Date roundRemove;
	@TableColumn
	public java.util.Date roundStart;
	@TableColumn
	public java.util.Date roundStop;
		
	public static final int ROUND_NAME 		= 1;
	public static final int ROUND_DESCR 	= 2;
	public static final int ROUND_HOST 		= 4;
	
	public static final int ROUND_CREATE 	= 8;
	public static final int ROUND_REMOVE 	= 16;

	public static final int ROUND_START 	= 32;
	public static final int ROUND_STOP	 	= 64;
	
	public static final int ROUND_ID 		= 128;

	public void insert(DatabaseConnectionKeeper kdb, int flags) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "RoundNode.insert() : makeListParam() return null");
		
		try {
			roundID = kdb.insertRow(true, names.get("RoundTable"), lst);			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(roundID != -1, "RoundNode.update() : roundID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "RoundNode.update() : makeListParam() return null");

		try {
			kdb.updateRow( names.get("RoundTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("roundID"), roundID) );						
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		}
	}	
	
	/**
	 * Make parameters list for insert in table 
	 * @param flags - define fields for insert, over filed must be have default value
	 */
	private List<RecordFeld>  makeListParam(int flags) {
		if(names == null)			
			return null;
				
		if(names.get("RoundTable") == null)
			return null;
		
		List<RecordFeld> lst = new ArrayList<>();

		if(((flags & ROUND_NAME) != 0) && (names.get("roundName") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("roundName"), roundName));
		if(((flags & ROUND_DESCR) != 0) && (names.get("roundDescr") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("roundDescr"), roundDescr));
		if(((flags & ROUND_HOST) != 0) && (names.get("roundHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("roundHost"), roundHost));

		if(((flags & ROUND_ID) != 0) && (names.get("roundID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundID"), roundID));
		
		if(((flags & ROUND_CREATE) != 0) && (names.get("roundCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("roundCreate"), roundCreate));
		if(((flags & ROUND_REMOVE) != 0) && (names.get("roundRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("roundRemove"), roundRemove));		

		if(((flags & ROUND_START) != 0) && (names.get("roundStart") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("roundStart"), roundStart));
		if(((flags & ROUND_STOP) != 0) && (names.get("roundStop") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("roundStop"), roundStop));		

		return lst;
	}

	@Override
	public String toString() {
		return roundName; 
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof RoundNode))
			return false;
		
		return (this.roundID == ((RoundNode)obj).roundID ? true : false);
	}

	@Override
	public int hashCode() {		
		return roundID;
	}

	@Override
	public Integer getID() {		
		return roundID;
	}
}