package ru.gazprom.gtnn.minos.entity;

public class TestRound {
	public int id;
	public String name;
	public String reason;
	
	
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
		
		if(obj.getClass() != this.getClass())
			return false;
		
		return (this.id == ((TestRound)obj).id ? true : false);
	}

	@Override
	public int hashCode() {		
		return id;
	}

}
