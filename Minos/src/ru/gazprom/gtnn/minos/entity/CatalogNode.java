package ru.gazprom.gtnn.minos.entity;

import java.util.Date;
import java.util.List;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "CatalogTable")
public class CatalogNode {
	@TableColumn
	public int catalogID;
	@TableColumn
	public String catalogName;
	@TableColumn
	public int catalogParent;
	@TableColumn
	public int catalogItem;
	@TableColumn
	public Date catalogCreate;
	@TableColumn
	public Date catalogRemove;
	
	public List<Integer> subCatalogs;

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
