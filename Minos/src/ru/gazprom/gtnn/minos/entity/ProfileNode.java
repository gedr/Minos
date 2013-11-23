package ru.gazprom.gtnn.minos.entity;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

@TableName(name = "ProfileTable")
public class ProfileNode {
	public enum ProfileType {UNKNOWN, ONLY_NAME, POSITION_DIVISION };
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
	public int profileVariant;
	@TableColumn
	public java.util.Date profileCreate;
	@TableColumn
	public java.util.Date profileRemove;
	@TableColumn
	public int profileCompetenceID;

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
	
	public ProfileType getProfileType() {
		switch(profileVariant) {
		case 10:
			return ProfileType.ONLY_NAME;
		case 1:
			return ProfileType.POSITION_DIVISION;

		}
		return ProfileType.UNKNOWN;
	}
}

