package ru.gazprom.gtnn.minos.models.entity;

public class LevelNode {
	public int id;
	public String name;
	public double price;
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if(this.getClass() != obj.getClass())
			return false;		
			
		return (this.id == ((LevelNode)obj).id ? true : false);
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
