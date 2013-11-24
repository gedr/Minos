package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "IndicatorTable")
public class IndicatorNode  extends BasicNode<Integer> {
	@TableColumn
	public int indicatorID;		// primary key unique
	@TableColumn
	public String indicatorName; // name
	@TableColumn
	public String indicatorHost; // creator login
	@TableColumn
	public int indicatorItem; // position in list
	@TableColumn
	public int indicatorLevelID; // level
	@TableColumn
	public int indicatorCompetenceIncarnatio; // competence incarnation
	@TableColumn
	public int indicatorChild; // indicator id what replace this indicator
	@TableColumn
	public java.util.Date indicatorCreate; // date and time create
	@TableColumn
	public java.util.Date indicatorRemove; // date and time remove

	public static final int INDICATOR_NAME = 1;
	public static final int INDICATOR_ITEM = 2;
	public static final int INDICATOR_HOST = 4;
	public static final int INDICATOR_LEVEL = 8;
	public static final int INDICATOR_COMPETENCE = 16;	
	public static final int INDICATOR_CHILD = 32;
	public static final int INDICATOR_CREATE = 64;
	public static final int INDICATOR_REMOVE = 128;
	public static final int INDICATOR_ID = 256;
			
	public void insert(DatabaseConnectionKeeper kdb, int flags) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "IndicatorNode.insert() : makeListParam() return null");
		
		try {
			indicatorID = kdb.insertRow(true, names.get("IndicatorTable"), lst);
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		} finally {
			if(lst != null)
				lst.clear();			
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(indicatorID != -1, "IndicatorNode.update() : indicatorID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "IndicatorNode.update() : makeListParam() return null");

		try {
			kdb.updateRow( names.get("IndicatorTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorID"), indicatorID) );						
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		} finally {
			if(lst != null)
				lst.clear();			
		}
	}	
	
	/**
	 * Make parameters list for insert in table 
	 * @param flags - define fields for insert, over filed must be have default value
	 */
	private List<RecordFeld>  makeListParam(int flags) {
		if(names == null)			
			return null;
				
		if(names.get("IndicatorTable") == null)
			return null;

		
		List<RecordFeld> lst = new ArrayList<>();
		if(((flags & INDICATOR_NAME) != 0) && (names.get("indicatorName") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("indicatorName"), indicatorName));
		if(((flags & INDICATOR_HOST) != 0) && (names.get("indicatorHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("indicatorHost"), indicatorHost));

		if(((flags & INDICATOR_ITEM) != 0) && (names.get("indicatorItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorItem"), indicatorItem));
		if(((flags & INDICATOR_ID) != 0) && (names.get("indicatorChild") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorChild"), indicatorChild));
		if(((flags & INDICATOR_LEVEL) != 0) && (names.get("indicatorLevelID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorLevelID"), indicatorLevelID));
		if(((flags & INDICATOR_COMPETENCE) != 0) && (names.get("indicatorCompetenceIncarnatio") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorCompetenceIncarnatio"), indicatorCompetenceIncarnatio));
		if(((flags & INDICATOR_ID) != 0) && (names.get("indicatorID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorID"), indicatorID));

		
		if(((flags & INDICATOR_CREATE) != 0) && (names.get("indicatorCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("indicatorCreate"), indicatorCreate));
		if(((flags & INDICATOR_REMOVE) != 0) && (names.get("indicatorRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("indicatorRemove"), indicatorRemove));
		
		return lst;
	}
	
	@Override
	public String toString() {
		return indicatorName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof IndicatorNode))
			return false;		
			
		return (this.indicatorID == ((IndicatorNode)obj).indicatorID ? true : false);
	}

	@Override
	public int hashCode() {		
		return indicatorID;
	}

	@Override
	public Integer getID() {		
		return indicatorID;
	}
}
