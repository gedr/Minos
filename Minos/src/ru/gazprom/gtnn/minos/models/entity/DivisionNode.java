package ru.gazprom.gtnn.minos.models.entity;

import java.util.List;

public class DivisionNode {
	public int id;		
	public String name;		
	public int parent;
	public List<Integer> subDivisions;
	
	@Override
	public String toString() {
		return name; // + " (id:" + id + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if(this.getClass() != obj.getClass())
			return false;		
			
		return (this.id == ((DivisionNode)obj).id ? true : false);
	}

	@Override
	public int hashCode() {		
		return id;
	}
}
