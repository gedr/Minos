package ru.gazprom.gtnn.minos.util;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;


public class DatabaseConnectionKeeper {	

	public  DatabaseConnectionKeeper(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;		
	}	
	
	public void connect() throws SQLException {
		con = DriverManager.getConnection(url, user, password);
	}
	
	/**
	 * Make SQL string for execute
	 * @param sourceStr is string with pattern
	 * @param patternStr is pattern string
	 * @param replaceStr is substitute string
	 * @return string of SQL for execute or null
	 */
	public String makeSQLString(String sourceStr, String patternStr, String replaceStr) {
		StringBuilder sb = new StringBuilder();
		sb.append(sourceStr);
		
		int count = 0;
		int ind;		
		while((ind = sb.indexOf(patternStr)) != -1) {
			sb.delete(ind,  ind + patternStr.length());
			sb.insert(ind,  replaceStr);
			count++;
		}
			
		return count > 0 ? sb.toString() : null;
	}
	
	/**
	 * <p>Выборка из таблицы  
	 * @param sqlExpression - sql выражение
	 * @return TableRows -  
	 * @throws Exception
	 */ 
	public TableKeeper selectRows(String sqlExpression) throws Exception {
		if(sqlExpression == null)
			throw new IllegalArgumentException("DatabaseConnectionKeeper.selectRows() sqlExpression is null");
				
		Statement stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = stmt.executeQuery(sqlExpression);
		int rowCount = 0;
		if(rs.last()){
			rowCount = rs.getRow();
			rs.beforeFirst();
		}		
		
		ResultSetMetaData rsmd = rs.getMetaData();
		if((rowCount == 0) || (rsmd.getColumnCount() == 0)) {
			closeResultSet(rs);
			closeStatement(stmt);
			return null;
		}
				
		TableKeeper tk = new TableKeeper(rowCount, rsmd.getColumnCount());
		
		// формируем список столбцов и типов
		for(int i = 1; i <= rsmd.getColumnCount(); i++) {
			tk.setColumnDescr(new TableKeeper.ColumnDescr(rsmd.getColumnName(i), rsmd.getColumnType(i)), i);								
		}

		// формируем список всех строк выборки
		int rowNumber = 0;
		while(rs.next()) {
			rowNumber++;
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {				
				switch(rsmd.getColumnType(i)) {					
				case java.sql.Types.CLOB:
					tk.setValue(rs.getClob(i), rowNumber, i);										
					break;
				case java.sql.Types.BLOB:
					tk.setValue(rs.getBlob(i), rowNumber, i);										
					break;

				case java.sql.Types.TIMESTAMP:
					tk.setValue(rs.getTimestamp(i), rowNumber, i);				
					break;					

				case java.sql.Types.DATE:		
					tk.setValue(rs.getDate(i), rowNumber, i);										
					break;					

				case java.sql.Types.DECIMAL:
				case java.sql.Types.NUMERIC:
					tk.setValue(rs.getBigDecimal(i), rowNumber, i);				
					break;					

				case java.sql.Types.FLOAT:
				case java.sql.Types.DOUBLE:
					tk.setValue(rs.getDouble(i), rowNumber, i);				
					break;					

				case java.sql.Types.REAL:
					tk.setValue(rs.getFloat(i), rowNumber, i);						
					break;

				case java.sql.Types.BIGINT:											
					tk.setValue(rs.getLong(i), rowNumber, i);
					break;

				case java.sql.Types.INTEGER:
					tk.setValue(rs.getInt(i), rowNumber, i);
					break;

				case java.sql.Types.TINYINT:
				case java.sql.Types.SMALLINT:
					tk.setValue(rs.getShort(i), rowNumber, i);
					break;

				case java.sql.Types.BIT:
				case java.sql.Types.BOOLEAN:
					tk.setValue(rs.getBoolean(i), rowNumber, i);
					break;

				case java.sql.Types.BINARY:
				case java.sql.Types.VARBINARY:
					tk.setValue(rs.getBytes(i), rowNumber, i);
					break;

				case java.sql.Types.LONGVARBINARY:
					tk.setValue(rs.getBinaryStream(i), rowNumber, i);
					break;
					
				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.NCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.LONGNVARCHAR:					
					tk.setValue(rs.getString(i), rowNumber, i);
					break;

				default:
					throw new IllegalArgumentException("DatabaseConnectionKeeper.selectRows() unknown sql type : " + rsmd.getColumnType(i));
				}				
			}			
		}				
		closeResultSet(rs);
		closeStatement(stmt);
		return tk;
	}

	public static class RecordFeld {
		public int dataType;
		public String columnName;
		public Object val;
		
		public RecordFeld(int dataType, String columnName, Object val) {
			this.dataType = dataType;
			this.columnName = columnName;
			this.val = val;
		}
	};
	
	public int insertRow(boolean flagHaveAutoIncrementKey, String tableName, List<RecordFeld> vals) throws Exception {
		String sql = makePreparedSQL(tableName, vals);
		PreparedStatement stmt = (!flagHaveAutoIncrementKey ? con.prepareStatement(sql)
				: con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS) );
		
		for(int i = 0; i < vals.size(); i++) {
			//System.out.println(vals.get(i).val);
		
			switch(vals.get(i).dataType) {					
			case java.sql.Types.CLOB:
				stmt.setClob(i + 1, (Reader)vals.get(i).val);														
				break;
			case java.sql.Types.BLOB:
				stmt.setBlob(i + 1, (InputStream)vals.get(i).val);														
				break;

			case java.sql.Types.TIMESTAMP:
				stmt.setTimestamp(i + 1, (Timestamp)vals.get(i).val);								
				break;					

			case java.sql.Types.DATE:
				java.sql.Date dt  = null;
				if(vals.get(i).val instanceof java.util.Date)
					dt = new java.sql.Date( ((java.util.Date)vals.get(i).val).getTime() );
				else 
					if(vals.get(i).val instanceof java.sql.Date)
						dt = (java.sql.Date)vals.get(i).val;

				stmt.setDate(i + 1, dt);									
				break;					

			case java.sql.Types.DECIMAL:
			case java.sql.Types.NUMERIC:
				stmt.setBigDecimal(i + 1, (BigDecimal)vals.get(i).val);								
				break;					

			case java.sql.Types.FLOAT:
			case java.sql.Types.DOUBLE:
				stmt.setDouble(i + 1, (Double)vals.get(i).val);								
				break;					

			case java.sql.Types.REAL:
				stmt.setFloat(i + 1, (Float)vals.get(i).val);										
				break;

			case java.sql.Types.BIGINT:					
				stmt.setLong(i + 1, (Long)vals.get(i).val);				
				break;

			case java.sql.Types.INTEGER:
				stmt.setInt(i + 1, (Integer)vals.get(i).val);
				break;

			case java.sql.Types.TINYINT:
			case java.sql.Types.SMALLINT:
				stmt.setShort(i + 1, (Short)vals.get(i).val);				
				break;

			case java.sql.Types.BIT:
			case java.sql.Types.BOOLEAN:
				stmt.setBoolean(i + 1, (Boolean)vals.get(i).val);				
				break;

			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
			case java.sql.Types.LONGVARCHAR:
			case java.sql.Types.NCHAR:
			case java.sql.Types.NVARCHAR:
			case java.sql.Types.LONGNVARCHAR:
				stmt.setString(i + 1, (String)vals.get(i).val);				
				break;

			default:
				throw new IllegalArgumentException("DatabaseConnectionKeeper.insertRow() unknown sql type : " + vals.get(i).dataType);
			}				

		}
		stmt.executeUpdate();
		
		int key = 0;
		if(flagHaveAutoIncrementKey) {
			ResultSet keys = stmt.getGeneratedKeys(); 
			keys.next(); 
			key = keys.getInt(1);
			closeResultSet(keys);
		}

		closeStatement(stmt);		
		return key;
	}
	
	public int updateRow(String tableName, List<RecordFeld> vals, RecordFeld whereVal) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(tableName).append(" SET ");
		boolean flagFirst = true;
		for(int i = 0; i < vals.size(); i++) {
			sb.append(flagFirst ? "" : " , ").append(vals.get(i).columnName).append(" = ? ");
			flagFirst = false;
		}
		sb.append(" WHERE ").append(whereVal.columnName).append(" = ? ");

		PreparedStatement stmt = con.prepareStatement(sb.toString());

		for(int i = 0; i < vals.size(); i++) {
			stmt.setObject(i + 1, vals.get(i).val);	
		}
		stmt.setObject(vals.size() + 1, whereVal.val);
		
		int result = stmt.executeUpdate();
		closeStatement(stmt);
		
		return result;
	}
	
	private String makePreparedSQL(String tableName, List<RecordFeld> vals) {
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		sb1.append(" INSERT INTO ").append(tableName).append(" ( ");
		sb2.append(" VALUES ( ");
		boolean flagFirst = true;
		for(int i = 0; i < vals.size(); i++) {
			sb1.append(flagFirst ? "" : " , ").append(vals.get(i).columnName);
			sb2.append(flagFirst ? "" : " , ").append(" ? ");
			flagFirst = false;
		}
		sb1.append(" ) ");
		sb2.append(" ) ");
		
		return sb1.append(sb2).toString();
	}
	
	
	private void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();					
			} catch(SQLException e) {
				System.err.println(e);
			}
		}
	}
	
	private void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch(SQLException e) {
				System.err.println(e);
			}
		}
	}
	
	private Connection con = null;
	private String url, user, password;
}
