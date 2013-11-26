package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

import com.google.common.base.Preconditions;

@TableName(name = "RoundPersonsTable")
public class RoundPersonsNode extends BasicNode<Integer> {
	@TableColumn
	public int roundPersonsID;
	@TableColumn
	public int roundPersonsMinosID;
	@TableColumn
	public int roundPersonsSinnerID;
	@TableColumn
	public int roundPersonsRoundID;
	@TableColumn
	public int roundPersonsItem;
	@TableColumn
	public String roundPersonsHost;
	@TableColumn
	public java.util.Date roundPersonsCreate;
	@TableColumn
	public java.util.Date roundPersonsRemove;


	public static final int ROUND_PERSONS_MINOS		= 1;
	public static final int ROUND_PERSONS__SINNER 	= 2;
	public static final int ROUND_PERSONS_ROUND 	= 4;
	
	public static final int ROUND_PERSONS_ITEM		= 8;
	public static final int ROUND_PERSONS_HOST 		= 16;

	public static final int ROUND_PERSONS_CREATE	= 32;
	public static final int ROUND_PERSONS_REMOVE	= 64;
	
	public static final int ROUND_PERSONS_ID 		= 128;


	public void insert(DatabaseConnectionKeeper kdb, int flags) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "RoundPersonsNode.insert() : makeListParam() return null");
		
		try {
			roundPersonsID = kdb.insertRow(true, names.get("RoundPersonsTable"), lst);			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(roundPersonsID != -1, "RoundPersonsNode.update() : roundPersonsID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "RoundPersonsNode.update() : makeListParam() return null");

		try {
			kdb.updateRow( names.get("RoundPersonsTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("roundPersonsID"), roundPersonsID) );						
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

		if(((flags & ROUND_PERSONS_HOST) != 0) && (names.get("roundPersonsHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("roundPersonsHost"), roundPersonsHost));


		if(((flags & ROUND_PERSONS_MINOS) != 0) && (names.get("roundPersonsMinosID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundPersonsMinosID"), roundPersonsMinosID));
		if(((flags & ROUND_PERSONS__SINNER) != 0) && (names.get("roundPersonsSinnerID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundPersonsSinnerID"), roundPersonsSinnerID));
		if(((flags & ROUND_PERSONS_ROUND) != 0) && (names.get("roundPersonsRoundID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundPersonsRoundID"), roundPersonsRoundID));
		if(((flags & ROUND_PERSONS_ITEM) != 0) && (names.get("roundPersonsItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundPersonsItem"), roundPersonsItem));

		
		if(((flags & ROUND_PERSONS_ID) != 0) && (names.get("roundPersonsID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundPersonsID"), roundPersonsID));
		
		if(((flags & ROUND_PERSONS_CREATE) != 0) && (names.get("roundPersonsCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("roundPersonsCreate"), roundPersonsCreate));
		if(((flags & ROUND_PERSONS_REMOVE) != 0) && (names.get("roundPersonsRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("roundPersonsRemove"), roundPersonsRemove));		

		return lst;
	}
	
	
	@Override
	public String toString() {
		return null; 
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof RoundPersonsNode))
			return false;
		
		return (this.roundPersonsID == ((RoundPersonsNode)obj).roundPersonsID ? true : false);
	}

	@Override
	public int hashCode() {		
		return roundPersonsID;
	}

	@Override
	public Integer getID() {		
		return roundPersonsID;
	}
}



		

	


