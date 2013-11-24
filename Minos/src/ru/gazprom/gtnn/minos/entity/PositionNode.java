package ru.gazprom.gtnn.minos.entity;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "PersonTable")
public class PositionNode extends BasicNode<Integer> {
	@TableColumn
	public int positionID;
	@TableColumn
	public String positionName;			 

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if(!(obj instanceof PositionNode))
			return false;		
			
		return (this.positionID == ((PositionNode)obj).positionID ? true : false);
	}

	@Override
	public int hashCode() {		
		return positionID;
	}

	@Override
	public String toString() {
		return positionName + " < " + positionID + " > ";
	}

	@Override
	public Integer getID() {
		return positionID;
	}
}
