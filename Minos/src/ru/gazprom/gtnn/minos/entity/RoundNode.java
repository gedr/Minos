package ru.gazprom.gtnn.minos.entity;

public class RoundNode {
	public int roundID;
	public String roundName;
	public String roundDescr;
	public java.util.Date roundCreate;
	public java.util.Date roundRemove;
	
	
	@Override
	public String toString() {
		return roundName; 
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof RoundNode))
			return false;
		
		return (this.roundID == ((RoundNode)obj).roundID ? true : false);
	}

	@Override
	public int hashCode() {		
		return roundID;
	}

}
