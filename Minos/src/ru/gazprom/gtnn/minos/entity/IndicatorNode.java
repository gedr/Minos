package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "IndicatorTable")
public class IndicatorNode {
	
	@TableColumn
	public int indicatorID;
	@TableColumn
	public String indicatorName;
	@TableColumn
	public String indicatorHost;
	@TableColumn
	public int indicatorItem;
	@TableColumn
	public int indicatorLevelID;
	@TableColumn
	public int indicatorCompetenceIncarnatio;
	@TableColumn
	public java.util.Date indicatorCreate;
	@TableColumn
	public java.util.Date indicatorRemove;

	
	public static final int INDICATOR_NAME = 1;
	public static final int INDICATOR_ITEM = 2;
	public static final int INDICATOR_HOST = 4;
	public static final int INDICATOR_LEVEL = 8;
	public static final int INDICATOR_COMPETENCE = 16;	
	public static final int INDICATOR_CREATE = 32;
	public static final int INDICATOR_REMOVE = 64;
	public static final int INDICATOR_ID = 128;
	public static Map<String, String> names;
		
	public static int insert(DatabaseConnectionKeeper kdb, int flags, IndicatorNode indicatorNode) {
		if(names == null)
			return -1;
		String tableName = names.get("IndicatorTable");
		if(tableName == null)
			return -1;
		List<RecordFeld> lst = new ArrayList<>();
		if(((flags & INDICATOR_NAME) != 0) && (names.get("indicatorName") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("indicatorName"), indicatorNode.indicatorName));
		if(((flags & INDICATOR_HOST) != 0) && (names.get("indicatorHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("indicatorHost"), indicatorNode.indicatorHost));

		if(((flags & INDICATOR_ITEM) != 0) && (names.get("indicatorItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorItem"), indicatorNode.indicatorItem));
		if(((flags & INDICATOR_LEVEL) != 0) && (names.get("indicatorLevelID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorLevelID"), indicatorNode.indicatorLevelID));
		if(((flags & INDICATOR_COMPETENCE) != 0) && (names.get("indicatorCompetenceIncarnatio") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorCompetenceIncarnatio"), indicatorNode.indicatorCompetenceIncarnatio));
		if(((flags & INDICATOR_ID) != 0) && (names.get("indicatorID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("indicatorID"), indicatorNode.indicatorID));
		
		if(((flags & INDICATOR_CREATE) != 0) && (names.get("indicatorCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("indicatorCreate"), indicatorNode.indicatorCreate));
		if(((flags & INDICATOR_REMOVE) != 0) && (names.get("indicatorRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("indicatorRemove"), indicatorNode.indicatorRemove));

		int key= -1; 
		try {
			key = kdb.insertRow(true, tableName, lst);
		} catch (Exception e) {
			e.printStackTrace();
			key = -1;
		}	
		
		return key;
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
}
