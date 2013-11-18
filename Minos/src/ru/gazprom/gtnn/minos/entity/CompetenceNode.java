package ru.gazprom.gtnn.minos.entity;

import java.util.Date;
import java.util.List;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

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
	public int competenceCatalogID;
	@TableColumn
	public int competenceIncarnatio;
	@TableColumn
	public int competenceChainNumber;
	@TableColumn
	public java.sql.Date competenceCreate;
	@TableColumn
	public java.sql.Date competenceRemove;
	
	
	public List<Integer> indicators;		

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
