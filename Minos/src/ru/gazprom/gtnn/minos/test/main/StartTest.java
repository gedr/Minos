package ru.gazprom.gtnn.minos.test.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.entity.RoundActorsNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.MinosCacheLoader;
import ru.gazprom.gtnn.minos.util.TableKeeper;
import ru.gedr.util.tuple.Pair;


class SinnerPanel extends JPanel {
	private JList<String> sinnerList;
	private DatabaseConnectionKeeper kdb;
	private List<Pair<Integer, Integer>> roundActorsList; //first element is RoundActorsID, second element is ProfileID 
	private LoadingCache<Integer, PersonNode> cachePerson;
	private LoadingCache<Integer, RoundActorsNode> cacheRoundActors;
	private LoadingCache<Integer, DivisionNode> cacheDivision;
	private LoadingCache<Integer, PositionNode> cachePosition;
	private LoadingCache<Integer, ProfileNode> cacheProfile;
	private StringBuilder sb = new StringBuilder();
	
	private class SinnerListModel implements ListModel<String> {
		@Override
		public String getElementAt(int index) {
			System.out.println("SinnerListModel.getElementAt()");
			try {
				RoundActorsNode node = cacheRoundActors.get(roundActorsList.get(index).getFirst());
				PersonNode pn = cachePerson.get(node.roundActorsSinnerID);
				ProfileNode prn = cacheProfile.get(roundActorsList.get(index).getSecond());
				DivisionNode dn = cacheDivision.get(prn.profileDivisionID);
				PositionNode pnn = cachePosition.get(prn.profilePositionID);
				
				sb.delete(0, sb.length());
				sb.append("<HTML> <p><h2> ").append(pn.personSurname).append(" ").
				append(pn.personName).append(" ").append(pn.personPatronymic).append(" </h2> </p><p><h4>").
				append(pnn.positionName).append(" </p> <p> [ ").append(dn.divisionName).append(" ] </h4></p>");
				return sb.toString(); 
			} catch(Exception e) {
				e.printStackTrace();	
			}
			
			return null;
		}

		@Override
		public int getSize() {			
			return roundActorsList.size();
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public SinnerPanel(DatabaseConnectionKeeper kdb, int minosID,
			LoadingCache<Integer, PersonNode> cachePerson,
			LoadingCache<Integer, RoundActorsNode> cacheRoundActors,
			LoadingCache<Integer, DivisionNode> cacheDivision,
			LoadingCache<Integer, PositionNode> cachePosition, 
			LoadingCache<Integer, ProfileNode> cacheProfile) {
		this.kdb = kdb;
		this.cachePerson = cachePerson;
		this.cacheRoundActors = cacheRoundActors;
		this.cacheDivision = cacheDivision;
		this.cachePosition = cachePosition;
		this.cacheProfile = cacheProfile;
		
		try {
			String sql = " select mra.id, MIN(mrp.profile_id)someone_profle_id from MinosRoundActors mra "
					+ " inner join MinosRound mr on mr.id = mra.round_id "
					+ " inner join MinosRoundProfile mrp on mrp.actors_id = mra.id "
					+ " where mra.minos_id = %id% "
					+ " and (GETDATE() between mr.round_start and mr.round_stop) "
					+ " and (GETDATE() between mra.date_create and mra.date_remove) "
					+ " group by mra.id ";
			
			String request = kdb.makeSQLString(sql, "%id%", String.valueOf(minosID));
			
			Preconditions.checkNotNull(request,
							"SinnerPanel.SinnerPanel() : makeListParam() return null");
			
			System.out.println(request);
			TableKeeper tk = kdb.selectRows(request);
			System.out.println(tk.getRowCount());
			
			if( (tk == null) || (tk.getRowCount() == 0) )
				return;
				
			roundActorsList = new ArrayList<>();
			for(int i = 0; i < tk.getRowCount(); i++) {

				roundActorsList.add(new Pair<>((Integer)tk.getValue(i + 1, 1), (Integer)tk.getValue(i + 1, 2)));
				
			}
			System.out.println("roundActorsList.size() = " + roundActorsList.size());

			
			
		
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		sinnerList = new JList<>(new SinnerListModel());
		sinnerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setLayout(new BorderLayout());
		
		add(new JScrollPane(sinnerList));
		JLabel lbl = new JLabel("<HTML> <h2>Выберите пользователя для оценивания по компетенциям</h2>");
		lbl.setHorizontalAlignment(JLabel.CENTER);
		add(lbl, BorderLayout.NORTH);
		add(new JButton("<HTML> <h2>Начать оценку</h2>"), BorderLayout.SOUTH);
	}
}

class CompetencePanel extends JPanel {
	
	private DatabaseConnectionKeeper kdb;

	public CompetencePanel(DatabaseConnectionKeeper kdb, int minosID) {
		this.kdb = kdb;
		try {
			
			
			

				
		
		
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}

public class StartTest extends JFrame {
	private DatabaseConnectionKeeper kdb;
	private DatabaseConnectionKeeper kdbM;
	private Map<String, String> map = new HashMap<>();
	private static final String SINNER_PANEL = "sinners";
	private static final String COMPETENCE_PANEL = "competence";
	private LoadingCache<Integer, DivisionNode> cacheDivision;
	private LoadingCache<Integer, PositionNode> cachePosition;
	private LoadingCache<Integer, PersonNode> cachePerson;
	private LoadingCache<Integer, RoundActorsNode> cacheRoundActors;
	private LoadingCache<Integer, ProfileNode> cacheProfile;
	
	
	private void makeCaches() {
		cacheDivision = CacheBuilder.newBuilder().
				build(new MinosCacheLoader<Integer, DivisionNode>(DivisionNode.class, kdb, 
						"select tOrgStruID, FullName, Parent from tOrgStru where tOrgStruID in (%id%)",
						"%id%", map));
		
		cachePosition = CacheBuilder.newBuilder().
				build(new MinosCacheLoader<Integer, PositionNode>(PositionNode.class, kdb, 
						"select tStatDolSPId, FullTXT from tStatDolSP where tStatDolSPId in (%id%)",
						"%id%", map));

		cachePerson = CacheBuilder.newBuilder().
				build(new MinosCacheLoader<Integer, PersonNode>(PersonNode.class, kdb, 
						"select tPersonaId, F, I, O, Drojd, Sex from tPersona where tPersonaId in (%id%)",
						"%id%", map));
		cacheRoundActors = CacheBuilder.newBuilder().
				build(new MinosCacheLoader<Integer, RoundActorsNode>(RoundActorsNode.class, kdbM,
						"select id, minos_id, sinner_id, round_id, date_create, date_remove, host, finishFlag from MinosRoundActors where id in (%id%)",
						"%id%", map));

		cacheProfile = CacheBuilder.newBuilder().
				build(new MinosCacheLoader<Integer, ProfileNode>(ProfileNode.class, kdbM, 
						"select p.id, p.name, p.division_id, p.positionB_id, p.position_id, " +
								"p.item, p.min_level, p.variety, p.date_create, p.date_remove, p.host, " +
								"p.competence_incarnatio, c.id as competence_id from MinosProfile p " +
								"join MinosCompetence c on c.incarnatio = p.competence_incarnatio " +
								"and p.id in (%id%)",
						"%id%", map));

	}
	
	private void makeMap() {
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
		
		map.put("RoundActorsTable", "MinosRoundActors");
		map.put("roundActorsID", "id");
		map.put("roundActorsMinosID", "minos_id");
		map.put("roundActorsSinnerID", "sinner_id");
		map.put("roundActorsRoundID", "round_id");
		map.put("roundActorsHost", "host");
		map.put("roundActorsFinish", "finishFlag");
		map.put("roundActorsCreate", "date_create");
		map.put("roundActorsRemove", "date_remove");
		
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


	}
	
	
	private String currentLogin; 
	private int currentID;
	
	public StartTest(String urlMinos, String urlSerg) {
		this.kdbM = new DatabaseConnectionKeeper(urlMinos, null, null);
		
		this.kdb = ( urlMinos.equals(urlSerg) ? kdbM : new DatabaseConnectionKeeper(urlSerg, null, null) );
		
		try {
			kdbM.connect();
			if(kdb != kdbM)
				kdb.connect();
			TableKeeper tkUser = kdbM.selectRows("select SYSTEM_USER");
			currentLogin = (String)tkUser.getValue(1, 1);
							
			TableKeeper tk = kdbM.selectRows("select person_id from MinosPersonLogin where personLogin = SYSTEM_USER");
			if( (tk ==null) || (tk.getRowCount() != 1) ) {				
				JOptionPane.showMessageDialog(null,
						"<HTML><h1>С логином [" + currentLogin + "] должен быть увязан только один работник</h1>",
						"Информация",					
						JOptionPane.INFORMATION_MESSAGE);				
				return;
			}
			currentID = (Integer)tk.getValue(1, 1);

			
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		

		
		makeMap();
		makeCaches();
		
		JFrame frm = new JFrame();
		JPanel cards = new JPanel(new CardLayout());
		
		JPanel pan = new SinnerPanel(kdbM, currentID, cachePerson, cacheRoundActors, cacheDivision, cachePosition, cacheProfile);
		cards.add(pan, SINNER_PANEL);
		cards.add(new JPanel(), COMPETENCE_PANEL);

		frm.setContentPane(cards);
		((CardLayout)cards.getLayout()).show(cards, SINNER_PANEL);
		frm.setSize(640, 480);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);

	}
	
	
	public static void main(String[] args) {
		new StartTest("jdbc:sqlserver://192.168.56.2:1433;databaseName=Minos;user=sa;password=Q11W22e33;",
			"jdbc:sqlserver://192.168.56.2:1433;databaseName=serg;user=sa;password=Q11W22e33;");

	}

}
