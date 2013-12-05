package ru.gazprom.gtnn.minos.main;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
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
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeCellRenderer;

import com.google.common.cache.*;

import ru.gazprom.gtnn.minos.entity.*;
import ru.gazprom.gtnn.minos.handlers.AddCatalogListener;
import ru.gazprom.gtnn.minos.handlers.AddCompetenceListener;
import ru.gazprom.gtnn.minos.handlers.AddIndicatorListener;
import ru.gazprom.gtnn.minos.handlers.AddMinos_SinnerListener;
import ru.gazprom.gtnn.minos.handlers.AddRoundListener;
import ru.gazprom.gtnn.minos.handlers.EditProfileAndPositionAction;
import ru.gazprom.gtnn.minos.handlers.LoadCompetenceCatalogListener;
import ru.gazprom.gtnn.minos.handlers.LoadCompetenceFileListener;
import ru.gazprom.gtnn.minos.handlers.PrintResult;
import ru.gazprom.gtnn.minos.handlers.ReloadListener;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gazprom.gtnn.minos.models.CatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceAndCatalogModel;
import ru.gazprom.gtnn.minos.models.CompetenceModel;
import ru.gazprom.gtnn.minos.models.DivisionModel;
import ru.gazprom.gtnn.minos.models.MinosTreeRenderer;
import ru.gazprom.gtnn.minos.models.MyTransferHandler;
import ru.gazprom.gtnn.minos.models.PersonInDivisionModel;
import ru.gazprom.gtnn.minos.models.PositionInDivisionModel;
import ru.gazprom.gtnn.minos.models.ProfileAndPersonInDivision;
import ru.gazprom.gtnn.minos.models.ProfileAndPositionInDivision;
import ru.gazprom.gtnn.minos.models.ProfileModel;
import ru.gazprom.gtnn.minos.models.RoundActorsTableModel;
import ru.gazprom.gtnn.minos.models.RoundModel;
import ru.gazprom.gtnn.minos.util.*;




public class Start {
	//private String connectionUrl;
	private DatabaseConnectionKeeper kdb, kdbM;
	private Map<String, String> map = new HashMap<>();

	public Start(String connectionUrl) {
		//this.connectionUrl = connectionUrl;
		
		try {
			kdb = new DatabaseConnectionKeeper(connectionUrl, null, null);
			kdbM = kdb;

			kdb.connect();		
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
				    "Возникла ошибка при подключении к базе данных",
				    "Ошибка",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		makeUI();
	}

	public void makeUI() {

		EventQueue.invokeLater(new Runnable() {
			
			
			public void fillMap() {
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
				
				map.put("CatalogTable", "MinosCatalog");
				map.put("catalogID", "id");
				map.put("catalogName", "name");
				map.put("catalogHost", "host");
				map.put("catalogMode", "mode");
				map.put("catalogParent", "parent");
				map.put("catalogItem", "item");
				map.put("catalogCreate", "date_create");
				map.put("catalogRemove", "date_remove");
				map.put("catalogVariety", "variety");
				
				map.put("CompetenceTable", "MinosCompetence");
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
				map.put("competenceVariety", "variety");
				
				map.put("LevelTable", "MinosLevel");
				map.put("levelID", "id");
				map.put("levelName", "name");
				map.put("levelPrice", "price");
				
				map.put("IndicatorTable", "MinosIndicator");
				map.put("indicatorID", "id");
				map.put("indicatorName", "name");
				map.put("indicatorItem", "item");
				map.put("indicatorLevelID", "level_id");
				map.put("indicatorCompetenceIncarnatio", "competence_incarnatio");
				map.put("indicatorCreate", "date_create");
				map.put("indicatorRemove", "date_remove");
				map.put("indicatorHost", "host");

				map.put("ProfileTable", "MinosProfile");
				map.put("profileID", "id");
				map.put("profileName", "name");
				map.put("profileItem", "item");
				map.put("profileDivisionID", "division_id");
				map.put("profilePositionID", "position_id");
				map.put("profilePositionBID", "positionB_id");
				map.put("profileCompetenceID", "competence_id");
				map.put("profileCompetenceIncarnatio", "competence_incarnatio");
				map.put("profileMinLevel", "min_level");
				map.put("profileVariety", "variety");
				map.put("profileCreate", "date_create");
				map.put("profileRemove", "date_remove");
				map.put("profileHost", "host");

				map.put("StringAttrTable", "MinosStringAttr");
				map.put("stringAttrID", "id");
				map.put("stringAttrItem", "item");
				map.put("stringAttrValue", "value");
				map.put("stringAttrDescr", "descr");
				map.put("stringAttrVariety", "variety");
				map.put("stringAttrHost", "host");
				map.put("stringAttrExternalID1", "external_id1");
				map.put("stringAttrExternalID2", "external_id2");
				map.put("stringAttrExternalID3", "external_id3");
				map.put("stringAttrCreate", "date_create");
				map.put("stringAttrRemove", "date_remove");
				
				map.put("RoundTable", "MinosRound");
				map.put("roundID", "id");
				map.put("roundName", "name");
				map.put("roundDescr", "descr");
				map.put("roundHost", "host");
				map.put("roundCreate", "date_create");
				map.put("roundRemove", "date_remove");
				map.put("roundStart", "round_start");
				map.put("roundStop", "round_stop");

				map.put("RoundActorsTable", "MinosRoundActors");
				map.put("roundActorsID", "id");
				map.put("roundActorsMinosID", "minos_id");
				map.put("roundActorsSinnerID", "sinner_id");
				map.put("roundActorsRoundID", "round_id");
				map.put("roundActorsHost", "host");
				map.put("roundActorsFinish", "finishFlag");
				map.put("roundActorsCreate", "date_create");
				map.put("roundActorsRemove", "date_remove");

				map.put("RoundProfileTable", "MinosRoundProfile");
				map.put("roundProfileID", "id");
				map.put("roundProfileRoundActorsID", "actors_id");
				map.put("roundProfileProfileID", "profile_id");
				map.put("roundProfileIndicatorFlagsHI", "indicatorResultFlagsHi");
				map.put("roundProfileIndicatorFlagsLO", "indicatorResultFlagsLo");
				map.put("roundProfileCost", "cost");				
			}

			
			public void run() {
				fillMap();

				


				
				
				
				BasicNode.names = map;

				
/*
				DatabaseConnectionKeeper kdb;
				DatabaseConnectionKeeper kdbM;
				JTextField url = new JTextField(100);
				
				JComponent[] inputs = new JComponent[] {
						new JLabel("db url"),
						url,
				};
				
				
				if( (JOptionPane.OK_OPTION != JOptionPane.showOptionDialog(null, inputs, "db url dialog", 
						JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null, null, null)) ||
						(url.getText().isEmpty()) ) {
					return;
				}
				String connectionUrl = url.getText();
				kdb = new DatabaseConnectionKeeper(connectionUrl, null, null);
				kdbM = kdb;
				*/
				try {
					kdb.connect();
					if(kdb != kdbM) kdbM.connect();
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
								"select id, name, item, parent, host, mode, date_create, date_remove, variety from MinosCatalog where id in (%id%) order by item",
								"%id%", map));

				LoadingCache<Integer, CompetenceNode> cacheCompetence = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, CompetenceNode>(CompetenceNode.class, kdbM, 
								"select id, incarnatio, chain_number, name, description, catalog_id, item, date_create, date_remove, variety from MinosCompetence where (id in (%id%)) and (GetDate() between date_create and date_remove) order by item",
								"%id%", map));

				LoadingCache<Integer, LevelNode> cacheLevel = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, LevelNode>(LevelNode.class, kdbM, 
								"select id, name, price from MinosLevel where id in (%id%) order by price",
								"%id%", map));

				LoadingCache<Integer, IndicatorNode> cacheIndicator = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, IndicatorNode>(IndicatorNode.class, kdbM, 
								"select id, name, level_id, competence_incarnatio, item, date_create, date_remove, host  from MinosIndicator where (id in (%id%)) and (GetDate() between date_create and date_remove) order by item",
								"%id%", map));
				
				LoadingCache<Integer, ProfileNode> cacheProfile = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, ProfileNode>(ProfileNode.class, kdbM, 
								"select p.id, p.name, p.division_id, p.positionB_id, p.position_id, " +
										"p.item, p.min_level, p.variety, p.date_create, p.date_remove, p.host, " +
										"p.competence_incarnatio, c.id as competence_id from MinosProfile p " +
										"join MinosCompetence c on c.incarnatio = p.competence_incarnatio " +
										"where GETDATE() between p.date_create and p.date_remove " +
										"and GETDATE() between c.date_create and c.date_remove " +
										"and p.id in (%id%)",
								"%id%", map));
				
				LoadingCache<Integer, StringAttrNode> cacheStringAttr = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, StringAttrNode>(StringAttrNode.class, kdbM,
								"select id, item, value, descr, variety, external_id1, external_id2, external_id3, date_create, date_remove, host from MinosStringAttr where id in (%id%)",
								"%id%", map));

				LoadingCache<Integer, RoundNode> cacheRound = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, RoundNode>(RoundNode.class, kdbM,
								"select id, name, descr, date_create, date_remove, host, round_start, round_stop from MinosRound where id in (%id%)",
								"%id%", map));

				LoadingCache<Integer, RoundActorsNode> cacheRoundActors = CacheBuilder.
						newBuilder().
						build(new MinosCacheLoader<Integer, RoundActorsNode>(RoundActorsNode.class, kdbM,
								"select id, minos_id, sinner_id, round_id, date_create, date_remove, host, finishFlag from MinosRoundActors where id in (%id%)",
								"%id%", map));

				
				
				
				
				BasicModel divisionModel = new DivisionModel(kdb, cacheDivision, 
						"select tOrgStruID from tOrgStru where Parent = 0  and isnull(Isdelete, 0) = 0", 
						"select tOrgStruID from tOrgStru where Parent = %id% and isnull(Isdelete, 0) = 0", "%id%");
				
				BasicModel positionInDivisionModel = new PositionInDivisionModel(kdb, cachePosition, divisionModel, 
						"select distinct(tStatDolSpId) from tOrgAssignCur where tOrgStruId = %id%", 
						"%id%", true);

				BasicModel personInDivisionModel = new PersonInDivisionModel(kdb, cachePerson, 
						//cachePosition, 
						divisionModel, 
						"select tPersonaId, tStatDolSpId from tOrgAssignCur where tOrgStruId = %id% "
						+ " and (GETDATE() between BegDA and EndDA) and (State = 3)", 
						"%id%", true);
				
				BasicModel catalogModel = new CatalogModel(kdbM, cacheCatalog, 
						"select id from MinosCatalog where (parent = %id%) and (GetDate() between date_create and date_remove) order by item", "%id%");
				
				BasicModel competenceModel = new CompetenceModel(kdbM, cacheCompetence, cacheLevel, cacheIndicator, 
						//"select 1", 
						"select id from MinosIndicator where competence_incarnatio = %id%", "%id%");

				BasicModel competenceAndCatalogModel = new CompetenceAndCatalogModel(kdbM, cacheCompetence, catalogModel, competenceModel,
						"select id from MinosCompetence where catalog_id = %id%", "%id%", true);

				BasicModel profileModel = new ProfileModel(kdbM, competenceModel, 
						cacheCompetence, cacheStringAttr,
						"select id from MinosStringAttr where external_id1 = %id% and variety = 1",
						"%id%");			

				
				String[] arr = {"%id1%", "%id2%"};
				BasicModel profileAndPositionInDivisionModel = new ProfileAndPositionInDivision(kdbM, cacheProfile, positionInDivisionModel, profileModel,
						"select p.id from MinosProfile p " +
						"join MinosCompetence c on c.incarnatio = p.competence_incarnatio " +
						"where GETDATE() between p.date_create and p.date_remove " +
						"and GETDATE() between c.date_create and c.date_remove " +
						"and division_id = %id1% and position_id = %id2% " + 
						"and p.variety = 1 ",
						arr);

				BasicModel profileAndPersonInDivisionModel = new ProfileAndPersonInDivision(kdbM, 
						cacheProfile, 
						personInDivisionModel, 
						profileModel,
						"select p.id from MinosProfile p " +
						"join MinosCompetence c on c.incarnatio = p.competence_incarnatio " +
						"where GETDATE() between p.date_create and p.date_remove " +
						"and GETDATE() between c.date_create and c.date_remove " +
						"and division_id = %id1% and position_id = %id2% " + 
						"and p.variety = 1 ",
						arr);

				TreeCellRenderer tcr = new MinosTreeRenderer(cacheCompetence, cacheLevel);
				
				JTree treeCompetenceAndCatalog = new JTree(competenceAndCatalogModel);
				treeCompetenceAndCatalog.setRootVisible(false);				
				treeCompetenceAndCatalog.setCellRenderer(tcr);
				//treeCompetenceAndCatalog.setDragEnabled(true);
				//treeCompetenceAndCatalog.setTransferHandler(new MyTransferHandler("tcc"));
				//treeCompetenceAndCatalog.setDropMode(DropMode.ON);
				
				JTree treeCatalog = new JTree(catalogModel);
				treeCatalog.setRootVisible(false);
				treeCatalog.setName("Catalog");
				
				//JTree treePersonInDivision = new JTree(personInDivisionModel);
				/*
				tper.setDragEnabled(true);
				tper.setTransferHandler();
				*/
				JTree treeProfileAndPositionInDivision1 = new JTree(profileAndPositionInDivisionModel);
				treeProfileAndPositionInDivision1.setCellRenderer(tcr);

				JTree treeProfileAndPositionInDivision2 = new JTree(profileAndPositionInDivisionModel);
				treeProfileAndPositionInDivision2.setCellRenderer(tcr);

				//treeProfileAndPositionInDivision.setDragEnabled(true);
				//treeProfileAndPositionInDivision.setTransferHandler(new MyTransferHandler("tpos"));
				//treeProfileAndPositionInDivision.setDropMode(DropMode.ON);

				String action = "changeWnd";
				InputMap im =treeProfileAndPositionInDivision1.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
				im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), action);

				ActionMap actionMap = treeProfileAndPositionInDivision1.getActionMap();
				actionMap.put(action, new EditProfileAndPositionAction(treeProfileAndPositionInDivision1, cacheLevel));
				treeProfileAndPositionInDivision1.setActionMap(actionMap);

				JFrame frm = new JFrame("test");

				JTree treeCompetence = new JTree(competenceModel);
				treeCompetence.setName("Competence");
				treeCompetence.setDragEnabled(true);
				treeCompetence.setTransferHandler(new MyTransferHandler("tcom"));
				treeCompetence.setDropMode(DropMode.ON);
				treeCompetence.setName("Competence");
				
				JTree treeProfileAndPersonInDivisionModel = new JTree(profileAndPersonInDivisionModel);
				treeProfileAndPersonInDivisionModel.setCellRenderer(tcr);
				
				JButton btnRefreshCatalog = new JButton(new ImageIcon(getClass().getResource("/img/repeat_32.png")));
				btnRefreshCatalog.setToolTipText("Обновить дерево");
				btnRefreshCatalog.addActionListener(new ReloadListener(treeCompetenceAndCatalog));
								
				JButton btnAddCatalog = new JButton(new ImageIcon(getClass().getResource("/img/folder_add_32.png")));
				btnAddCatalog.setToolTipText("Добавить каталог");
				btnAddCatalog.addActionListener(new AddCatalogListener(treeCompetenceAndCatalog));

				JButton btnAddCompetence = new JButton(new ImageIcon(getClass().getResource("/img/book_add_32.png")));
				btnAddCompetence.setToolTipText("Добавить новую компетенцию");
				btnAddCompetence.addActionListener(new AddCompetenceListener(treeCompetenceAndCatalog));

				JButton btnAddIndicator = new JButton(new ImageIcon(getClass().getResource("/img/page_add_32.png")));
				btnAddIndicator.setToolTipText("Добавить индикатор");
				btnAddIndicator.addActionListener(new AddIndicatorListener(treeCompetenceAndCatalog));

				JButton btnLoadCompetenceDir = new JButton(new ImageIcon(getClass().getResource("/img/folder_down_32.png")));
				btnLoadCompetenceDir.setToolTipText("Загрузить каталоги с компетенциями");
				btnLoadCompetenceDir.addActionListener(new LoadCompetenceCatalogListener(treeCompetenceAndCatalog, frm));

				JButton btnLoadCompetenceFile = new JButton(new ImageIcon(getClass().getResource("/img/page_down_32.png")));
				btnLoadCompetenceFile.setToolTipText("Загрузить файл с компетенциями");
				btnLoadCompetenceFile.addActionListener(new LoadCompetenceFileListener(treeCompetenceAndCatalog, frm));

				JButton btnMakeLink = new JButton(new ImageIcon(getClass().getResource("/img/next_32.png")));
				btnMakeLink.setToolTipText("Создать профиль с указаными компетенциями");
				btnMakeLink.addActionListener(new MakeProfileAction(treeCompetenceAndCatalog, treeProfileAndPositionInDivision1));
				
				JToolBar tb = new JToolBar();
				tb.add(btnAddCatalog);
				tb.add(btnAddCompetence);
				tb.add(btnAddIndicator);
				tb.add(btnRefreshCatalog);				
				tb.add(btnLoadCompetenceDir);
				tb.add(btnLoadCompetenceFile);
				tb.add(btnMakeLink);
				
				JPanel competenceAndCatalogPanel = new JPanel(new BorderLayout());
				competenceAndCatalogPanel.add(new JScrollPane(treeCompetenceAndCatalog), BorderLayout.CENTER);
				competenceAndCatalogPanel.add(tb, BorderLayout.NORTH);

				
				JSplitPane split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
						competenceAndCatalogPanel,   
						new JScrollPane(treeProfileAndPositionInDivision1));

				JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
						new JScrollPane(treeProfileAndPositionInDivision2), 
						new JScrollPane(treeProfileAndPersonInDivisionModel)); 
				
				

				TableModel minosSinnerTableModel = new RoundActorsTableModel(kdbM, cacheRoundActors, cachePerson,
						"select id from MinosRoundActors where round_id = %id%", "%id%");

				JTable table = new JTable(minosSinnerTableModel);
				String actionT = "printTable";
				InputMap imt =table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
				imt.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), actionT);

				ActionMap actionMapT = table.getActionMap();
				actionMapT.put(actionT, new PrintResult(table, kdbM, cachePerson));
				table.setActionMap(actionMapT);

				
				

				
				JLabel roundLabel = new JLabel();
				
				JComboBox<RoundNode> cmb = new JComboBox<>(); 
				RoundModel roundModel = new RoundModel(kdbM, cacheRound, roundLabel, table, "select id from MinosRound");
				cmb.setModel( roundModel );
				Dimension dim = cmb.getPreferredSize();
				dim.width *= 10;
				cmb.setPreferredSize(dim);
				
				
				JPanel panelMinos_Sinner = new JPanel(new BorderLayout());
				JTree treeMinoses = new JTree(personInDivisionModel);
				JTree treeSinners = new JTree(profileAndPersonInDivisionModel);
				JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
						new JScrollPane(treeMinoses),
						new JScrollPane(treeSinners));
				splitPane3.setDividerLocation(0.5);
				panelMinos_Sinner.add(splitPane3, BorderLayout.CENTER);
				
				JButton btnAddMinos_Sinner = new JButton("add");
				btnAddMinos_Sinner.addActionListener(new AddMinos_SinnerListener(treeMinoses, treeSinners, roundModel, table, kdbM));
				panelMinos_Sinner.add(btnAddMinos_Sinner, BorderLayout.SOUTH);
				
				
				JPanel roundRating = new JPanel();
				roundRating.setLayout(new BorderLayout());
				
				
				JButton btnAddRound = new JButton("+");
				btnAddRound.addActionListener(new AddRoundListener(kdbM, cmb));
								

				JToolBar tb2 = new JToolBar();
				tb2.add(btnAddRound);
				tb2.add(cmb);
	
				roundRating.add(tb2, BorderLayout.NORTH);
				roundRating.add(roundLabel, BorderLayout.SOUTH);
				roundRating.add(new JScrollPane(table));
											
				
				JSplitPane split4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelMinos_Sinner, roundRating);
				
				split4.setDividerLocation(300);				
				
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
				tabbedPane.addTab("компетенция - профиль", split1);
				tabbedPane.addTab("профиль - сотрудник", split2);
				tabbedPane.addTab("эксперт - испытуемый", split4);
				
				frm.add(tabbedPane);

				frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frm.pack();
				frm.setVisible(true);			
			}
		});
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
	
	public static void main(String[] args) {
		setLaF("Nimbus");
		//			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		System.out.println(args[0]);
		new Start(args[0]);
		//makeUI();
	}

}


