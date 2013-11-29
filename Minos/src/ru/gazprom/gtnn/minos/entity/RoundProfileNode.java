package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "RoundProfileTable")
public class RoundProfileNode extends BasicNode<Integer> {
	@TableColumn
	public int roundProfileID;
	@TableColumn
	public int roundProfileRoundActorsID;
	@TableColumn
	public int roundProfileProfileID;
	@TableColumn
	public long roundProfileIndicatorFlagsHI;
	@TableColumn
	public long roundProfileIndicatorFlagsLO;
	@TableColumn
	public double roundProfileCost;

	public static final int ROUND_PROFILE_ACTORS 				= 1;
	public static final int ROUND_PROFILE_PROFILE 				= 2;
	public static final int ROUND_PROFILE_INDICATOR_FLAGS_HI 	= 4;
	public static final int ROUND_PROFILE_INDICATOR_FLAGS_LO 	= 8;	
	public static final int ROUND_PROFILE_COST				 	= 16;
	public static final int ROUND_PROFILE_ID				 	= 32;
		
	public void insert(DatabaseConnectionKeeper kdb, int flags) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "RoundProfileNode.insert() : makeListParam() return null");
		
		try {
			roundProfileID = kdb.insertRow(true, names.get("RoundProfileTable"), lst);			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(roundProfileID != -1, "RoundProfileNode.update() : roundProfileID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "RoundProfileNode.update() : makeListParam() return null");

		try {
			kdb.updateRow( names.get("RoundProfileTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("roundProfileID"), roundProfileID) );						
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
				
		if(names.get("RoundProfileTable") == null)
			return null;
		
		List<RecordFeld> lst = new ArrayList<>();


		if(((flags & ROUND_PROFILE_ACTORS) != 0) && (names.get("roundProfileRoundActorsID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundProfileRoundActorsID"), roundProfileRoundActorsID));
		
		if(((flags & ROUND_PROFILE_PROFILE) != 0) && (names.get("roundProfileProfileID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundProfileProfileID"), roundProfileProfileID));
		
		if(((flags & ROUND_PROFILE_INDICATOR_FLAGS_HI) != 0) && (names.get("roundProfileIndicatorFlagsHI") != null) )
			lst.add(new RecordFeld(java.sql.Types.BIGINT, names.get("roundProfileIndicatorFlagsHI"), roundProfileIndicatorFlagsHI));
		if(((flags & ROUND_PROFILE_INDICATOR_FLAGS_LO) != 0) && (names.get("roundProfileIndicatorFlagsLO") != null) )
			lst.add(new RecordFeld(java.sql.Types.BIGINT, names.get("roundProfileIndicatorFlagsLO"), roundProfileIndicatorFlagsLO));

		
		if(((flags & ROUND_PROFILE_ID) != 0) && (names.get("roundProfileID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("roundProfileID"), roundProfileID));
		
		if(((flags & ROUND_PROFILE_COST) != 0) && (names.get("roundProfileCost") != null) )
			lst.add(new RecordFeld(java.sql.Types.FLOAT, names.get("roundProfileCost"), roundProfileCost));

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
		
		if( !(obj instanceof RoundProfileNode))
			return false;
		
		return (this.roundProfileID == ((RoundProfileNode)obj).roundProfileID ? true : false);
	}

	@Override
	public int hashCode() {		
		return roundProfileID;
	}

	@Override
	public Integer getID() {		
		return roundProfileID;
	}
}