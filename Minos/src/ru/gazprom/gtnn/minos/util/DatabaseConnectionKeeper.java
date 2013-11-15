package ru.gazprom.gtnn.minos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
		Pattern p = Pattern.compile(patternStr);
		Matcher m = p.matcher(sourceStr);
		StringBuffer sb = new StringBuffer();
		int count = 0;
		while (m.find()) {
			m.appendReplacement(sb, replaceStr);
			count++;
		}
		return count > 0 ? sb.toString() : null;
	}

	
	/**
	 * <p>������� �� �������  
	 * @param sqlExpression - sql ���������
	 * @return TableRows -  
	 * @throws Exception
	 */ 
	public TableKeeper selectRows(String sqlExpression) throws Exception {
		if(sqlExpression == null)
			throw new IllegalArgumentException("DatabaseConnectionKeeper.selectRows() sqlExpression is null");
				
		Statement stmt = con.createStatement(); 
		ResultSet rs = stmt.executeQuery(sqlExpression);
		ResultSetMetaData rsmd = rs.getMetaData();
		if((rs.getRow() == 0) || (rsmd.getColumnCount() == 0)) {
			closeResultSet(rs);
			closeStatement(stmt);	
		}
				
		TableKeeper tk = new TableKeeper(rs.getRow(), rsmd.getColumnCount());
		
		// ��������� ������ �������� � �����
		for(int i = 1; i <= rsmd.getColumnCount(); i++) {
			tk.setColumnDescr(new TableKeeper.ColumnDescr(rsmd.getCatalogName(i), rsmd.getColumnType(i)), i);								
		}

		// ��������� ������ ���� ����� �������
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

				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.LONGVARCHAR:
					tk.setValue(rs.getString(i), rowNumber, i);
					break;

				case java.sql.Types.BINARY:
				case java.sql.Types.VARBINARY:
					tk.setValue(rs.getBytes(i), rowNumber, i);
					break;

				case java.sql.Types.LONGVARBINARY:
					tk.setValue(rs.getBinaryStream(i), rowNumber, i);
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