package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "CompetenceTable")
public class CompetenceNode extends BasicNode<Integer> {
	@TableColumn
	public int competenceID;
	@TableColumn
	public String competenceName;
	@TableColumn
	public String competenceDescr;
	@TableColumn
	public int competenceItem;
	@TableColumn
	public int competenceVariant;	
	@TableColumn
	public String competenceHost;
	@TableColumn
	public int competenceMode;
	@TableColumn
	public int competenceCatalogID;
	@TableColumn
	public int competenceIncarnatio;
	@TableColumn
	public int competenceChainNumber;
	@TableColumn
	public java.util.Date competenceCreate;
	@TableColumn
	public java.util.Date competenceRemove;
		
	public List<Integer> indicators;		
	
	public static final int COMPETENCE_NAME = 1;
	public static final int COMPETENCE_DESCR = 2;
	public static final int COMPETENCE_ITEM = 4;
	public static final int COMPETENCE_VARIANT = 8;
	public static final int COMPETENCE_HOST = 16;
	public static final int COMPETENCE_MODE = 32;
	public static final int COMPETENCE_CATALOG = 64;
	public static final int COMPETENCE_INCARNATIO = 128;
	public static final int COMPETENCE_CHAIN_NUMBER = 256;
	public static final int COMPETENCE_CREATE = 512;
	public static final int COMPETENCE_REMOVE = 1024;
	public static final int COMPETENCE_ID = 2048;
		
	public void insert(DatabaseConnectionKeeper kdb, int flags, boolean flagNevCompetence) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "CompetenceNode.insert() : makeListParam() return null");
		
		try {
			competenceID = kdb.insertRow(true, names.get("CompetenceTable"), lst);
			if (flagNevCompetence) {
				competenceIncarnatio = competenceID;
				update(kdb, COMPETENCE_INCARNATIO);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(lst != null)
				lst.clear();			
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(competenceID != -1, "CompetenceNode.update() : competenceID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "CompetenceNode.update() : makeListParam() return null");

		try {
			kdb.updateRow( names.get("CompetenceTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("competenceID"), competenceID) );						
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		} finally {
			if(lst != null)
				lst.clear();			
		}
	}	
	
	/**
	 * Make parameters list for insert in table 
	 * @param flags - define fields for insert, over filed must be have default value
	 */
	private List<RecordFeld>  makeListParam(int flags) {
		if(names == null)			
			return null;
				
		if(names.get("CompetenceTable") == null)
			return null;
		
		List<RecordFeld> lst = new ArrayList<>();

		if(((flags & COMPETENCE_NAME) != 0) && (names.get("competenceName") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("competenceName"), competenceName));
		if(((flags & COMPETENCE_DESCR) != 0) && (names.get("competenceDescr") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("competenceDescr"), competenceDescr));
		if(((flags & COMPETENCE_HOST) != 0) && (names.get("competenceHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("competenceHost"), competenceHost));

		if(((flags & COMPETENCE_ITEM) != 0) && (names.get("competenceItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceItem"), competenceItem));
		if(((flags & COMPETENCE_ITEM) != 0) && (names.get("competenceVariant") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceVariant"), competenceVariant));
		if(((flags & COMPETENCE_MODE) != 0) && (names.get("competenceMode") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceMode"), competenceMode));
		if(((flags & COMPETENCE_CATALOG) != 0) && (names.get("competenceCatalogID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceCatalogID"), competenceCatalogID));
		if(((flags & COMPETENCE_INCARNATIO) != 0) && (names.get("competenceIncarnatio") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceIncarnatio"), competenceIncarnatio));
		if(((flags & COMPETENCE_CHAIN_NUMBER) != 0) && (names.get("competenceChainNumber") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceChainNumber"), competenceChainNumber));
		if(((flags & COMPETENCE_ID) != 0) && (names.get("competenceID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("competenceID"), competenceID));
		
		if(((flags & COMPETENCE_CREATE) != 0) && (names.get("competenceCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("competenceCreate"), competenceCreate));
		if(((flags & COMPETENCE_REMOVE) != 0) && (names.get("competenceRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("competenceRemove"), competenceRemove));		
		return lst;
	}
		
	@Override
	public String toString() {
		return competenceName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof CompetenceNode))
			return false;		
			
		return (this.competenceID == ((CompetenceNode)obj).competenceID ? true : false);
	}

	@Override
	public int hashCode() {		
		return competenceID;
	}

	@Override
	public Integer getID() {
		return competenceID;
	}
}
