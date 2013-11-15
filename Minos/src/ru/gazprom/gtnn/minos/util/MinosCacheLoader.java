package ru.gazprom.gtnn.minos.util;

import java.lang.reflect.Field;
import java.util.Map;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

import com.google.common.cache.CacheLoader;


class MinosCacheLoaderExcetion extends Exception {
	private static final long serialVersionUID = 1L;

	public MinosCacheLoaderExcetion(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MinosCacheLoaderExcetion(String arg0) {
		super(arg0);
	}	
}

public class MinosCacheLoader<KeyT, ValueT> extends CacheLoader<KeyT, ValueT> {
	
	
	public MinosCacheLoader(Class<?> ValueT_class, // class ValueT type for instance objects
			DatabaseConnectionKeeper kdb, // object for manipulate DB  
			String sqlForLoadRow, // SQL request for load 1 or many rows from table
			String patternID,  // pattern id for replace
			Map<String, String> mapFieldOnColumn) {  //mapping field name on column table
			
		super();
		this.kdb = kdb;
		this.sqlForLoadRow = sqlForLoadRow;
		this.patternID = patternID;
		this.mapFieldOnColumn = mapFieldOnColumn;
		this.vclass = ValueT_class;
	}
	@Override
	public ValueT load(KeyT arg) throws Exception {			
		String sql = kdb.makeSQLString(sqlForLoadRow, patternID, arg.toString());
		if(sql == null)
			throw new MinosCacheLoaderExcetion("MinosCacheLoaderExcetion.load() : makeSQLString() return null");
		TableKeeper tk = kdb.selectRows(sql);
		if(tk.getRowCount() != 1) 
			throw new MinosCacheLoaderExcetion("MinosCacheLoaderExcetion.load() : selectRows() return incorrect row count (" + tk.getRowCount() + ")");
		
		ValueT inst = (ValueT) vclass.newInstance();
		if(inst == null)
			throw new MinosCacheLoaderExcetion("MinosCacheLoaderExcetion.load() : cannot create new object");
		
		if(!inst.getClass().isAnnotationPresent(TableName.class))
			throw new MinosCacheLoaderExcetion("MinosCacheLoaderExcetion.load() : class " + inst.getClass().getName() + " not contain annotaiton");
					
		for(Field f : inst.getClass().getFields()) {
			if(f.isAnnotationPresent(TableColumn.class)) {
				int cn = tk.getColumnNumberByName(mapFieldOnColumn.get(f.getName()), true);
				f.set(inst, tk.getValue(1, cn));				
			}
		}
		
		return inst;
	}

	private DatabaseConnectionKeeper kdb;
	private String sqlForLoadRow;
	private String patternID;
	private Map<String, String> mapFieldOnColumn;
	private Class<?> vclass;
}