package ru.gazprom.gtnn.minos.test.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.basic.BasicTreeUI.MouseHandler;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.LevelNode;
import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.entity.RoundActorsNode;
import ru.gazprom.gtnn.minos.entity.RoundNode;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.MinosCacheLoader;
import ru.gazprom.gtnn.minos.util.TableKeeper;
import ru.gedr.util.tuple.Pair;


class SinnerPanel extends JPanel {
	public static final String BUTTON_NAME = "START_RAITING";
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
			LoadingCache<Integer, ProfileNode> cacheProfile		) {
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
						
			TableKeeper tk = kdb.selectRows(request);
						
			if( (tk == null) || (tk.getRowCount() == 0) )
				return;
				
			roundActorsList = new ArrayList<>();
			for(int i = 0; i < tk.getRowCount(); i++) {

				roundActorsList.add(new Pair<>((Integer)tk.getValue(i + 1, 1), (Integer)tk.getValue(i + 1, 2)));
				
			}
		
			
			
		
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
		
		JButton btn = new JButton("<HTML> <h2>Начать оценку</h2>");
		btn.setName(BUTTON_NAME);
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg) {
				fireActionPerformed(arg);
			}
		});

		add(btn, BorderLayout.SOUTH);
	}

	public int getSinnerID() {
		if(sinnerList.isSelectionEmpty() )
			return 0;

		int ind = sinnerList.getSelectedIndex();
		int sinnerID = 0;
		try {
			sinnerID = cacheRoundActors.get(roundActorsList.get(ind).getFirst()).roundActorsSinnerID;
		} catch (ExecutionException e) {
			e.printStackTrace();
			sinnerID = 0;
		}

		return sinnerID;
	}

	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	protected void fireActionPerformed(ActionEvent event) {
		if(sinnerList.isSelectionEmpty() )
			return;
		EventListener[] listeners = listenerList.getListeners(ActionListener.class);

		for (EventListener evnt : listeners ) {
			((ActionListener)evnt).actionPerformed(event);
		}
	}
}









class CompetencePanel extends JPanel implements ActionListener {
	public static final String BUTTON_NAME = "SELECT_SINNER";
	private DatabaseConnectionKeeper kdb;
	private JComboBox<CompetenceNode> competenceComboBox;
	private JLabel competenceName;
	private JTextArea competenceDescr;
	private JList<IndicatorNode> indicatorList;
	private JButton btnPrev;
	private JButton btnSinner;
	private JButton btnNext;
	private int minosID;
	private int sinnerID;
	private int[] round;
	private List<Pair<Integer, Integer>> profiles; // fist element is profile id, second element is competence id
	private List<Pair<Integer, Boolean>> indicators; // fist element is idicatorID, second element is check status
	private LoadingCache<Integer, CompetenceNode> cacheCompetence;
	private LoadingCache<Integer, IndicatorNode> cacheIndicator;
	private int currentCompetenceNumber = 0;
	private Map<String, ImageIcon> img;
	
	
	
	public CompetencePanel(DatabaseConnectionKeeper kdb, int minosID, 
			LoadingCache<Integer, CompetenceNode> cacheCompetence,
			LoadingCache<Integer, IndicatorNode> cacheIndicator) {
		this.kdb = kdb;
		this.minosID = minosID;
		this.cacheCompetence = cacheCompetence;
		this.cacheIndicator = cacheIndicator;
		img = new HashMap<>();
		
		img.put("prefs_24", new ImageIcon(getClass().getResource("/img/prefs_24.png")) );
		img.put("prefs_shadow_24", new ImageIcon(getClass().getResource("/img/prefs_shadow_24.png")) );
		try {			
			makeUI();				
		
		
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	public boolean setSinnerID(int sinnerID) {
		this.sinnerID = sinnerID;
		return load();
	}
	
	public boolean load() {
		round = getRounds();
		if(round == null) return false;
		
		if( (profiles != null) && (profiles.size() > 0) )
			profiles.clear();
	
		profiles = loadProfile();
		if(profiles == null) return false;
		
		
		
		currentCompetenceNumber = 0;
		btnPrev.setEnabled(false);
		changeCompetence();
		
		return true;		
	}
	
	private void changeCompetence() {
		try {
			competenceName.setText(cacheCompetence.get(profiles.get(currentCompetenceNumber).getSecond()).competenceName);
			competenceDescr.setText(cacheCompetence.get(profiles.get(currentCompetenceNumber).getSecond()).competenceDescr);

			if( (indicators != null) && (indicators.size() > 0) )
				indicators.clear();			
			indicators = loadIndiactor(cacheCompetence.get(profiles.get(currentCompetenceNumber).getSecond()).competenceIncarnatio);
			indicatorList.updateUI();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}		
	}
	
	public List<Pair<Integer, Boolean>> loadIndiactor(int comptenceIncarnatio) {
		String sql = " select id from MinosIndicator "
				+ " where  GETDATE() between date_create and date_remove "
				+ " and competence_incarnatio = %id% "
				+ " order by id ";

		try {
			String request = kdb.makeSQLString(sql, "%id%", String.valueOf(comptenceIncarnatio));
			
			Preconditions.checkNotNull(request,
							"CompetencePanel.loadProfile() : makeListParam() return null");

			TableKeeper tk = kdb.selectRows(request);
			if ((tk == null) || (tk.getRowCount() <= 0))
				return null;
		
			List<Pair<Integer, Boolean>> lst = new ArrayList<>();
			for(int i = 0; i < tk.getRowCount(); i++)
				lst.add(new Pair<>((Integer)tk.getValue(i + 1, 1), false));
			return lst;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<Pair<Integer, Integer>> loadProfile() {
		String sql = " select distinct(mrp.profile_id), mc.id competence_id from MinosRoundActors mra "
				+ " inner join MinosRound mr on mr.id = mra.round_id "
				+ " inner join MinosRoundProfile mrp on mrp.actors_id = mra.id "
				+ " inner join MinosProfile mp on mp.id = mrp.profile_id "
				+ " inner join MinosCompetence mc on mc.incarnatio = mp.competence_incarnatio "
				+ " where GETDATE() between mra.date_create and mra.date_remove "
				+ " and GETDATE() between mr.round_start and mr.round_stop "
				+ " and GETDATE() between mc.date_create and mc.date_remove "
				+ " and mra.minos_id = %id1% and mra.sinner_id = %id2% ";

		try {
			String sql2 = kdb.makeSQLString(sql, "%id1%", String.valueOf(minosID));
			Preconditions.checkNotNull(sql2,
							"CompetencePanel.loadProfile() : makeListParam() return null (1)");
			
			String request = kdb.makeSQLString(sql2, "%id2%", String.valueOf(sinnerID));
			Preconditions.checkNotNull(request,
							"CompetencePanel.loadProfile() : makeListParam() return null (2)");
			
			TableKeeper tk = kdb.selectRows(request);
			if ((tk == null) || (tk.getRowCount() <= 0))
				return null;
			
			List<Pair<Integer, Integer>> lst = new ArrayList<>();
			for(int i = 0; i < tk.getRowCount(); i++) 
				lst.add(new Pair<>((Integer)tk.getValue(i + 1, 1), (Integer)tk.getValue(i + 1, 2)));
			
			return lst;				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;

	}
	
	public int[] getRounds() {
		String sql = " select distinct(mra.round_id) from MinosRoundActors mra "
				+ " inner join MinosRound mr on mr.id = mra.round_id "
				+ " where GETDATE() between mra.date_create and mra.date_remove "
				+ " and GETDATE() between mr.round_start and mr.round_stop "
				+ " and mra.minos_id = %id1% and mra.sinner_id = %id2% ";
		
		try {
			String sql2 = kdb.makeSQLString(sql, "%id1%", String.valueOf(minosID));
			Preconditions.checkNotNull(sql2,
					"CompetencePanel.getRounds() : makeListParam() return null (1)");
			String request = kdb.makeSQLString(sql2, "%id2%", String.valueOf(sinnerID));
			Preconditions.checkNotNull(request,
					"CompetencePanel.getRounds() : makeListParam() return null (2)");

			TableKeeper tk = kdb.selectRows(request);
			if( (tk == null) || (tk.getRowCount() <= 0))
				return null;
			
			int[] result = new int [tk.getRowCount()];
			for(int i = 0; i < tk.getRowCount(); i++)
				result[i] = (Integer)tk.getValue(i + 1, 1);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public void makeUI() {
		MyMouseHandler mh = new MyMouseHandler();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{29, 29, 75, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		competenceName = new JLabel("competence name");
		competenceName.setHorizontalAlignment(SwingConstants.CENTER);
		competenceName.setVerticalAlignment(SwingConstants.CENTER);
		competenceName.addMouseListener(mh);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		add(competenceName, gbc_lblNewLabel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 2;
		add(scrollPane_1, gbc_scrollPane_1);
		
		competenceDescr = new JTextArea();
		competenceDescr.setFont(new Font("Courier New", Font.PLAIN, 14));
		competenceDescr.setEditable(false);
		competenceDescr.setLineWrap(true);
		competenceDescr.setWrapStyleWord(true);
		scrollPane_1.setViewportView(competenceDescr);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		add(scrollPane, gbc_scrollPane);
		
		indicatorList = new JList<>(new ListModel<IndicatorNode>() {

			@Override
			public IndicatorNode getElementAt(int index) {
				IndicatorNode node = null;
				try {
					node = cacheIndicator.get(indicators.get(index).getFirst());
				} catch (ExecutionException e) {
					e.printStackTrace();
					node = null;
				}
				return node;
			}

			@Override
			public int getSize() {
				return (indicators == null ? 0 : indicators.size() );
			}

			@Override
			public void addListDataListener(ListDataListener l) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removeListDataListener(ListDataListener l) {
				// TODO Auto-generated method stub
				
			}			
		});
		indicatorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		indicatorList.setCellRenderer(new ListCellRenderer<IndicatorNode>() {
			
			@Override
			public Component getListCellRendererComponent(
					JList<? extends IndicatorNode> list, IndicatorNode value,
					int index, boolean isSelected, boolean cellHasFocus) {
				final JLabel lbl = new JLabel();
				lbl.setOpaque(true);
				lbl.setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
				
				lbl.setIcon(img.get(indicators.get(index).getSecond() ? "prefs_24" : "prefs_shadow_24"));
				lbl.setText(value.indicatorName + " [ " + String.valueOf(value.indicatorLevelID) + " ] ");
				return lbl;
			}
		});
		
		indicatorList.addMouseListener(mh);
		scrollPane.setViewportView(indicatorList);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 4;
		add(panel, gbc_panel);
		
		btnPrev = new JButton("Предыдущая компетенция");
		btnPrev.addActionListener(this);
		panel.add(btnPrev);
		
		btnSinner = new JButton("Выбор тестируемого");
		btnSinner.addActionListener(this);
		btnSinner.setName(BUTTON_NAME);
		panel.add(btnSinner);
		
		btnNext = new JButton("Следующая компетенция");
		btnNext.addActionListener(this);
		panel.add(btnNext);
	}
 
	@Override
	public void actionPerformed(ActionEvent arg) {
		if(arg.getSource() == btnNext) {
			makeCost();
			currentCompetenceNumber++;
			currentCompetenceNumber = (currentCompetenceNumber >= profiles.size() ? profiles.size() - 1 : currentCompetenceNumber);   
			btnNext.setEnabled(((currentCompetenceNumber == (profiles.size() - 1)) ? false : true) );
			if(!btnPrev.isEnabled())
				btnPrev.setEnabled(true);
			changeCompetence();
		}
		if(arg.getSource() == btnPrev) {
			makeCost();
			currentCompetenceNumber--;
			currentCompetenceNumber = (currentCompetenceNumber < 0 ? 0 : currentCompetenceNumber);
			btnPrev.setEnabled(((currentCompetenceNumber == 0) ? false : true ) );
			if(!btnNext.isEnabled())
				btnNext.setEnabled(true);
			changeCompetence();
		}
		if(arg.getSource() == btnSinner) {
			fireActionPerformed(arg);
		}		

	}
	
	
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	protected void fireActionPerformed(ActionEvent event) {
		EventListener[] listeners = listenerList.getListeners(ActionListener.class);

		for (EventListener evnt : listeners ) {
			((ActionListener)evnt).actionPerformed(event);
		}
	}

	/**
	 * расчет оценки для текущих индикаторов
	 */
	private int makeCost() {
		int[][] countIndicatorsByLevel =  getCountIndicatorsByLevel();
		printArray(countIndicatorsByLevel);
	
		return 0;
	}
	
	private void printArray(int[][] arr) {
		
		
		for(int i = 0; i < arr.length; i++) {
			for(int j = 0; j < arr[i].length; j++)
				System.out.print(arr[i][j] + "  ");
			System.out.println();
		}
				
	}
	
	private int[][] getCountIndicatorsByLevel() {
		int [][] arr = new int [2][LevelNode.LEVEL_COUNT];
		
		// make zero
		for(int i = 0; i < LevelNode.LEVEL_COUNT; i++) {
			arr[0][i] = 0;
			arr[1][i] = 0;
		}
		
		try {
			// sum
			int level = 0;
			for (int i = 0; i < indicators.size(); i++) {
				level = cacheIndicator.get(indicators.get(i).getFirst()).indicatorLevelID;
				arr[0][level - 1]++;
				if(indicators.get(i).getSecond()) 
					arr[1][level - 1]++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}
	
	
	private class MyMouseHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2 && !e.isConsumed()) {
			     e.consume();
			     if(e.getSource() == indicatorList) {

			    	 int ind = indicatorList.getSelectedIndex();
			    	 boolean b = indicators.get(ind).getSecond();
			    	 indicators.get(ind).setSecond(!b);

			    	 indicatorList.updateUI();
			     }
			     
			     if(e.getSource() == competenceName) {
			    	 final JComboBox<CompetenceNode> competenceComboBox = 
			 				new JComboBox<>(new DefaultComboBoxModel<CompetenceNode>() {
			 					private static final long serialVersionUID = 1L;
			 					private int selectNumber = 0;

			 					@Override
			 					public int getIndexOf(Object arg) {
			 						Preconditions.checkNotNull(arg, " CompetenceModel.getIndexOf() is null");
			 						Preconditions.checkState(arg instanceof CompetenceNode, "CompetenceModel.getIndexOf() is not CompetenceNode ");
			 						if( (profiles == null) || (profiles.size() <= 0) )
			 							return -1;
			 						CompetenceNode node = (CompetenceNode) arg;
			 						try {
			 							for (int i = 0; i < profiles.size(); i++) {
			 								CompetenceNode node2 = cacheCompetence.get(profiles.get(i).getSecond());
			 								if (node.competenceID == node2.competenceID)
			 									return i;
			 							}
			 						} catch (Exception e) {
			 							e.printStackTrace();
			 						}
			 						return -1;
			 					}

			 					@Override
			 					public CompetenceNode getElementAt(int arg) {
			 						CompetenceNode node = null;
			 						
			 						try {
			 							node = cacheCompetence.get(profiles.get(arg).getSecond());
			 						} catch (ExecutionException e) {
			 							e.printStackTrace();
			 							node = null;
			 						}

			 						return node;
			 					}

			 					@Override
			 					public int getSize() {
			 						return (profiles == null ? 0 : profiles.size());
			 					}


			 					@Override
			 					public Object getSelectedItem() {
			 						if( (profiles == null) || (profiles.size() <= 0) )
			 							return null;
			 							
			 						CompetenceNode node = null;
			 						
			 						try {
			 							node = cacheCompetence.get(profiles.get(selectNumber).getSecond());
			 						} catch (ExecutionException e) {
			 							e.printStackTrace();
			 							node = null;
			 						}					
			 							
			 						return node;
			 					}

			 					
			 					@Override
			 					public void setSelectedItem(Object arg) {
			 						selectNumber = getIndexOf(arg);
			 						Preconditions.checkState(selectNumber >= 0, "CompetenceModel.setSelectedItem() < 0");		
			 					}
			 				});


			 		competenceComboBox.setSelectedIndex(currentCompetenceNumber);
			 		final JComponent[] inputs = new JComponent[] {							
			 				competenceComboBox,
			 		};
					
					if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(CompetencePanel.this, inputs, "Выбор компетенции", 
							JOptionPane.OK_CANCEL_OPTION, 
							JOptionPane.QUESTION_MESSAGE, 
							null, null, null)) ) {
						currentCompetenceNumber = competenceComboBox.getSelectedIndex();
						
						btnNext.setEnabled(((currentCompetenceNumber == (profiles.size() - 1)) ? false : true) );
						btnPrev.setEnabled(((currentCompetenceNumber == 0) ? false : true) );
						changeCompetence();
					}
			     }
			}
			super.mouseClicked(e);
		}
		
	}
}














public class StartTest extends JFrame implements ActionListener{
	private DatabaseConnectionKeeper kdb;
	private DatabaseConnectionKeeper kdbM;
	private SinnerPanel sinnerPanel;
	private CompetencePanel competencePanel;
	private JPanel cards;
	private Map<String, String> map = new HashMap<>();
	private static final String SINNER_PANEL = "sinners";
	private static final String COMPETENCE_PANEL = "competence";
	private LoadingCache<Integer, DivisionNode> cacheDivision;
	private LoadingCache<Integer, PositionNode> cachePosition;
	private LoadingCache<Integer, PersonNode> cachePerson;
	private LoadingCache<Integer, RoundActorsNode> cacheRoundActors;
	private LoadingCache<Integer, ProfileNode> cacheProfile;
	private LoadingCache<Integer, CompetenceNode> cacheCompetence;	
	private LoadingCache<Integer, IndicatorNode> cacheIndicator;
	
	
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
		
		cacheCompetence = CacheBuilder.newBuilder().
				build(new MinosCacheLoader<Integer, CompetenceNode>(CompetenceNode.class, kdbM, 
						"select id, incarnatio, chain_number, name, description, catalog_id, item, date_create, date_remove, variety from MinosCompetence where (id in (%id%)) order by item",
						"%id%", map));
		cacheIndicator = CacheBuilder.newBuilder().
				build(new MinosCacheLoader<Integer, IndicatorNode>(IndicatorNode.class, kdbM, 
						"select id, name, level_id, competence_incarnatio, item, date_create, date_remove, host  from MinosIndicator where (id in (%id%)) and (GetDate() between date_create and date_remove) order by item",
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

		map.put("IndicatorTable", "MinosIndicator");
		map.put("indicatorID", "id");
		map.put("indicatorName", "name");
		map.put("indicatorItem", "item");
		map.put("indicatorLevelID", "level_id");
		map.put("indicatorCompetenceIncarnatio", "competence_incarnatio");
		map.put("indicatorCreate", "date_create");
		map.put("indicatorRemove", "date_remove");
		map.put("indicatorHost", "host");

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
		cards = new JPanel(new CardLayout());
		
		sinnerPanel = new SinnerPanel(kdbM, currentID, cachePerson, cacheRoundActors, cacheDivision, cachePosition, cacheProfile);
		sinnerPanel.addActionListener(this);
		
		competencePanel = new CompetencePanel(kdbM, currentID, cacheCompetence, cacheIndicator);
		competencePanel.addActionListener(this);
		//competencePanel.setSinnerID(50002311);

		cards.add(sinnerPanel, SINNER_PANEL);
		cards.add(competencePanel, COMPETENCE_PANEL);

		frm.setContentPane(cards);
		((CardLayout)cards.getLayout()).show(cards, SINNER_PANEL);
		frm.setSize(640, 480);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JButton) {
			JButton btn = (JButton)e.getSource();
			if(btn.getName().equals(SinnerPanel.BUTTON_NAME)) {
				competencePanel.setSinnerID(sinnerPanel.getSinnerID());
				((CardLayout)cards.getLayout()).show(cards, COMPETENCE_PANEL);
			}
			if(btn.getName().equals(CompetencePanel.BUTTON_NAME)) {
				((CardLayout)cards.getLayout()).show(cards, SINNER_PANEL);
			}

		}
		
	}
	
	public static void main(String[] args) {
		new StartTest("jdbc:sqlserver://192.168.56.2:1433;databaseName=Minos;user=sa;password=Q11W22e33;",
			"jdbc:sqlserver://192.168.56.2:1433;databaseName=serg;user=sa;password=Q11W22e33;");

	}


}
