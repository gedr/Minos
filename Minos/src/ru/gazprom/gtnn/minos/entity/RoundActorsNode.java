package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

import com.google.common.base.Preconditions;

@TableName(name = "RoundActorsTable")
public class RoundActorsNode extends BasicNode<Integer> {
	@TableColumn
	public int roundActorsID;
	@TableColumn
	public int roundActorsMinosID;
	@TableColumn
	public int roundActorsSinnerID;
	@TableColumn
	public int roundActorsRoundID;
	@TableColumn
	public String roundActorsHost;
	@TableColumn
	public String roundActorsFinish;
	@TableColumn
	public java.util.Date roundActorsCreate;
	@TableColumn
	public java.util.Date roundActorsRemove;


	public static final int ROUND_ACTORS_MINOS		= 1;
	public static final int ROUND_ACTORS_SINNER 	= 2;
	public static final int ROUND_ACTORS_ROUND 		= 4;
	
	public static final int ROUND_ACTORS_FINISH		= 8;
	public static final int ROUND_ACTORS_HOST 		= 16;

	public static final int ROUND_ACTORS_CREATE		= 32;
	public static final int ROUND_ACTORS_REMOVE		= 64;
	
	public static final int ROUND_ACTORS_ID 		= 128;


	public void insert(DatabaseConnectionKeeper kdb, int flags) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "RoundActorsNode.insert() : makeListParam() return null");
		
		try {
			roundActorsID = kdb.insertRow(true, names.get("RoundActorsTable"), lst);			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(roundActorsID != -1, "RoundActorsNode.update() : roundPersonsID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "RoundActorsNode.update() : makeListParam() return null");

		try {
			kdb.updateRow( names.get("RoundActorsTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("roundActorsID"), roundActorsID) );						
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
				
		if(names.get("RoundActorsTable") == null)
			return null;
		
		List<RecordFeld> lst = new ArrayList<>();

		if(((flags & ROUND_ACTORS_HOST) != 0) && (names.get("roundActorsHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("roundActorsHost"), roundActorsHost));


		if(((flags & ROUND_ACTORS_MINOS) != 0) && (names.get("roundActorsMinosID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundActorsMinosID"), roundActorsMinosID));
		if(((flags & ROUND_ACTORS_SINNER) != 0) && (names.get("roundActorsSinnerID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundActorsSinnerID"), roundActorsSinnerID));
		if(((flags & ROUND_ACTORS_ROUND) != 0) && (names.get("roundActorsRoundID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundActorsRoundID"), roundActorsRoundID));

		if(((flags & ROUND_ACTORS_FINISH) != 0) && (names.get("roundActorsFinish") != null) )
			lst.add(new RecordFeld(java.sql.Types.BIT, names.get("roundActorsFinish"), roundActorsFinish));
		
		if(((flags & ROUND_ACTORS_ID) != 0) && (names.get("roundActorsID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundActorsID"), roundActorsID));
		
		if(((flags & ROUND_ACTORS_CREATE) != 0) && (names.get("roundActorsCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("roundActorsCreate"), roundActorsCreate));
		if(((flags & ROUND_ACTORS_REMOVE) != 0) && (names.get("roundActorsRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("roundActorsRemove"), roundActorsRemove));		

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
		
		if( !(obj instanceof RoundActorsNode))
			return false;
		
		return (this.roundActorsID == ((RoundActorsNode)obj).roundActorsID ? true : false);
	}

	@Override
	public int hashCode() {		
		return roundActorsID;
	}

	@Override
	public Integer getID() {		
		return roundActorsID;
	}
}