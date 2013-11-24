package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "CatalogTable")
public class CatalogNode extends BasicNode<Integer> {
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
	public int catalogVariant;
	@TableColumn
	public java.util.Date catalogCreate;
	@TableColumn
	public java.util.Date catalogRemove;
	
	public List<Integer> subCatalogs;
	
	public static final int CATALOG_NAME 	= 1;
	public static final int CATALOG_PARENT 	= 2;
	public static final int CATALOG_ITEM 	= 4;
	public static final int CATALOG_VARIANT = 8;
	public static final int CATALOG_CREATE 	= 16;
	public static final int CATALOG_REMOVE 	= 32;
	public static final int CATALOG_MODE 	= 64;
	public static final int CATALOG_HOST 	= 128;
	public static final int CATALOG_ID 		= 256;
	
		
	public void insert(DatabaseConnectionKeeper kdb, int flags) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "CatalogNode.insert() : insert() return null");
		
		try {
			catalogID = kdb.insertRow(true, names.get("CatalogTable"), lst);			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(catalogID != -1, "CatalogNode.update() : catalogID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "CatalogNode.insert() : insert() return null");

		try {
			kdb.updateRow( names.get("CatalogTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("catalogID"), catalogID) );						
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		}
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

	@Override
	public Integer getID() {
		return catalogID;
	}
	
	/**
	 * Make parameters list for insert in table 
	 * @param flags - define fields for insert, over filed must be have default value
	 */
	private List<RecordFeld>  makeListParam(int flags) {
		if(names == null)			
			return null;
				
		if(names.get("CatalogTable") == null)
			return null;
		
		List<RecordFeld> lst = new ArrayList<>();
		if(((flags & CATALOG_NAME) != 0) && (names.get("catalogName") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("catalogName"), catalogName));
		if(((flags & CATALOG_PARENT) != 0) && (names.get("catalogParent") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogParent"), catalogParent));
		if(((flags & CATALOG_ITEM) != 0) && (names.get("catalogItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogItem"), catalogItem));
		if(((flags & CATALOG_ITEM) != 0) && (names.get("catalogVariant") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogVariant"), catalogItem));
		if(((flags & CATALOG_CREATE) != 0) && (names.get("catalogCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("catalogCreate"), catalogCreate));
		if(((flags & CATALOG_REMOVE) != 0) && (names.get("catalogRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("catalogRemove"), catalogRemove));
		if(((flags & CATALOG_MODE) != 0) && (names.get("catalogMode") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogMode"), catalogMode));
		if(((flags & CATALOG_HOST) != 0) && (names.get("catalogHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("catalogHost"), catalogHost));
		if(((flags & CATALOG_ID) != 0) && (names.get("catalogID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("catalogID"), catalogID));
		
		return lst;
	}
}
