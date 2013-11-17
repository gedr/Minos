package ru.gazprom.gtnn.minos.entity;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "IndicatorTable")
public class IndicatorNode {
	@TableColumn
	public int indicatorID;
	@TableColumn
	public String indicatorName;
	@TableColumn
	public int indicatorItem;
	@TableColumn
	public int indicatorLevelID;
	@TableColumn
	public int indicatorCompetenceIncarnatio;

	@Override
	public String toString() {
		return indicatorName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof IndicatorNode))
			return false;		
			
		return (this.indicatorID == ((IndicatorNode)obj).indicatorID ? true : false);
	}

	@Override
	public int hashCode() {		
		return indicatorID;
	}
}
