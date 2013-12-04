package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.google.common.base.Preconditions;

import ru.gazprom.gtnn.minos.entity.RoundActorsNode;
import ru.gazprom.gtnn.minos.models.RoundActorsTableModel;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;
import ru.gedr.util.tuple.Pair;

public class PrintResult extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private JTable tbl;
	private File patternWordFile;
	private File outDir;
	private JFileChooser fileChooser;
	private int num = 1;
	private DatabaseConnectionKeeper kdb;
	

	public PrintResult(JTable tbl, DatabaseConnectionKeeper kdb) {
		super();
		this.tbl = tbl;
		this.kdb = kdb;
		fileChooser = new  JFileChooser();		
	}



	@Override
	public void actionPerformed(ActionEvent arg0) {

		System.out.println("print result");
		int[] rows = tbl.getSelectedRows();
		RoundActorsNode[] actors = new RoundActorsNode [rows.length];
		
		for(int i = 0; i < rows.length; i ++)
			actors[i] = ((RoundActorsTableModel)tbl.getModel()).getActors(rows[i]);
		
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		if(JFileChooser.APPROVE_OPTION != fileChooser.showDialog(null, "”кажите шаблон")) {
			return;			
		}
		
		patternWordFile = fileChooser.getSelectedFile();
		System.out.println(patternWordFile);
		
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(JFileChooser.APPROVE_OPTION != fileChooser.showDialog(null, "”кажите каталог дл€ вывода")) {
			return;			
		}

		outDir = fileChooser.getSelectedFile();
		System.out.println(outDir.getAbsolutePath());

		for(int i = 0; i < actors.length; i++) {
			System.out.println("sinner  " + actors[i].roundActorsSinnerID);
			print(actors[i].roundActorsID);
		}

	}

	
	public void print(int actorsID) {
		String sql = " select mc.name, mc.variety, mrp.cost, mp.min_level from MinosRoundProfile mrp "
				+ " inner join MinosProfile mp on mp.id = mrp.profile_id "
				+ " inner join MinosRoundActors mra on mra.id = mrp.actors_id "
				+ " inner join MinosRound mr on mr.id = mra.round_id "
				+ " inner join MinosCompetence mc on mc.incarnatio = mp.competence_incarnatio "
				+ " where mr.round_start between mc.date_create and mc.date_remove "
				+ " and mrp.actors_id = %id% ";
		

		String request = kdb.makeSQLString(sql, "%id%", String.valueOf(actorsID));
		
		Preconditions.checkNotNull(request,
						"PrintResult.print() : makeListParam() return null");
					
		TableKeeper tk = null;
		try {
			tk = kdb.selectRows(request);
			
			if( (tk == null) || (tk.getRowCount() == 0) )
				return;
			
			// open and save pattern file
			XWPFDocument patternDoc = new XWPFDocument(new FileInputStream(patternWordFile));
			String workFile = outDir.getAbsolutePath() + "\\" + String.valueOf(num++) + ".docx";
			patternDoc.write(new FileOutputStream(workFile)); 

			XWPFDocument workDoc = new XWPFDocument(new FileInputStream(workFile));
			List<XWPFTable> tbls = workDoc.getTables();
			List<XWPFTableRow> rows = tbls.get(0).getRows();
			int competenceNum = 1;
			boolean fCompetenceWrite = false;
			boolean fProfileWrite = false;
			String profileColor = null;
			boolean fResultWrite = false;
			String resultColor = null;
			int counter;
			
			System.out.println(rows.size());
			for(int i= 0; i < rows.size(); i++) {
				resultColor = profileColor = null;
				counter = 0;
				List<XWPFTableCell> cells = rows.get(i).getTableCells();
				for(int j= 0; j < cells.size(); j++) {
					if(cells.get(j).getText().equalsIgnoreCase("competence")) {
						List<XWPFParagraph> cellParagraphs = cells.get(0).getParagraphs();
						
						while(cellParagraphs.get(0).getRuns().size() > 1)
							cellParagraphs.get(0).removeRun(1);
						
						XWPFRun run = cellParagraphs.get(0).getRuns().get(0);
						run.setText((String)tk.getValue(competenceNum, 1), 0);
						fCompetenceWrite = true;						
					}
					if(cells.get(j).getText().equalsIgnoreCase("profile") ) {
						//cells.get(j).setText(" ");
						
						List<XWPFParagraph> cellParagraphs = cells.get(0).getParagraphs();
						while(cellParagraphs.size() > 0)
							cellParagraphs.remove(0);						

						counter = 1;
						fProfileWrite = true;
						if(counter > (Integer)tk.getValue(competenceNum, 4))
							break;
							
						profileColor = cells.get(j).getColor();		
						
					}						
					
					if(profileColor != null) {
						counter++;						
						if(counter <= ((Integer)tk.getValue(competenceNum, 4) + 1)) {
							cells.get(j).setColor(profileColor);
							
						}
					}

					if(cells.get(j).getText().equalsIgnoreCase("result") ) {
						//cells.get(j).setText(" ");
						
						List<XWPFParagraph> cellParagraphs = cells.get(0).getParagraphs();
						while(cellParagraphs.size() > 0)
							cellParagraphs.remove(0);						

						counter = 1;
						fResultWrite = true;
						Double d = (Double)tk.getValue(competenceNum, 3);
						if(counter > d.intValue()) {
							cells.get(j).setColor(null);
							break;
						}
							
						resultColor = cells.get(j).getColor();		
						
					}						
					
					if(resultColor != null) {
						counter++;
						Double d = (Double)tk.getValue(competenceNum, 3);
						if(counter <= (d.intValue() + 1)) {
							cells.get(j).setColor(resultColor);
							
						}
					}

				}
				
				
				
				
				if(fCompetenceWrite && fProfileWrite && fResultWrite) {
					fCompetenceWrite = fResultWrite = fProfileWrite = false;
				
					competenceNum++;
					if(competenceNum > tk.getRowCount())
						break;
				}
				
			}
			workDoc.write(new FileOutputStream(workFile));
			
		} catch (Exception e) {
			e.printStackTrace();
			tk = null;
		}
					
					
	}
}
