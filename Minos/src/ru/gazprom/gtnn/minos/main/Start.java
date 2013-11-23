package ru.gazprom.gtnn.minos.main;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import javax.swing.tree.TreePath;

import com.google.common.base.Preconditions;
import com.google.common.cache.*;

import ru.gazprom.gtnn.minos.entity.*;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.models.CatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceAndCatalog;
import ru.gazprom.gtnn.minos.models.CompetenceModel;
import ru.gazprom.gtnn.minos.models.DivisionModel;
import ru.gazprom.gtnn.minos.models.MinosTreeRenderer;
import ru.gazprom.gtnn.minos.models.MyTransferHandler;
import ru.gazprom.gtnn.minos.models.PersonInDivisionModel;
import ru.gazprom.gtnn.minos.models.PositionInDivisionModel;
import ru.gazprom.gtnn.minos.models.ProfileAndPositionInDivision;
import ru.gazprom.gtnn.minos.models.ProfileModel;
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
				

				map.put("ProfileTable", "PROFILE");
				map.put("profileID", "id");
				map.put("profileName", "name");
				map.put("profileItem", "item");
				map.put("profileDivisionID", "division_id");
				map.put("profilePositionID", "position_id");
				map.put("profilePositionBID", "positionB_id");
				map.put("profileCompetenceID", "competence_id");
				map.put("profileMinLevel", "min_level");
				map.put("profileVariant", "variant");
				map.put("profileCreate", "date_create");
				map.put("profileRemove", "date_remove");
				map.put("profileHost", "host");

								
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
				
				LoadingCache<Integer, ProfileNode> cacheProfile = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, ProfileNode>(ProfileNode.class, kdbM, 
								"select p.id, p.name, p.division_id, p.positionB_id, p.position_id, " +
										"p.item, p.min_level, p.variant, p.date_create, p.date_remove, p.host, " +
										"c.id as competence_id from PROFILE p " +
										"join COMPETENCE c on c.incarnatio = p.competence_incanatio " +
										"where GETDATE() between p.date_create and p.date_remove " +
										"and GETDATE() between c.date_create and c.date_remove " +
										"and p.id in (%id%)",
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

				BasicModel tmpm = new ProfileModel(kdbM, tmcom, cacheCompetence);			
						
				
				String[] arr = {"%id1%", "%id2%"};
				BasicModel tcpd = new ProfileAndPositionInDivision(kdbM, cacheProfile, tmpos, tmpm,
						"select p.id from PROFILE p " +
						"join COMPETENCE c on c.incarnatio = p.competence_incanatio " +
						"where GETDATE() between p.date_create and p.date_remove " +
						"and GETDATE() between c.date_create and c.date_remove " +
						"and division_id = %id1% and position_id = %id2% " + 
						"and variant = 1 ",
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

				JButton btnLoadCompetenceDir = new JButton("load competence dir");
				btnLoadCompetenceDir.addActionListener(new MyListener5(tcc, (CatalogModel)tmcat));

				
				
				tb.add(btnAddCatalog);
				tb.add(btnAddCompetence);
				tb.add(btnRefreshCatalog);
				tb.add(btnAddIndicator);
				tb.add(btnLoadCompetenceDir);
				
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

	static class MyListener5 implements ActionListener, Runnable{		
		private String 		startDir;
		private String 		fileName;
		private CatalogNode parentCatalog;
		private JTree catalogTree;
		private CatalogModel catalogModel;
		

		public MyListener5(JTree catalogTree, CatalogModel catalogModel) {		
			this.catalogTree = catalogTree;		
			this.catalogModel = catalogModel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if( catalogTree.isSelectionEmpty() ) {
				JOptionPane.showMessageDialog(catalogTree, "не выбрана позиция для вставки");
				return;
			}
			TreePath p = catalogTree.getSelectionPath();
			if( !( p.getLastPathComponent() instanceof CatalogNode) ) {
				JOptionPane.showMessageDialog(catalogTree, "не выбрана позиция для вставки");
				return;				
			}
			
			parentCatalog = (CatalogNode) p.getLastPathComponent();
			
			JTextField pathField = new JTextField(100);
			JTextField fileField = new JTextField(100);
			pathField.setText("c:\\tmp\\minos\\");
			fileField.setText("1.txt");

			JComponent[] inputs = new JComponent[] {
					new JLabel("Start catalog"),
					pathField,
					new JLabel("Common file"),
					fileField,
			};
			
			if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "Competence dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) &&
					(!pathField.getText().isEmpty()) && (!fileField.getText().isEmpty()) ) {

				startDir = pathField.getText();
				fileName = fileField.getText();
				Thread thread = new Thread(this);
				thread.start();
			}
				
		}

		@Override
		public void run() {			
			Node node = readDir(Paths.get(startDir));
			if(node == null)
				return;
			printNode(node, 1);
			try {
				catalogLoader(parentCatalog, node, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}	
		
		private Node readDir(Path startPath) {
			Node node = new Node();
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(startPath)) {					
				node.name = startPath.getFileName().toString();
				
			    for (Path file: stream) {		    	
			        if(Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {		        	
			        	if(node.child == null)
			        		node.child = new ArrayList<>();
			        	node.child.add(readDir(file));
			        }

			        if(file.getFileName().toString().equalsIgnoreCase(fileName))
			        	node.path = file;
			    }
			} catch (IOException | DirectoryIteratorException e) {
				e.printStackTrace();
			}
			
			return node;
		}
		
		private void catalogLoader(CatalogNode dest, Node node, int level) throws Exception {
			CatalogNode nodeCatalog;
			if(level == 0) {
				nodeCatalog = dest;
			} else {
				nodeCatalog = new CatalogNode();			
				nodeCatalog.catalogName = node.name;
				nodeCatalog.subCatalogs = Collections.emptyList();
				nodeCatalog.catalogCreate = new java.sql.Date(System.currentTimeMillis());
				nodeCatalog.catalogRemove = BasicModel.endTime;
				catalogModel.add(nodeCatalog, dest, false, 
						CatalogNode.CATALOG_NAME | CatalogNode.CATALOG_PARENT | CatalogNode.CATALOG_ITEM |
						CatalogNode.CATALOG_CREATE | CatalogNode.CATALOG_REMOVE | CatalogNode.CATALOG_VARIANT);
			}

			Preconditions.checkArgument(nodeCatalog.catalogID != -1, "catalogLoader() : cannot load  CatalogNode" + nodeCatalog);
			
			//fileLoader(node.path, node.catalogID);
			
			if(node.child != null) {				
				for(Node n : node.child) 
					catalogLoader(nodeCatalog, n, level + 1);
			}
		}

		
		private void fileLoader(Path file, int catalogID) {
			if(file == null)
				return;
			
			Charset charset = Charset.forName("UTF-8");
			try ( BufferedReader reader = Files.newBufferedReader(file, charset) ) {
			    String line = null;
			    int step = 1;	
			    int itemIndicator = 1;
			    int itemCompetence = 1;
			    
			    java.util.Date currentDate = new java.util.Date(System.currentTimeMillis()); 

			    // start initialization
			    CompetenceNode nodeCompetence = new CompetenceNode();
			    nodeCompetence.competenceIncarnatio = 0; //
			    nodeCompetence.competenceCatalogID = catalogID;
			    nodeCompetence.competenceChainNumber = 0;
			    nodeCompetence.competenceCreate = currentDate;
			    nodeCompetence.competenceRemove = BasicModel.endTime;
			    
			    IndicatorNode nodeIndicator = new IndicatorNode();
			    nodeIndicator.indicatorCreate = currentDate;
			    nodeIndicator.indicatorRemove = BasicModel.endTime;   
			    
			    while ((line = reader.readLine()) != null) {
			    	if ( line.contains("$") ) {
			    			step = 1;
			    			itemIndicator = 1;
			    			itemCompetence++;
			    			continue;
			    	}
			    	if(line.isEmpty()) { 
			    		step++;
			    		itemIndicator = 1;
			    		continue;
			    	}
			    	switch(step) {
			    	case 1:
			    		nodeCompetence.competenceName = line;
			    		nodeCompetence.competenceItem = itemCompetence;
 			    		//System.out.println("<competence name> "  + line);
			    		break;
			    	case 2:
			    		nodeCompetence.competenceDescr = line;
			    		/*
			    		nodeCompetence.competenceID = CompetenceNode.insert(kdb,			    				
			    				CompetenceNode.COMPETENCE_NAME | CompetenceNode.COMPETENCE_DESCR | CompetenceNode.COMPETENCE_ITEM |
			    				CompetenceNode.COMPETENCE_CATALOG | CompetenceNode.COMPETENCE_INCARNATIO | CompetenceNode.COMPETENCE_CHAIN_NUMBER |
			    				CompetenceNode.COMPETENCE_CREATE | CompetenceNode.COMPETENCE_REMOVE,
			    				true, nodeCompetence);
			    		nodeCompetence.competenceIncarnatio = nodeCompetence.competenceID;
*/
			    		System.out.println(nodeCompetence);
			    		//System.out.println("<competence desc> "  + line);
			    		break;
			    	case 3:
			    	case 4:
			    	case 5:
			    	case 6:
			    	case 7:
			    		nodeIndicator.indicatorName = line;
			    		nodeIndicator.indicatorItem = itemIndicator++;
			    		nodeIndicator.indicatorLevelID = step - 2;
			    		nodeIndicator.indicatorCompetenceIncarnatio = nodeCompetence.competenceIncarnatio;
			    		
			    		/*
			    		nodeIndicator.indicatorID = IndicatorNode.insert(kdb, 
					    		IndicatorNode.INDICATOR_NAME | IndicatorNode.INDICATOR_ITEM | 
					    		IndicatorNode.INDICATOR_LEVEL | IndicatorNode.INDICATOR_COMPETENCE |
					    		IndicatorNode.INDICATOR_CREATE | IndicatorNode.INDICATOR_REMOVE, 
					    		nodeIndicator);
					    */
			    		System.out.println(nodeIndicator);
			    		//System.out.println("<indicaot level =" + (step - 2) + "   item =" + itemIndicator + " > "  + line);
			    		break;
			    	}			    	
			    }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		private void printNode(Node node, int level) {
			String s = "";
			for(int i = 0; i < level * 3; i++)
				s += " ";
			System.out.println(s + "<dir> " + node.name);
			if(node.path != null ) {
				System.out.println(s + " <file> " + node.path);
			}
			
			if(node.child == null) 
				return;
			
			for(Node n : node.child) {
				printNode(n, level + 1);
			}	
		}

		
		private static class Node {
			public List<Node> child;		
			Path path;
			String name;
			int catalogID;
		}
		
		static final int STEP_COUNT = 7;
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


