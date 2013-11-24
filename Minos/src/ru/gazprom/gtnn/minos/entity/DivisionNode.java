package ru.gazprom.gtnn.minos.entity;

import java.util.List;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "DivisionTable")
public class DivisionNode extends BasicNode<Integer> {
	@TableColumn
	public int divisionID;
	@TableColumn
	public String divisionName;
	@TableColumn
	public int divisionParent;
	
	public List<Integer> subDivisions;
	
	@Override
	public String toString() {
		return divisionName; 
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof DivisionNode))
			return false;		
			
		return (this.divisionID == ((DivisionNode)obj).divisionID ? true : false);
	}

	@Override
	public int hashCode() {		
		return divisionID;
	}

	@Override
	public Integer getID() {		
		return divisionID;
	}
}
