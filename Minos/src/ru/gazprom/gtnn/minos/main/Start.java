package ru.gazprom.gtnn.minos.main;


import java.awt.EventQueue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreeModel;

import com.google.common.cache.*;

import ru.gazprom.gtnn.minos.annotations.TableColumn;
import ru.gazprom.gtnn.minos.entity.*;
import ru.gazprom.gtnn.minos.models.CatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceModel;
import ru.gazprom.gtnn.minos.models.DivisionModel;
import ru.gazprom.gtnn.minos.models.MyTransferHandler;
import ru.gazprom.gtnn.minos.models.PersonInDivisionModel;
import ru.gazprom.gtnn.minos.models.PositionInDivisionModel;
import ru.gazprom.gtnn.minos.util.*;


public class Start {
	

	public static void makeUI() {

		EventQueue.invokeLater(new Runnable() {
			public void run() {

				Map<String, String> map = new HashMap<>();
				map.put("divisionID", "tOrgStruID");
				map.put("divisionParent", "Parent");
				map.put("divisionName", "FullName");

				map.put("positionID", "tStatDolSPId");
				map.put("positionName", "FullTXT");

				map.put("personID", "tPersonaId");
				map.put("personSurname", "F");
				map.put("personName", "I");
				map.put("personPatronymic", "O");
				map.put("personBirthDate", "Drojd");
				map.put("personSex", "Sex");
				
				map.put("catalogID", "id");
				map.put("catalogName", "name");
				map.put("catalogParent", "parent");
				map.put("catalogItem", "item");
				
				map.put("competenceID", "id");
				map.put("competenceName", "name");
				map.put("competenceDescr", "description");
				map.put("competenceItem", "item");
				map.put("competenceCatalogID", "catalog_id");
				map.put("competenceIncarnatio", "incarnatio");
				map.put("competenceChainNumber", "chain_number");
				
				map.put("levelID", "id");
				map.put("levelName", "name");
				map.put("levelPrice", "price");
				
				map.put("indicatorID", "id");
				map.put("indicatorName", "name");
				map.put("indicatorItem", "item");
				map.put("indicatorLevelID", "level_id");
				map.put("indicatorCompetenceIncarnatio", "competence_incarnatio");
				
			
				
				
				String connectionUrl = "jdbc:sqlserver://192.168.56.2:1433;databaseName=serg;user=sa;password=Q11W22e33;";
				DatabaseConnectionKeeper kdb = new DatabaseConnectionKeeper(connectionUrl, null, null);

				String connectionUrl1 = "jdbc:sqlserver://192.168.56.2:1433;databaseName=Minos;user=sa;password=Q11W22e33;";
				DatabaseConnectionKeeper kdbM = new DatabaseConnectionKeeper(connectionUrl1, null, null);

				try {
					kdb.connect();
					kdbM.connect();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				LoadingCache<Integer, DivisionNode> cacheDivision = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, DivisionNode>(DivisionNode.class, kdb, 
								"select tOrgStruID, FullName, Parent from tOrgStru where tOrgStruID in (%id%)",
								"%id%", map));
				
				LoadingCache<Integer, PositionNode> cachePosition = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, PositionNode>(PositionNode.class, kdb, 
								"select tStatDolSPId, FullTXT from tStatDolSP where tStatDolSPId in (%id%)",
								"%id%", map));

				LoadingCache<Integer, PersonNode> cachePerson = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, PersonNode>(PersonNode.class, kdb, 
								"select tPersonaId, F, I, O, Drojd, Sex from tPersona where tPersonaId in (%id%)",
								"%id%", map));

				LoadingCache<Integer, CatalogNode> cacheCatalog = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, CatalogNode>(CatalogNode.class, kdbM, 
								"select id, name, item, parent from CATALOG where id in (%id%) order by item",
								"%id%", map));

				LoadingCache<Integer, CompetenceNode> cacheCompetence = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, CompetenceNode>(CompetenceNode.class, kdbM, 
								"select id, incarnatio, chain_number, name, description, catalog_id, item from COMPETENCE where (id in (%id%)) and (GetDate() between date_create and date_remove) order by item",
								"%id%", map));

				LoadingCache<Integer, LevelNode> cacheLevel = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, LevelNode>(LevelNode.class, kdbM, 
								"select id, name, price from LEVEL where id in (%id%) order by price",
								"%id%", map));

				LoadingCache<Integer, IndicatorNode> cacheIndicator = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, IndicatorNode>(IndicatorNode.class, kdbM, 
								"select id, name, level_id, competence_incarnatio, item from INDICATOR where (id in (%id%)) and (GetDate() between date_create and date_remove) order by item",
								"%id%", map));

				

				
				
				TreeModel tmd = new DivisionModel(kdb, cacheDivision, 
						"select tOrgStruID from tOrgStru where Parent = 0", 
						"select tOrgStruID from tOrgStru where Parent = %id%", "%id%");
				
				TreeModel tmpos = new PositionInDivisionModel(kdb, cachePosition, tmd, 
						"select distinct(tStatDolSpId) from tOrgAssignCur where tOrgStruId = %id%", 
						"%id%", true);

				TreeModel tmper = new PersonInDivisionModel(kdb, cachePerson, tmd, 
						"select tPersonaId from tOrgAssignCur where tOrgStruId = %id%", 
						"%id%", true);
				
				TreeModel tmcat = new CatalogModel(kdbM, cacheCatalog, 
						"select id from CATALOG where (parent = %id%) and (GetDate() between date_create and date_remove) order by item", "%id%");
				
				TreeModel tmcom = new CompetenceModel(kdbM, cacheCompetence, cacheLevel, cacheIndicator, "select 1", "select id from INDICATOR where competence_incarnatio = %id%", "%id%");
				
				JTree tcat = new JTree(tmcat);
				tcat.setRootVisible(false);
				
				JTree tper = new JTree(tmper);
				/*
				tper.setDragEnabled(true);
				tper.setTransferHandler();
				*/
				JTree tpos = new JTree(tmpos);
				tpos.setName("Position in Division");
				tpos.setDragEnabled(true);
				tpos.setTransferHandler(new MyTransferHandler("tpos"));
				tpos.setDropMode(DropMode.ON);
				
				JTree tcom = new JTree(tmcom);
				tcom.setName("Competence");
				tcom.setDragEnabled(true);
				tcom.setTransferHandler(new MyTransferHandler("tcom"));
				tcom.setDropMode(DropMode.ON);
				
				JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
						new JScrollPane(tcom), 
						new JScrollPane(tpos));

				JFrame frm = new JFrame("test");
				frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				frm.add(split);
				frm.pack();
				frm.setVisible(true);
				
			
			
			}
		});
		
	}
	
	public static class myTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;
		
	}
		
	
	public static void main(String[] args) {
		//PersonNode node =  new PersonNode();
		makeUI();
		if(true)
			return;
		
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

