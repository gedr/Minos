package ru.gazprom.gtnn.minos.main;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.reflect.Reflection;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.annotations.TableName;
import ru.gazprom.gtnn.minos.models.entity.*;
import ru.gazprom.gtnn.minos.util.KeeperDB;

public class Start {
	
	
	class MinosCacheLoader<KeyT, ValueT> extends CacheLoader<KeyT, ValueT> {			
		public MinosCacheLoader(Class<?> ValueT_class, // class ValueT type for instance objects
				KeeperDB kdb, // object for manipulate DB  
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
			ValueT inst = (ValueT) vclass.newInstance();
			String sql = kdb.makeSQLString(sqlForLoadRow, patternID, arg.toString());
			return null;
		}
		/*
		protected TableRows executeRequest(KeyT arg) throws Exception{
			Pair<Integer, String> p = lkdb.makeSQLString(request, 
					columnNameMap.get("where"), 
					arg.toString());
			if(p.getFirst() != 1) 
				throw new IncorrectFindingException("MyCacheLoader.executeRequest() : makeRequest() return illegal value");

			return lkdb.selectRows(p.getSecond());
		}
*/
		private KeeperDB kdb;
		private String sqlForLoadRow;
		private String patternID;
		private Map<String, String> mapFieldOnColumn;
		private Class<?> vclass;
	}

	cacheDivision = CacheBuilder.
			newBuilder().
			build(new MyCacheLoader<Integer, DivisionNode>(kdb, sqlCommands.get("loadDivisionByID"), columnMirror) {

				@Override
				public DivisionNode load(Integer arg) throws Exception {							
					TableRows tr = executeRequest(arg);
					if(tr.getRowCount() != 1) 
						throw new Exception("cacheCatalog.load() : request return illegal count rows");

					DivisionNode node = new DivisionNode();
					node.id = (Integer)tr.getValue(0, columnNameMap.get("divisionID"), true);
					node.parent = (Integer)tr.getValue(0, columnNameMap.get("divisionParent"), true);
					node.name = (String)tr.getValue(0, columnNameMap.get("divisionName"), true);						
					node.subDivisions = null;
					tr.close();
					return node;
				}
			});
	
	
	public static <K, V> CacheLoader<K, V> getCL() {
		CacheLoader<K, V> cl = new CacheLoader<K, V>() {

			@Override
			public V load(K arg0) throws Exception {
				//V v =
				
				V.class.getConstructor().newInstance();
				return null;
			}
		};
			
		
		return null;
	}
	
	public static <T> T ret(Class<?> t) {
	    try {
	    	
	    	T inst = (T)t.newInstance();
	    	return inst;

        } catch (java.lang.Exception e) {
           e.printStackTrace();
        }
		return null;
	}
	
	public static void getCL(Object obj) throws Exception {
		System.out.println(obj.getClass().getSimpleName());
		System.out.println("Contain annotation TableName : " + obj.getClass().isAnnotationPresent(TableName.class));
		if(obj.getClass().isAnnotationPresent(TableName.class)) {
			TableName a = obj.getClass().getAnnotation(TableName.class);
			System.out.println(a.name() );
		}

		for(Field f : obj.getClass().getFields()) {
			System.out.print(f.getName() + "   :   ");
			System.out.print(f.getType().getSimpleName() + "   :   ");
			System.out.println("Contain annotation TableColumn : " + f.isAnnotationPresent(TableColumn.class));

			switch(f.getType().getSimpleName()) {
			case "int" :
				f.set(obj, Integer.valueOf(10));
				break;
			case "String" :
				f.set(obj, "это строка");
				break;
			case "Date":
				f.set(obj, new Date());
				break;			
			}
		}
			
			

			
	}

	public static void main(String[] args) {
		//PersonNode node =  new PersonNode();
		
		try {
			PersonNode node =  ret(PersonNode.class);	
			getCL(node);
			/*
			System.out.println(node.getClass().getSimpleName());
			System.out.println("Contain annotation TableName : " + node.getClass().isAnnotationPresent(TableName.class));
			if(node.getClass().isAnnotationPresent(TableName.class)) {
				TableName a = node.getClass().getAnnotation(TableName.class);
				System.out.println(a.name() );
			}
			
			for(Field f : node.getClass().getFields()) {
				System.out.print(f.getName() + "   :   ");
				System.out.print(f.getType().getSimpleName() + "   :   ");
				System.out.println("Contain annotation TableColumn : " + f.isAnnotationPresent(TableColumn.class));
				
				switch(f.getType().getSimpleName()) {
				case "int" :
					f.set(node, Integer.valueOf(10));
					break;
				case "String" :
					f.set(node, "это строка");
					break;
				case "Date":
					f.set(node, new Date());
					break;
				
				}
				
				
			}
			*/
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		//System.out.println(node);

	}

}
