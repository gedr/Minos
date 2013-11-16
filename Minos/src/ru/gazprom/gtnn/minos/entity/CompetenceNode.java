package ru.gazprom.gtnn.minos.entity;

import java.util.List;

public class CompetenceNode {
	public int id;
	public String name;
	public String descr;
	public int item;
	public int catalogID;
	public int incarnatio;
	public int chainNumber;
	public List<Integer> indicators;		

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof CompetenceNode))
			return false;		
			
		return (this.id == ((CompetenceNode)obj).id ? true : false);
	}

	@Override
	public int hashCode() {		
		return id;
	}
}
