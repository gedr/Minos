package ru.gazprom.gtnn.minos.models;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
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
import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.PositionNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gedr.util.tuple.Pair;


public class MinosTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private JLabel cell = new JLabel(); 
	
	private ImageIcon competence1 =  new ImageIcon(getClass().getResource("/img/book_green_24.png")); 
	private ImageIcon competence2 =  new ImageIcon(getClass().getResource("/img/book_yellow_24.png"));
	private ImageIcon competence3 =  new ImageIcon(getClass().getResource("/img/book_red_24.png"));

	private ImageIcon catalog1 =  new ImageIcon(getClass().getResource("/img/folder_green_24.png")); 
	private ImageIcon catalog2 =  new ImageIcon(getClass().getResource("/img/folder_yellow_24.png"));
	private ImageIcon catalog3 =  new ImageIcon(getClass().getResource("/img/folder_red_24.png"));

	private ImageIcon indicator =  new ImageIcon(getClass().getResource("/img/page_24.png"));

	private ImageIcon[] level = { new ImageIcon(getClass().getResource("/img/level0_24.png")),
			
			new ImageIcon(getClass().getResource("/img/level1_24.png")),
			new ImageIcon(getClass().getResource("/img/level2_24.png")),
			new ImageIcon(getClass().getResource("/img/level3_24.png")),
			new ImageIcon(getClass().getResource("/img/level4_24.png")),
			new ImageIcon(getClass().getResource("/img/level5_24.png")) };	

	private ImageIcon division =  new ImageIcon(getClass().getResource("/img/office_24.png"));
	private ImageIcon position =  new ImageIcon(getClass().getResource("/img/users_24.png"));
	private ImageIcon person =  new ImageIcon(getClass().getResource("/img/user_24.png"));

	private static Color selectedColor = Color.LIGHT_GRAY;
	private static Color unselectedColor = Color.WHITE;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		cell.setOpaque(true);
		cell.setBackground(selected ? selectedColor : unselectedColor);
		cell.setText("");
		cell.setIcon(null);
		if(value instanceof CatalogNode) {
			CatalogNode node = (CatalogNode)value;
			cell.setIcon((node.catalogVariety == 1 ? catalog1 : (node.catalogVariety == 2 ? catalog2 : catalog3) ));
			cell.setText(node.catalogName);			
			return cell;
		}

		if(value instanceof DivisionNode) {
			cell.setIcon(division);
			cell.setText(((DivisionNode)value).divisionName);
			return cell;
		}
		
		if(value instanceof CompetenceNode) {
			CompetenceNode node = (CompetenceNode)value;
			cell.setIcon((node.competenceVariety == 1 ? competence1 : (node.competenceVariety == 2 ? competence2 : competence3) ));
			cell.setText(node.competenceName);
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
				LevelNode node = (LevelNode)p.getSecond();
				cell.setIcon(level[node.levelID]);			
				cell.setText(((LevelNode)p.getSecond()).levelName);
				return cell;				
			}
			if(p.getSecond() instanceof PositionNode) {
				cell.setIcon(position);
				cell.setText(((PositionNode)p.getSecond()).positionName);
				return cell;
			}
		}
		
		if(value instanceof PersonNode) {
			System.out.println("PersonNode");
			PersonNode node = (PersonNode)value;
			cell.setIcon(person);
			cell.setText(node.personSurname + " " + node.personName + " " + node.personPatronymic);
			return cell;
		}

		if(value instanceof ProfileNode) {
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

			if( (cn != null) && (ln !=null) ) {
				ImageIcon icon1 = ( cn.competenceVariety == 1 ? competence1 : (cn.competenceVariety == 2 ? competence2 : competence3) ); 
				ImageIcon icon2 = level[ln.levelID];
				
				cell.setIcon(compositeIcon(icon1, icon2, 3));
				cell.setText(cn.competenceName);				
			}
			
			return cell;
		}
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}
	
	private ImageIcon  compositeIcon(ImageIcon icon1, ImageIcon icon2, int spacer) {
		 final BufferedImage compositeImage = new BufferedImage( icon1.getIconWidth() + icon2.getIconWidth(),
				 (icon1.getIconHeight() >  icon2.getIconHeight() ? icon2.getIconHeight() : icon1.getIconHeight()), 
				 BufferedImage.TYPE_INT_ARGB );
	        
		 final Graphics graphics = compositeImage.createGraphics();
		 graphics.drawImage(icon1.getImage(), 0, 0, null);
		 graphics.drawImage(icon2.getImage(), icon1.getIconWidth() + spacer, 0, null);	 

		 return new ImageIcon( compositeImage );
	}

	public MinosTreeRenderer(LoadingCache<Integer, CompetenceNode> cacheCompetence,
			LoadingCache<Integer, LevelNode> cacheLevel) {
		super();
		this.cacheCompetence = cacheCompetence;
		this.cacheLevel = cacheLevel;		
	}

	private LoadingCache<Integer, CompetenceNode> cacheCompetence;
	private LoadingCache<Integer, LevelNode> cacheLevel;
}