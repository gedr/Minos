package ru.gazprom.gtnn.minos.entity;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "LevelTable")
public class LevelNode  extends BasicNode<Integer> {
	public static final int LEVEL_COUNT = 5;
	@TableColumn
	public int levelID;
	@TableColumn
	public String levelName;
	@TableColumn
	public double levelPrice;
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof LevelNode))
			return false;		
			
		return (this.levelID == ((LevelNode)obj).levelID ? true : false);
	}

	@Override
	public int hashCode() {		
		return levelID;
	}

	@Override
	public String toString() {
		return levelName;
	}

	@Override
	public Integer getID() {		
		return levelID;
	}
}
