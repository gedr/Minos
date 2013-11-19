package ru.gazprom.gtnn.minos.main;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.tree.TreeCellRenderer;

import com.google.common.cache.*;

import ru.gazprom.gtnn.minos.entity.*;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.models.CatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceAndCatalog;
import ru.gazprom.gtnn.minos.models.CompetenceAndPositionInDivisionModel;
import ru.gazprom.gtnn.minos.models.CompetenceModel;
import ru.gazprom.gtnn.minos.models.DivisionModel;
import ru.gazprom.gtnn.minos.models.MinosTreeRenderer;
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
				
				map.put("CatalogTable", "CATALOG");
				map.put("catalogID", "id");
				map.put("catalogName", "name");
				map.put("catalogHost", "host");
				map.put("catalogMode", "mode");
				map.put("catalogParent", "parent");
				map.put("catalogItem", "item");
				map.put("catalogCreate", "date_create");
				map.put("catalogRemove", "date_remove");
				
				map.put("CompetenceTable", "COMPETENCE");
				map.put("competenceID", "id");
				map.put("competenceName", "name");
				map.put("competenceHost", "host");
				map.put("competenceMode", "mode");
				map.put("competenceDescr", "description");
				map.put("competenceItem", "item");
				map.put("competenceCatalogID", "catalog_id");
				map.put("competenceIncarnatio", "incarnatio");
				map.put("competenceChainNumber", "chain_number");
				map.put("competenceCreate", "date_create");
				map.put("competenceRemove", "date_remove");
				
				map.put("levelID", "id");
				map.put("levelName", "name");
				map.put("levelPrice", "price");
				
				map.put("IndicatorTable", "INDICATOR");
				map.put("indicatorID", "id");
				map.put("indicatorName", "name");
				map.put("indicatorItem", "item");
				map.put("indicatorLevelID", "level_id");
				map.put("indicatorCompetenceIncarnatio", "competence_incarnatio");
				map.put("indicatorCreate", "date_create");
				map.put("indicatorRemove", "date_remove");
				map.put("indicatorHost", "host");
				
				CatalogNode.names = map;
				CompetenceNode.names = map;
				IndicatorNode.names = map;
			
				
				
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
								"select id, name, item, parent, host, mode, date_create, date_remove from CATALOG where id in (%id%) order by item",
								"%id%", map));

				LoadingCache<Integer, CompetenceNode> cacheCompetence = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, CompetenceNode>(CompetenceNode.class, kdbM, 
								"select id, incarnatio, chain_number, name, description, catalog_id, item, date_create, date_remove from COMPETENCE where (id in (%id%)) and (GetDate() between date_create and date_remove) order by item",
								"%id%", map));

				LoadingCache<Integer, LevelNode> cacheLevel = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, LevelNode>(LevelNode.class, kdbM, 
								"select id, name, price from LEVEL where id in (%id%) order by price",
								"%id%", map));

				LoadingCache<Integer, IndicatorNode> cacheIndicator = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, IndicatorNode>(IndicatorNode.class, kdbM, 
								"select id, name, level_id, competence_incarnatio, item, date_create, date_remove, host  from INDICATOR where (id in (%id%)) and (GetDate() between date_create and date_remove) order by item",
								"%id%", map));
				
				
				BasicModel tmd = new DivisionModel(kdb, cacheDivision, 
						"select tOrgStruID from tOrgStru where Parent = 0", 
						"select tOrgStruID from tOrgStru where Parent = %id%", "%id%");
				
				BasicModel tmpos = new PositionInDivisionModel(kdb, cachePosition, tmd, 
						"select distinct(tStatDolSpId) from tOrgAssignCur where tOrgStruId = %id%", 
						"%id%", true);

				BasicModel tmper = new PersonInDivisionModel(kdb, cachePerson, cachePosition, tmd, 
						"select tPersonaId, tStatDolSpId from tOrgAssignCur where tOrgStruId = %id%", 
						"%id%", true);
				
				BasicModel tmcat = new CatalogModel(kdbM, cacheCatalog, 
						"select id from CATALOG where (parent = %id%) and (GetDate() between date_create and date_remove) order by item", "%id%");
				
				BasicModel tmcom = new CompetenceModel(kdbM, cacheCompetence, cacheLevel, cacheIndicator, "select 1", "select id from INDICATOR where competence_incarnatio = %id%", "%id%");

				BasicModel tmcc = new CompetenceAndCatalog(kdbM, cacheCompetence, tmcat, tmcom,
						"select id from COMPETENCE where catalog_id = %id%", "%id%", true);

				String[] arr = {"%id1%", "%id2%"};
				BasicModel tcpd = new CompetenceAndPositionInDivisionModel(kdbM, cacheCompetence, tmper, tmcom,
						"select distinct(c.id) from PROFILE p " +
								" join PROFILE_COMPETENCE pc on pc.profile_id = p.id and GETDATE() between pc.date_create and pc.date_remove " +
								" join COMPETENCE c on c.incarnatio = pc.competence_incarnatio " +
								" where GETDATE() between  c.date_create and c.date_remove " +
								" and p.division_id = %id1% " + 
								" and p.position_id = %id2% ",
								arr);


				TreeCellRenderer tcr = new MinosTreeRenderer();
				
				JTree tcc = new JTree(tmcc);
				tcc.setRootVisible(false);				
				tcc.setCellRenderer(tcr);
				tcc.setDragEnabled(true);
				tcc.setTransferHandler(new MyTransferHandler("tcc"));
				tcc.setDropMode(DropMode.ON);
				tcc.setName("CompetenceAndCatalog");
				
				JTree tcat = new JTree(tmcat);
				tcat.setRootVisible(false);
				tcat.setName("Catalog");
				
				JTree tper = new JTree(tmper);
				tper.setName("PersonInDivision");
				/*
				tper.setDragEnabled(true);
				tper.setTransferHandler();
				*/
				JTree tpos = new JTree(tmpos);
				tpos.setName("Position in Division");
				tpos.setCellRenderer(tcr);
				tpos.setDragEnabled(true);
				tpos.setTransferHandler(new MyTransferHandler("tpos"));
				tpos.setDropMode(DropMode.ON);
				tpos.setName("PositionInDivision");
				
				JTree tcom = new JTree(tmcom);
				tcom.setName("Competence");
				tcom.setDragEnabled(true);
				tcom.setTransferHandler(new MyTransferHandler("tcom"));
				tcom.setDropMode(DropMode.ON);
				tcom.setName("Competence");
				
	
				
				JToolBar tb = new JToolBar();
				JButton btnRefreshCatalog = new JButton("refresh catalog");
				btnRefreshCatalog.addActionListener(new MyListener(tcc));
				
				
				JButton btnAddCatalog = new JButton("add catalog");
				btnAddCatalog.addActionListener(new MyListener2(tcc));

				JButton btnAddCompetence = new JButton("add competence");
				btnAddCompetence.addActionListener(new MyListener3(tcc));

				JButton btnAddIndicator = new JButton("add indicator");
				btnAddIndicator.addActionListener(new MyListener4(tcc));

				
				tb.add(btnAddCatalog);
				tb.add(btnAddCompetence);
				tb.add(btnRefreshCatalog);
				tb.add(btnAddIndicator);
				
				JPanel pan = new JPanel(new BorderLayout());
				pan.add(new JScrollPane(tcc), BorderLayout.CENTER);
				pan.add(tb, BorderLayout.NORTH);

				
				JSplitPane split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
						pan,  //tcom), 
						new JScrollPane(tpos));

				JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
						new JScrollPane(new JTree(tmpos)), new JScrollPane(tper));
				
				
				JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(new JTree(tmper)), new JScrollPane(new JTree(tmper)));
				splitPane3.setDividerLocation(0.5);
				
				JPanel roundRating = new JPanel();
				roundRating.setLayout(new BorderLayout());
				
				JToolBar tb2 = new JToolBar();
				tb2.add(new JButton("Add Round"));
				tb2.add(new JComboBox<String>());
				
				roundRating.add(tb2, BorderLayout.NORTH);
				roundRating.add(new JLabel("Раунд оценки: ???, название: ???,  приказ: ???, дата начала: ???, дата конца: ???"), BorderLayout.SOUTH);
				
				
				
				JTable table = new JTable(3, 2);				
				roundRating.add(new JScrollPane(table));
				
							
				JSplitPane split4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane3, roundRating);
				split4.setDividerLocation(300);

				
				
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
				tabbedPane.addTab("компетенция - профиль", split1);
				tabbedPane.addTab("профиль - сотрудник", split2);
				tabbedPane.addTab("эксперт - испытуемый", split4);
				tabbedPane.addTab("check", new JScrollPane(new JTree(tcpd)));
				
				JFrame frm = new JFrame("test");
				frm.add(tabbedPane);
				frm.add(tb, BorderLayout.NORTH);

				frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frm.pack();
				frm.setVisible(true);			
			
			}
		});
		
	}
	
	static class MyListener implements ActionListener {
		private JTree t;
		public MyListener(JTree t) {
			this.t = t;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			((BasicModel)t.getModel()).reload();
			t.updateUI();
			
		}		
	}

	static class MyListener2 implements ActionListener {
		private JTree t;
		public MyListener2(JTree t) {
			this.t = t;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String inputValue = JOptionPane.showInputDialog("Please input a value");     
			System.out.println(inputValue);
			if(inputValue != null) {
				CatalogNode cn = new CatalogNode();
				cn.catalogID = 1;
				cn.catalogName = inputValue;
				cn.catalogItem = 1;
				cn.catalogCreate = new Date(System.currentTimeMillis());
				cn.catalogRemove = new Date(BasicModel.endTime.getTime());
				
				try {
					((BasicModel) t.getModel()).add(cn, t.getSelectionPath());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			//t.updateUI();
			
		}		
	}

	
	static class MyListener3 implements ActionListener {
		private JTree t;
		public MyListener3(JTree t) {
			this.t = t;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField nameField = new JTextField();
			JTextArea descrField = new JTextArea(10, 100);
			
			final JComponent[] inputs = new JComponent[] {
					new JLabel("Name"),
					nameField,
					new JLabel("Descr"),
					descrField,
			};
			if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "Competence dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) &&
					(!nameField.getText().isEmpty()) ) {
				
				CompetenceNode cn = new CompetenceNode();
				cn.competenceName = nameField.getText(); 
				cn.competenceDescr = descrField.getText();
				cn.competenceItem = 1;
				cn.competenceCreate = new Date(System.currentTimeMillis());
				cn.competenceRemove = new Date(BasicModel.endTime.getTime());
				
				try {
					((BasicModel) t.getModel()).add(cn, t.getSelectionPath());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
				
			//JOptionPane.showMessageDialog(null, inputs, "Competence dialog", JOptionPane.QUESTION_MESSAGE);
			return ;
		}		
	}

	static class MyListener4 implements ActionListener {
		private JTree t;
		public MyListener4(JTree t) {
			this.t = t;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String inputValue = JOptionPane.showInputDialog("Please input a indicator");     
			System.out.println(inputValue);
			if(inputValue != null) {
				IndicatorNode source = new IndicatorNode();								
				source.indicatorName = inputValue;
				source.indicatorCreate = new Date(System.currentTimeMillis());
				source.indicatorRemove = new Date(BasicModel.endTime.getTime());
				try {
					((BasicModel) t.getModel()).add(source, t.getSelectionPath());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			//t.updateUI();
			
		}		
	}

	

	
	public static void setLaF(String name) {
		LookAndFeelInfo[] lfis = UIManager.getInstalledLookAndFeels();
		boolean fOk = false;
		for(LookAndFeelInfo lfi : lfis) {			
			if(lfi.getName().contains(name)) {
				try {
					UIManager.setLookAndFeel(lfi.getClassName());
					fOk = true;					
					break;
				} catch(Exception e) {					
					fOk = false;
					break;
				}				
			}
		}	
		System.out.println(name + (fOk ? " LaF set ok " : " LaF cannot set "));
	}
	
	
	public static void test()  {
		try {
			String connectionUrl = "jdbc:sqlserver://192.168.56.2:1433;databaseName=Minos;user=sa;password=Q11W22e33;";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			DatabaseConnectionKeeper kdb = new DatabaseConnectionKeeper(
					connectionUrl, null, null);
			kdb.connect();
			int key = kdb.insertRow(true, "CATALOG", 
					Arrays.asList(new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "name", "Test"),
					new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "parent", Integer.valueOf(10)),
					new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "item", Integer.valueOf(1)),
					new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "date_create", new java.sql.Date(1)),
					new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "date_remove", new java.sql.Date(1000))
					));
			System.out.println(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
		
	
	
	public static void test2()  {
		try {
			String connectionUrl = "jdbc:sqlserver://192.168.56.2:1433;databaseName=Minos;user=sa;password=Q11W22e33;";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			DatabaseConnectionKeeper kdb = new DatabaseConnectionKeeper(
					connectionUrl, null, null);
			kdb.connect();
			int key = kdb.updateRow("CATALOG", 
					Arrays.asList(new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "name", "TestUpdate"),
							new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "item", Integer.valueOf(100))),
							new DatabaseConnectionKeeper.RecordFeld(java.sql.Types.VARCHAR, "id", Integer.valueOf(14)));
					
			System.out.println(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	public static void main(String[] args) {
		//PersonNode node =  new PersonNode();
		//test2();
		//setLaF("Nimbus");
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


