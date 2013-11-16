package ru.gazprom.gtnn.minos.entity;

public class IndicatorNode {
	public int id;
	public String name;
	public int item;
	public int levelID;
	public int competenceIncarnatio;

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
		
		if(this.getClass() != obj.getClass())
			return false;		
			
		return (this.id == ((IndicatorNode)obj).id ? true : false);
	}

	@Override
	public int hashCode() {		
		return id;
	}
}
