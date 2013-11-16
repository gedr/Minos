package ru.gazprom.gtnn.minos.entity;

public class PositionNode {
	public int id;
	public String name;			 

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if(this.getClass() != obj.getClass())
			return false;		
			
		return (this.id == ((PositionNode)obj).id ? true : false);
	}

	@Override
	public int hashCode() {		
		return id;
	}

	@Override
	public String toString() {
		return name;
	}

}
