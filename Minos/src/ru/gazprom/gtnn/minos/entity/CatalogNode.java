package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "CatalogTable")
public class CatalogNode {
	@TableColumn
	public int catalogID;
	@TableColumn
	public String catalogName;
	@TableColumn
	public String catalogHost;
	@TableColumn
	public int catalogMode;
	@TableColumn
	public int catalogParent;
	@TableColumn
	public int catalogItem;
	@TableColumn
	public java.util.Date catalogCreate;
	@TableColumn
	public java.util.Date catalogRemove;
	
	public List<Integer> subCatalogs;

	
	public static final int CATALOG_NAME = 1;
	public static final int CATALOG_PARENT = 2;
	public static final int CATALOG_ITEM = 4;
	public static final int CATALOG_CREATE = 8;
	public static final int CATALOG_REMOVE = 16;
	public static final int CATALOG_MODE = 32;
	public static final int CATALOG_HOST = 64;
	public static final int CATALOG_ID = 128;
	public static Map<String, String> names;
		
	public static int insert(DatabaseConnectionKeeper kdb, int flag, CatalogNode catalogNode) {
		if(names == null)
			return -1;
		String tableName = names.get("CatalogTable");
		if(tableName == null)
			return -1;
		List<RecordFeld> lst = new ArrayList<>();
		if(((flag & CATALOG_NAME) != 0) && (names.get("catalogName") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("catalogName"), catalogNode.catalogName));
		if(((flag & CATALOG_PARENT) != 0) && (names.get("catalogParent") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogParent"), catalogNode.catalogParent));
		if(((flag & CATALOG_ITEM) != 0) && (names.get("catalogItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogItem"), catalogNode.catalogItem));
		if(((flag & CATALOG_CREATE) != 0) && (names.get("catalogCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("catalogCreate"), catalogNode.catalogCreate));
		if(((flag & CATALOG_REMOVE) != 0) && (names.get("catalogRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("catalogRemove"), catalogNode.catalogRemove));
		if(((flag & CATALOG_MODE) != 0) && (names.get("catalogMode") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogMode"), catalogNode.catalogMode));
		if(((flag & CATALOG_HOST) != 0) && (names.get("catalogHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("catalogHost"), catalogNode.catalogHost));
		if(((flag & CATALOG_ID) != 0) && (names.get("catalogID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogID"), catalogNode.catalogID));
		
		int key= -1; 
		try {
			key = kdb.insertRow(true, tableName, lst);
		} catch (Exception e) {
			key = -1;
		}	
		
		return key;
	}

	
	@Override
	public String toString() {
		return catalogName + " [ " + catalogCreate + " ] " + " [ " + catalogRemove + " ] ";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof CatalogNode) )
			return false;
		
		return (this.catalogID == ((CatalogNode)obj).catalogID ? true : false);
	}

	@Override
	public int hashCode() {		
		return catalogID;
	}
}
