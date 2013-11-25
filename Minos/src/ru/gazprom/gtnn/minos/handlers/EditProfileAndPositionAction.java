package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;


import ru.gazprom.gtnn.minos.entity.LevelNode;

import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gedr.util.tuple.Pair;

import com.google.common.cache.LoadingCache;

public class EditProfileAndPositionAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private LoadingCache<Integer, LevelNode> cacheLevel;
	private JComboBox<String> cb;
	JComponent[] inputs; 

	public EditProfileAndPositionAction(JTree tree, LoadingCache<Integer, LevelNode> cacheLevel) {
		super();
		this.tree = tree;		
		this.cacheLevel = cacheLevel;
		
		cb = new JComboBox<>();

		try {
			for(int i = 1; i <= LevelNode.LEVEL_COUNT; i++)
				cb.addItem(cacheLevel.get(i).levelName);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return;
		}
		
		inputs = new JComponent[1];
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(tree == null)
			return;
		
		if(tree.getSelectionCount() != 1) {
			JOptionPane.showMessageDialog(null, "Необходимо выбрать только один элемент");
			return ;
		}
		
		Object obj = tree.getSelectionPath().getLastPathComponent();
		if(obj instanceof ProfileNode) {
			ProfileNode node = (ProfileNode) obj; 
			inputs[0] = cb;
			if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "Chenge level dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) ) {
				System.out.println(cb.getSelectedIndex() );
				if( (cb.getSelectedIndex() + 1) != node.profileMinLevel ) {
					node.profileMinLevel =  cb.getSelectedIndex() + 1;
					try {
						node.update(((BasicModel)tree.getModel()).getDatabaseConnectionKeeper(), ProfileNode.PROFILE_MIN_LEVEL);
					} catch (Exception e) {
						e.printStackTrace();
					}
					tree.updateUI();
				}
				
			}	
		}
		
		if(obj instanceof Pair<?, ?>) {						
			int childCount = tree.getModel().getChildCount(obj);
			if(tree.getModel().getChildCount(obj) > 0) {
				List<ProfileNode> lst = new ArrayList<>();
				for(int i = 0; i < childCount; i++)
					lst.add((ProfileNode)tree.getModel().getChild(obj, i));

				inputs[0] = new JTable(childCount, 6);
				//((JTable)inputs[0]).setm
				
				if( (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(null, inputs, "Chenge level dialog", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) ) {

				}



			}

		}
	}
}