package ru.gazprom.gtnn.minos.models;

import java.awt.Color;
import java.awt.Component;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.DivisionNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.LevelNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
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
			cell.setText(((CatalogNode)value).catalogName);			
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
			@SuppressWarnings("unchecked")
			Pair<Integer, Object> p = (Pair<Integer, Object>) value;
			if(p.getSecond() instanceof LevelNode) {
				cell.setIcon(level);
				cell.setText(((LevelNode)p.getSecond()).levelName);
				return cell;				
			}
			if(p.getSecond() instanceof PositionNode) {
				cell.setIcon(position);
				cell.setText(((PositionNode)p.getSecond()).positionName);
				return cell;
			}
		}

		
		if(value instanceof ProfileNode) {
			cell.setIcon(competence);
			CompetenceNode cn = null;
			LevelNode ln = null;
			try {
				cn = cacheCompetence.get( ((ProfileNode)value).profileCompetenceID );
				ln = cacheLevel.get(((ProfileNode)value).profileMinLevel );
				
			} catch (ExecutionException e) {
				e.printStackTrace();
				cn = null;
				ln = null;
			}
			sb.delete(0, sb.length());
			sb.append(cn == null ? "null" : cn.competenceName).
			append(" [ Минимальный уровень : ").
			append(ln == null ? "null"  : ln.levelName).
			append(" ] "); 
			
			cell.setText(sb.toString());			
			return cell;
		}


		
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

	public MinosTreeRenderer(LoadingCache<Integer, CompetenceNode> cacheCompetence,
			LoadingCache<Integer, LevelNode> cacheLevel) {
		super();
		this.cacheCompetence = cacheCompetence;
		this.cacheLevel = cacheLevel;
		sb = new StringBuilder();
		
	}

	private LoadingCache<Integer, CompetenceNode> cacheCompetence;
	private LoadingCache<Integer, LevelNode> cacheLevel;
	private StringBuilder sb; 

}







