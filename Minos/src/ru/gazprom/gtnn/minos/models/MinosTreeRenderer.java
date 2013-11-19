package ru.gazprom.gtnn.minos.models;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.LevelNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gedr.util.tuple.Pair;


public class MinosTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private static JLabel cell = new JLabel(); 
	private static ImageIcon competence = new ImageIcon("C:\\tmp\\icon\\competence.png");
	private static ImageIcon catalog = new ImageIcon("C:\\tmp\\icon\\folder.png");
	private static ImageIcon level = new ImageIcon("C:\\tmp\\icon\\level.png");
	private static ImageIcon indicator = new ImageIcon("C:\\tmp\\icon\\indicator.png");
	private static ImageIcon division = new ImageIcon("C:\\tmp\\icon\\division.png");
	private static ImageIcon position = new ImageIcon("C:\\tmp\\icon\\position.png");

	private static Color selectedColor = Color.GRAY;
	private static Color unselectedColor = Color.WHITE;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		cell.setOpaque(true);
		cell.setBackground(selected ? selectedColor : unselectedColor);
		if(value instanceof CatalogNode) {			
			cell.setIcon(catalog);
			cell.setText(((CatalogNode)value).catalogName + ((CatalogNode)value).catalogMode);			
			return cell;
		}

		
		if(value instanceof DivisionNode) {
			cell.setIcon(division);
			cell.setText(((DivisionNode)value).divisionName);
			return cell;
		}
		
		if(value instanceof CompetenceNode) {
			cell.setIcon(competence);
			cell.setText(((CompetenceNode)value).competenceName);
			return cell;
		}

		if(value instanceof IndicatorNode) {
			cell.setIcon(indicator);
			cell.setText(((IndicatorNode)value).indicatorName);
			return cell;
		}

		
		if(value instanceof Pair<?, ?>) {
			if(tree.getName().equals("CompetenceAndCatalog")) {
				@SuppressWarnings("unchecked")
				Pair<Integer, LevelNode> p = (Pair<Integer, LevelNode>)value; 
				cell.setIcon(level);
				cell.setText(p.getSecond().levelName);
				return cell;				
			} else {
				// tree PositionInDivision
				@SuppressWarnings("unchecked")
				Pair<Integer, PositionNode> p = (Pair<Integer, PositionNode>)value;
				cell.setIcon(position);
				cell.setText(p.getSecond().positionName);
				return cell;
				
			}
		}
		


		
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

	public MinosTreeRenderer() {
		super();
		
		// TODO Auto-generated constructor stub
	}

}







