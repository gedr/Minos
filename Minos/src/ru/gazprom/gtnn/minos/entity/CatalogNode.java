package ru.gazprom.gtnn.minos.entity;

import java.util.List;

public class CatalogNode {
	public int id;
	public String name;
	public int parent;
	public int item;
	public List<Integer> subCatalogs;

	@Override
	public String toString() {
		return name; // + (catalogs == null ? "  :  catalogs is null" : "  :  catalog.size() = " + catalogs.size());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if(obj.getClass() != this.getClass())
			return false;
		
		return (this.id == ((CatalogNode)obj).id ? true : false);
	}

	@Override
	public int hashCode() {		
		return id;
	}
}
