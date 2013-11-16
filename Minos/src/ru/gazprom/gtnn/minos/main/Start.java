package ru.gazprom.gtnn.minos.main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.cache.*;

import ru.gazprom.gtnn.minos.entity.*;
import ru.gazprom.gtnn.minos.util.*;


public class Start {
	

		
	
	public static void main(String[] args) {
		//PersonNode node =  new PersonNode();
		
		try {
			
			Map<String, String> map = new HashMap<>();
			map.put("personID", "tPersonaId");
			map.put("personSurname", "F");
			map.put("personName", "I");
			map.put("personPatronymic", "O");
			map.put("personBirthDate", "Drojd");
			map.put("personSex", "Sex");
			
			
			String connectionUrl = "jdbc:sqlserver://192.168.56.2:1433;databaseName=serg;user=sa;password=Q11W22e33;";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			DatabaseConnectionKeeper kdb = new DatabaseConnectionKeeper(connectionUrl, null, null);
			kdb.connect();
			
			LoadingCache<Integer, PersonNode> cachePerson = CacheBuilder.
					newBuilder().
					build(new MinosCacheLoader<Integer, PersonNode>(PersonNode.class, kdb, 
							"select tPersonaId, F, I, O, Drojd, Sex from tPersona where tPersonaId in (%id%)",
							"%id%", map));

			TableKeeper tk = kdb.selectRows("select top 100 tPersonaId from tPersona");
			List<Integer> lst = new ArrayList<>();			
			for(int i = 1; i <= tk.getRowCount(); i++)
				lst.add((Integer)tk.getValue(i,  1));
			cachePerson.getAll(lst);
				
							
			for(int i = 1; i <= tk.getRowCount(); i++ ) {				
				System.out.println(cachePerson.get((Integer)tk.getValue(i, 1)));
			}
			
			System.out.println("refresh");
			cachePerson.refresh((Integer)tk.getValue(1, 1));
			System.out.println(cachePerson.get((Integer)tk.getValue(1, 1)));
			
			
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

}

