package ru.gazprom.gtnn.minos.util;

public class TableKeeper {
	
	public static class ColumnDescr {
		private String name;
		private int type;
		
		public ColumnDescr(String name, int type) {
			this.name = name;
			this.type = type;
		}
		
		public String getName() {
			return name;
		}
		public int getType() {
			return type;
		}
	}

	public TableKeeper(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		
		columns = new ColumnDescr[columnCount];
		tbl = new Object[rowCount][];
		for(int i = 0; i < rowCount; i++)
			tbl[i] = new Object[columnCount];		
	}
	
	/**
	 * The function set name and type for column
	 * @param columnName is column name
	 * @param columnType is column type from java.sql.Types;
	 * @param columnNumber is index of column (1 <= columnNumber <= columnCount)
	 */
	public void setColumnDescr(String columnName, int columnType, int columnNumber) throws IllegalArgumentException {
		if((columnNumber < 1) || (columnNumber > columnCount))
			throw new IllegalArgumentException("TableKeeper.setColumnDescr() get illegal value of columnIndex (" + columnNumber + ")");
		
		ColumnDescr cd = new ColumnDescr(columnName, columnType);
		columns[columnNumber - 1] = cd;
	}

	/**
	 * The function set name and type for column
	 * @param cd - filled object of ColumnDescr class
	 * @param columnNumber is index of column (1 <= columnNumber <= columnCount)
	 */
	public void setColumnDescr(ColumnDescr cd, int columnNumber) throws IllegalArgumentException {
		if((columnNumber < 1) || (columnNumber > columnCount))
			throw new IllegalArgumentException("TableKeeper.setColumnDescr() get illegal value of columnIndex (" + columnNumber + ")");		
		if(cd == null)
			throw new IllegalArgumentException("TableKeeper.setColumnDescr() get null value");		
		
		columns[columnNumber - 1] = cd;
	}
		
	/**
	 * The function set value in table on 
	 * @param rowNumber is row number (1 <= rowNumber <= rowCount)
	 * @param columnNumber is column number  (1 <= columnNumber <= columnCount)
	 */
	public void setValue(Object value, int rowNumber, int columnNumber)  throws IllegalArgumentException {
		if((columnNumber < 1) || (columnNumber > columnCount))
			throw new IllegalArgumentException("TableKeeper.setValue() get illegal value of columnNumber (" + columnNumber + ")");		

		if((rowNumber < 1) || (rowNumber > rowCount))
			throw new IllegalArgumentException("TableKeeper.setValue() get illegal value of rowNumber (" + rowNumber + ")");
		
		tbl[rowNumber - 1][columnNumber - 1] = value;
	}

	/**
	 * The function return ColumnDescr object use name of column
	 * @param name is compare string
	 * @param flagIgnoreCase is flag of case sensitivity 
	 * @return ColumnDescr object or null if name not exist
	 */
	public ColumnDescr getColumnDescrByName(String name, boolean flagIgnoreCase) {			
		for(ColumnDescr cd : columns) {				
			if(flagIgnoreCase ? name.equalsIgnoreCase(cd.name) : name.equals(cd.name)) {
				return cd;
			}				
		}
		return null;
	}
	
	/**
	 * The function return column number for column name equal name
	 * @param name is compare string
	 * @param flagIgnoreCase is flag of case sensitivity 
	 * @return if column not found then return -1? else return value [1, columnCount]
	 */
	public int getColumnNumberByName(String name, boolean flagIgnoreCase) {			
		for(int i = 0; i < columnCount; i++) {				
			if(flagIgnoreCase ? name.equalsIgnoreCase(columns[i].getName()) : name.equals(columns[i].getName())) {
				return i + 1;
			}				
		}
		return -1;
	}

	/**
	 * The function return ColumnDescr object use index of column
	 * @param columnNumber - is index of column (1 <= columnNumber <= columnCount)
	 * @return ColumnDescr object or null if name not exist
	 */
	public ColumnDescr getColumnDescrByIndex(int columnNumber) throws IllegalArgumentException {	
		if((columnNumber < 1) || (columnNumber > columnCount))
			throw new IllegalArgumentException("TableKeeper.setValue() get illegal value of columnNumber (" + columnNumber + ")");		

		return columns[columnNumber - 1]; 
	}

	/**
	 * The function return value from table  
	 * @param rowNumber is row number (1 <= rowNumber <= rowCount)
	 * @param columnNumber is column number  (1 <= columnNumber <= columnCount)
	 * @return
	 */
	public Object getValue(int rowNumber, int columnNumber) throws IllegalArgumentException  {
		if((columnNumber < 1) || (columnNumber > columnCount))
			throw new IllegalArgumentException("TableKeeper.getValue() get illegal value of columnNumber (" + columnNumber + ")");		

		if((rowNumber < 1) || (rowNumber > rowCount))
			throw new IllegalArgumentException("TableKeeper.getValue() get illegal value of rowNumber (" + rowNumber + ")");
		
		return tbl[rowNumber - 1][columnNumber - 1];
	}
	
	/**
	 * The function return count of columns 
	 * @return - number
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * The function return count of rows
	 * @return - number
	 */
	public int getRowCount() {
		return rowCount;
	}

	private Object [][] tbl;
	private ColumnDescr[] columns;
	private int rowCount;
	private int columnCount;	
}