package ru.gazprom.gtnn.minos.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper.RecordFeld;

@TableName(name = "StringAttrTable")
public class StringAttrNode extends BasicNode<Integer>{
	@TableColumn
	public int stringAttrID;
	@TableColumn
	public int stringAttrItem;
	@TableColumn
	public String stringAttrValue;
	@TableColumn
	public String stringAttrDescr;
	@TableColumn
	public int stringAttrVariety;
	@TableColumn
	public int stringAttrHost;
	@TableColumn
	public int stringAttrExternalID1;
	@TableColumn
	public int stringAttrExternalID2;
	@TableColumn
	public int stringAttrExternalID3;
	@TableColumn
	public java.util.Date stringAttrCreate;
	@TableColumn
	public java.util.Date stringAttrRemove;

	public static final int VARIETY_PROFILE		= 1;
	public static final int VARIETY_COMPETENCE	= 2;
	
	public static final int STRING_ATTR_VALUE 			= 1;
	public static final int STRING_ATTR_DESCR 			= 2;
	public static final int STRING_ATTR_VARIETY 		= 4;
	public static final int STRING_ATTR_EXTERNAL_ID1 	= 8;
	public static final int STRING_ATTR_EXTERNAL_ID2 	= 16;
	public static final int STRING_ATTR_EXTERNAL_ID3 	= 32;	
	public static final int STRING_ATTR_HOST		 	= 64;

	public static final int STRING_ATTR_CREATE			= 128;
	public static final int STRING_ATTR_REMOVE			= 256;

	public static final int STRING_ATTR_ID				= 512;
	public static final int STRING_ATTR_ITEM			= 1024;
	
	public void insert(DatabaseConnectionKeeper kdb, int flags) throws Exception{
		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "StringAttrNode.insert() : makeListParam() return null");
		
		try {
			stringAttrID = kdb.insertRow(true, names.get("StringAttrTable"), lst);			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void update(DatabaseConnectionKeeper kdb, int flags) throws Exception {
		Preconditions.checkArgument(stringAttrID != -1, "StringAttrNode.update() : stringAttrID field have incorrect value");

		List<RecordFeld> lst = makeListParam(flags);
		Preconditions.checkNotNull(lst, "StringAttrNode.insert() : makeListParam() return null");

		try {
			kdb.updateRow( names.get("StringAttrTable"), lst, new RecordFeld(java.sql.Types.INTEGER, names.get("stringAttrID"), stringAttrID) );						
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
				
		if(names.get("StringAttrTable") == null)
			return null;
		
		List<RecordFeld> lst = new ArrayList<>();
		if(((flags & STRING_ATTR_VALUE) != 0) && (names.get("stringAttrValue") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("stringAttrValue"), stringAttrValue));
		
		if(((flags & STRING_ATTR_DESCR) != 0) && (names.get("stringAttrDescr") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("stringAttrDescr"), stringAttrDescr));
		
		if(((flags & STRING_ATTR_VARIETY) != 0) && (names.get("stringAttrVariety") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("stringAttrVariety"), stringAttrVariety));
		
		if(((flags & STRING_ATTR_EXTERNAL_ID1) != 0) && (names.get("stringAttrExternalID1") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("stringAttrExternalID1"), stringAttrExternalID1));
		if(((flags & STRING_ATTR_EXTERNAL_ID2) != 0) && (names.get("stringAttrExternalID2") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("stringAttrExternalID2"), stringAttrExternalID2));
		if(((flags & STRING_ATTR_EXTERNAL_ID3) != 0) && (names.get("stringAttrExternalID3") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("stringAttrExternalID3"), stringAttrExternalID3));
		
		if(((flags & STRING_ATTR_CREATE) != 0) && (names.get("stringAttrCreate") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("stringAttrCreate"), stringAttrCreate));
		if(((flags & STRING_ATTR_REMOVE) != 0) && (names.get("stringAttrRemove") != null) )
			lst.add(new RecordFeld(java.sql.Types.DATE, names.get("stringAttrRemove"), stringAttrRemove));

		if(((flags & STRING_ATTR_HOST) != 0) && (names.get("stringAttrHost") != null) )
			lst.add(new RecordFeld(java.sql.Types.VARCHAR, names.get("stringAttrHost"), stringAttrHost));

		if(((flags & STRING_ATTR_ID) != 0) && (names.get("stringAttrID") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("stringAttrID"), stringAttrID));

		if(((flags & STRING_ATTR_ITEM) != 0) && (names.get("stringAttrItem") != null) )
			lst.add(new RecordFeld(java.sql.Types.INTEGER, names.get("stringAttrItem"), stringAttrItem));

		return lst;
	}

	
	@Override
	public Integer getID() {
		return stringAttrID;
	}
	
	@Override
	public String toString() {
		return (stringAttrDescr == null ? "" : stringAttrDescr) + " : " + 
				(stringAttrValue == null ? "" : stringAttrValue);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) 
			return false;
		if(obj == this)
			return true;
		
		if( !(obj instanceof StringAttrNode) )
			return false;
		
		return (this.stringAttrID == ((StringAttrNode)obj).stringAttrID ? true : false);
	}

	@Override
	public int hashCode() {		
		return stringAttrID;
	}


	

}
