package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "ProfileTable")
public class ProfileNode extends BasicNode<Integer>{
	@TableColumn
	public int profileID;
	@TableColumn
	public String profileName;
	@TableColumn
	public String profileHost;
	@TableColumn
	public int profileItem;
	@TableColumn
	public int profileDivisionID;
	@TableColumn
	public int profilePositionID;
	@TableColumn
	public int profilePositionBID;
	@TableColumn
	public int profileMinLevel;
	@TableColumn
	public int profileVariety;
	@TableColumn
	public java.util.Date profileCreate;
	@TableColumn
	public java.util.Date profileRemove;
	@TableColumn
	public int profileCompetenceID;
	@TableColumn
	public int profileCompetenceIncarnatio;
	
	public List<Integer> lstStringAttr;

	public static final int PROFILE_NAME = 1;
	public static final int PROFILE_ITEM = 2;
	public static final int PROFILE_HOST = 4;
	
	public static final int PROFILE_DIVISION = 8;
	public static final int PROFILE_POSITION = 16;
	public static final int PROFILE_POSITIONB = 32;
	public static final int PROFILE_MIN_LEVEL = 64;
	public static final int PROFILE_VARIETY = 128;
	public static final int PROFILE_COMPETENCE_INCARNATIO = 256;
	
	public static final int PROFILE_CREATE = 512;
	public static final int PROFILE_REMOVE = 1024;	
	public static final int PROFILE_ID = 2048;
	
	public void insert(DatabaseConnectionKeeper kdb, int flags) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "ProfileNode.insert() : makeListParam() return null");
		
		try {
			profileID = kdb.insertRow(true, names.get("ProfileTable"), lst);			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(profileID != -1, "ProfileNode.update() : catalogID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "ProfileNode.update() : makeListParam() return null");

		try {
			kdb.updateRow( names.get("ProfileTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("profileID"), profileID) );						
		} catch (Exception e) {
			e.printStackTrace();			
			throw e;
		}
	}	
	
	/**
	 * Make parameters list for insert in table 
	 * @param flags - define fields for insert, over filed must be have default value
	 */
	private List<RecordFeld>  makeListParam(int flags) {
		if(names == null)			
			return null;
				
		if(names.get("ProfileTable") == null)
			return null;
		
		List<RecordFeld> lst = new ArrayList<>();

		if(((flags & PROFILE_NAME) != 0) && (names.get("profileName") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("profileName"), profileName));
		if(((flags & PROFILE_HOST) != 0) && (names.get("profileHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("profileHost"), profileHost));

		if(((flags & PROFILE_ITEM) != 0) && (names.get("profileItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("profileItem"), profileItem));
		if(((flags & PROFILE_DIVISION) != 0) && (names.get("profileDivisionID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("profileDivisionID"), profileDivisionID));
		if(((flags & PROFILE_POSITION) != 0) && (names.get("profilePositionID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("profilePositionID"), profilePositionID));
		if(((flags & PROFILE_POSITIONB) != 0) && (names.get("profilePositionBID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("profilePositionBID"), profilePositionBID));
		if(((flags & PROFILE_MIN_LEVEL) != 0) && (names.get("profileMinLevel") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("profileMinLevel"), profileMinLevel));
		if(((flags & PROFILE_VARIETY) != 0) && (names.get("profileVariety") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("profileVariety"), profileVariety));
		if(((flags & PROFILE_COMPETENCE_INCARNATIO) != 0) && (names.get("profileCompetenceIncarnatio") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("profileCompetenceIncarnatio"), profileCompetenceIncarnatio));
		if(((flags & PROFILE_ID) != 0) && (names.get("profileID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("profileID"), profileID));
		
		if(((flags & PROFILE_CREATE) != 0) && (names.get("profileCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("profileCreate"), profileCreate));
		if(((flags & PROFILE_REMOVE) != 0) && (names.get("profileRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("profileRemove"), profileRemove));		
		return lst;
	}
	
	
	/*
	 * 	@TableColumn
	public int profileCompetenceIncanatio;
	 */
	@Override
	public String toString() {
		return profileName + "  [ min level : " + profileMinLevel +" ] ";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof ProfileNode) )
			return false;
		
		return (this.profileID == ((ProfileNode)obj).profileID ? true : false);
	}

	@Override
	public int hashCode() {		
		return profileID;
	}

	@Override
	public Integer getID() {
		return profileID;		
	}
}

