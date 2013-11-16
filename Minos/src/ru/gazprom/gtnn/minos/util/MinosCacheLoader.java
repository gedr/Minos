package ru.gazprom.gtnn.minos.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;

import com.google.common.base.Preconditions;
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
		
	public MinosCacheLoader(Class<?> ValueT_class, // class ValueT type for instance new objects
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

		Preconditions.checkState(vclass.isAnnotationPresent(TableName.class), 
				"MinosCacheLoader.MinosCacheLoader() : class " + vclass.getName() + " not contain annotaiton");
	}
		
	@Override
	public ValueT load(KeyT arg) throws Exception {	
		System.out.println("load");
		String sql = kdb.makeSQLString(sqlForLoadRow, patternID, arg.toString());
		Preconditions.checkNotNull(sql, "MinosCacheLoader.load() : makeSQLString() return null");
		TableKeeper tk = kdb.selectRows(sql);
		Preconditions.checkNotNull(tk, "MinosCacheLoader.load() : selectRows() return null");
		Preconditions.checkState(tk.getRowCount() == 1, "MinosCacheLoader.load() : selectRows() return incorrect row count (" + tk.getRowCount() + ")");
		
		return createObjectAndFillFieldsFromTable(tk, 1);
	}
	
	@Override
	public Map<KeyT, ValueT> loadAll(Iterable<? extends KeyT> keys)
			throws Exception {
		System.out.println("loadAll");
		Preconditions.checkNotNull(keys, "MinosCacheLoader.loadAll() : argument is null");
		Map<KeyT, ValueT> map = new HashMap<>();
		
		boolean flagFirstElement = true;
		StringBuilder sb = new StringBuilder();
		for(KeyT key : keys) {
			sb.append(flagFirstElement ? "" : ", ").append(key);
			flagFirstElement = false;			
		}

		String sql = kdb.makeSQLString(sqlForLoadRow, patternID, sb.toString());
		Preconditions.checkNotNull(sql, "MinosCacheLoader.loadAll() : makeSQLString() return null");
		TableKeeper tk = kdb.selectRows(sql);
		Preconditions.checkNotNull(tk, "MinosCacheLoader.loadAll() : selectRows() return null");
		for(int i = 1; i <= tk.getRowCount(); i++) {			
			// в запросе 1 столбец всегда должен содeржать ключ элемента
			@SuppressWarnings("unchecked")
			KeyT k = (KeyT)tk.getValue(i, 1); 
			map.put(k, createObjectAndFillFieldsFromTable(tk, i));			
		}
		
		return map;
	}

		
	
	private ValueT createObjectAndFillFieldsFromTable(TableKeeper tk, int rowNumder) throws Exception {
		@SuppressWarnings("unchecked")
		ValueT inst = (ValueT) vclass.newInstance();
		Preconditions.checkNotNull(inst, "MinosCacheLoader.createAndFillObjectFieldsFromTable() : cannot create new object");
		
		for(Field f : inst.getClass().getFields()) {
			if(f.isAnnotationPresent(TableColumn.class)) {
				int cn = tk.getColumnNumberByName(mapFieldOnColumn.get(f.getName()), true);
				f.set(inst, tk.getValue(rowNumder, cn));				
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