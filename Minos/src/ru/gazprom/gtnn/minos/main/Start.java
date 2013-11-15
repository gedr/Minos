package ru.gazprom.gtnn.minos.main;


import com.google.common.cache.*;

import ru.gazprom.gtnn.minos.models.entity.*;
import ru.gazprom.gtnn.minos.util.*;


public class Start {
	
	
	
	public static void check() {
		CacheLoader<Integer, PersonNode> cl = new MinosCacheLoader<Integer, PersonNode>(PersonNode.class, 
				new DatabaseConnectionKeeper(null, null, null),
				null,
				null,
				null);
		
		LoadingCache<Integer, PersonNode> cachePerson = CacheBuilder.
				newBuilder().
				build(cl);
		
		
	}
	
	
		
	
	public static void main(String[] args) {
		//PersonNode node =  new PersonNode();
		
		try {
			check();
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

