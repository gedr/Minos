package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "CompetenceTable")
public class CompetenceNode {
	@TableColumn
	public int competenceID;
	@TableColumn
	public String competenceName;
	@TableColumn
	public String competenceDescr;
	@TableColumn
	public int competenceItem;
	@TableColumn
	public String competenceHost;
	@TableColumn
	public int competenceMode;
	@TableColumn
	public int competenceCatalogID;
	@TableColumn
	public int competenceIncarnatio;
	@TableColumn
	public int competenceChainNumber;
	@TableColumn
	public java.util.Date competenceCreate;
	@TableColumn
	public java.util.Date competenceRemove;
	
	
	public List<Integer> indicators;		

	
	public static final int COMPETENCE_NAME = 1;
	public static final int COMPETENCE_DESCR = 2;
	public static final int COMPETENCE_ITEM = 4;
	public static final int COMPETENCE_HOST = 8;
	public static final int COMPETENCE_MODE = 16;
	public static final int COMPETENCE_CATALOG = 32;
	public static final int COMPETENCE_INCARNATIO = 64;
	public static final int COMPETENCE_CHAIN_NUMBER = 128;
	public static final int COMPETENCE_CREATE = 256;
	public static final int COMPETENCE_REMOVE = 512;
	public static final int COMPETENCE_ID = 1024;
	public static Map<String, String> names;
		
	public static int insert(DatabaseConnectionKeeper kdb, int flags, boolean fNew, CompetenceNode competenceNode) {
		if(names == null)
			return -1;
		String tableName = names.get("CompetenceTable");
		if(tableName == null)
			return -1;
		List<RecordFeld> lst = new ArrayList<>();
		if(((flags & COMPETENCE_NAME) != 0) && (names.get("competenceName") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("competenceName"), competenceNode.competenceName));
		if(((flags & COMPETENCE_DESCR) != 0) && (names.get("competenceDescr") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("competenceDescr"), competenceNode.competenceDescr));
		if(((flags & COMPETENCE_HOST) != 0) && (names.get("competenceHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("competenceHost"), competenceNode.competenceHost));

		if(((flags & COMPETENCE_ITEM) != 0) && (names.get("competenceItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceItem"), competenceNode.competenceItem));
		if(((flags & COMPETENCE_MODE) != 0) && (names.get("competenceMode") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceMode"), competenceNode.competenceMode));
		if(((flags & COMPETENCE_CATALOG) != 0) && (names.get("competenceCatalogID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceCatalogID"), competenceNode.competenceCatalogID));
		if(((flags & COMPETENCE_INCARNATIO) != 0) && (names.get("competenceIncarnatio") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceIncarnatio"), competenceNode.competenceIncarnatio));
		if(((flags & COMPETENCE_CHAIN_NUMBER) != 0) && (names.get("competenceChainNumber") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceChainNumber"), competenceNode.competenceChainNumber));
		if(((flags & COMPETENCE_ID) != 0) && (names.get("competenceID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceID"), competenceNode.competenceID));
		
		if(((flags & COMPETENCE_CREATE) != 0) && (names.get("competenceCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("competenceCreate"), competenceNode.competenceCreate));
		if(((flags & COMPETENCE_REMOVE) != 0) && (names.get("competenceRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("competenceRemove"), competenceNode.competenceRemove));

		int key= -1; 
		try {
			key = kdb.insertRow(true, tableName, lst);
			if (fNew) 
				kdb.updateRow( tableName,
						Arrays.asList(new RecordFeld(java.sql.Types.INTEGER, 
										names.get("competenceIncarnatio"), 
											Integer.valueOf(key))),								
						new RecordFeld(java.sql.Types.INTEGER, names.get("competenceID"), Integer.valueOf(key)) );
		} catch (Exception e) {
			e.printStackTrace();
			key = -1;
		}	
		
		return key;
	}

	
	@Override
	public String toString() {
		return competenceName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof CompetenceNode))
			return false;		
			
		return (this.competenceID == ((CompetenceNode)obj).competenceID ? true : false);
	}

	@Override
	public int hashCode() {		
		return competenceID;
	}
}
