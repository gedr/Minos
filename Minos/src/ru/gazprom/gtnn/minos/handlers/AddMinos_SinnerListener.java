package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import ru.gazprom.gtnn.minos.entity.PersonNode;

public class AddMinos_SinnerListener implements ActionListener {

	private JTree sinnerTree;
	private JTree minosTree;


	public AddMinos_SinnerListener(JTree minosTree, JTree sinnerTree) {
		this.minosTree = minosTree;
		this.sinnerTree = sinnerTree;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(minosTree.isSelectionEmpty() || sinnerTree.isSelectionEmpty())
			return;
		
		TreePath[] minosesPath = minosTree.getSelectionPaths();
		TreePath[] sinnersPath = sinnerTree.getSelectionPaths();
				
		for(TreePath minosPath : minosesPath) {
			if(minosPath.getLastPathComponent() instanceof PersonNode) {
				PersonNode minos =  (PersonNode) minosPath.getLastPathComponent();
				for(TreePath sinnerPath : sinnersPath) {
					if( (sinnerPath.getLastPathComponent() instanceof PersonNode) && // выделенный элементявляется деревом
							(sinnerTree.getModel().getChildCount(sinnerPath.getLastPathComponent()) > 0) ) { // у выделеного элемента есть профили
						PersonNode sinner =  (PersonNode) sinnerPath.getLastPathComponent();
					}
				}
			}
		}
		

	}

}
