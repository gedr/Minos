package ru.gazprom.gtnn.minos.handlers;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import ru.gazprom.gtnn.minos.entity.PersonNode;
import ru.gazprom.gtnn.minos.entity.RoundActorsNode;
import ru.gazprom.gtnn.minos.models.RoundActorsTableModel;
import ru.gazprom.gtnn.minos.util.DatabaseConnectionKeeper;
import ru.gazprom.gtnn.minos.util.TableKeeper;

public class PrintResult extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private JTable tbl;
	private File patternWordFile;
	private File outDir;
	private JFileChooser fileChooser;
	private int num = 1;
	private DatabaseConnectionKeeper kdb;
	private LoadingCache<Integer, PersonNode> cachePerson;


	public PrintResult(JTable tbl, DatabaseConnectionKeeper kdb, LoadingCache<Integer, PersonNode> cachePerson) {
		super();
		this.tbl = tbl;
		this.kdb = kdb;
		this.cachePerson = cachePerson;
		fileChooser = new  JFileChooser();		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		//get selected RoundActorsNode from table
		int[] rows = tbl.getSelectedRows();
		RoundActorsNode[] actors = new RoundActorsNode [rows.length];
		for(int i = 0; i < rows.length; i ++)
			actors[i] = ((RoundActorsTableModel)tbl.getModel()).getActors(rows[i]);

		// user select pattern WORD file and directory for export
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(JFileChooser.APPROVE_OPTION != fileChooser.showDialog(null, "Выбор шаблона")) 
			return;			
		patternWordFile = fileChooser.getSelectedFile();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(JFileChooser.APPROVE_OPTION != fileChooser.showDialog(null, "Выбор каталога для вывода"))
			return;			
		outDir = fileChooser.getSelectedFile();

		StringBuilder sb = new StringBuilder();
		PersonNode minos;
		PersonNode sinner;
		try {
			// print selected node
			for(int i = 0; i < actors.length; i++) {
				minos = cachePerson.get(actors[i].roundActorsMinosID);
				sinner = cachePerson.get(actors[i].roundActorsSinnerID);

				sb.delete(0,  sb.length());
				sb.append(outDir.getAbsolutePath()).append("\\").
				append(minos.personSurname).append(minos.personName).append(minos.personPatronymic).append("_").
				append(sinner.personSurname).append(sinner.personName).append(sinner.personPatronymic).append("_").
				append(System.currentTimeMillis()).append(".docx");

				print(actors[i].roundActorsID, sb.toString());	
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Очищает клеткe, оставляет только 1 Paragraph и в нем 1 Run 
	 * @param cell
	 */
	private void clearParagraphsAndRuns(XWPFTableCell cell) {
		List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
		
		while(cellParagraphs.get(0).getRuns().size() > 1)
			cellParagraphs.get(0).removeRun(1);

		List<XWPFRun> runs = cellParagraphs.get(0).getRuns();
		while(runs.size() > 1) 
			cellParagraphs.get(0).removeRun(runs.size() - 1);
	}
	
	/**
	 * поиск в строке ячейки с полем содержащим маркер CompetenceTestResult
	 * @return
	 */
	private int findCompetenceTestResult(XWPFTableRow row) {
		//row.get
		List<XWPFTableCell> cells = row.getTableCells();
		for(int i = 0; i < cells.size(); i++)
			if(cells.get(i).getText().equalsIgnoreCase("Competence_Test_Result")) 
				return i;
		
		return -1;
	}
	
	public void print(int actorsID, String outputFileName) {
		double commonResult = 0.0;
		String sql = " select mc.name, mc.variety, mrp.cost, mp.min_level from MinosRoundProfile mrp "
				+ " inner join MinosProfile mp on mp.id = mrp.profile_id "
				+ " inner join MinosRoundActors mra on mra.id = mrp.actors_id "
				+ " inner join MinosRound mr on mr.id = mra.round_id "
				+ " inner join MinosCompetence mc on mc.incarnatio = mp.competence_incarnatio "
				+ " where mr.round_start between mc.date_create and mc.date_remove "
				+ " and mrp.actors_id = %id% "
				+ " order by mc.variety";
		

		String request = kdb.makeSQLString(sql, "%id%", String.valueOf(actorsID));
		
		Preconditions.checkNotNull(request,
						"PrintResult.print() : makeListParam() return null");
					
		TableKeeper tk = null;
		try {
			tk = kdb.selectRows(request);
			
			if( (tk == null) || (tk.getRowCount() == 0) )
				return;
			int profileSum = 0;
			double resultSum = 0.0;
			for(int i = 0; i < tk.getRowCount(); i++) {
				profileSum += (Integer)tk.getValue(i + 1, 4);
				resultSum += (Double)tk.getValue(i + 1, 3);
			}
			commonResult = resultSum * 100 / profileSum;
							
			// open and save pattern file
			XWPFDocument patternDoc = new XWPFDocument(new FileInputStream(patternWordFile));
			patternDoc.write(new FileOutputStream(outputFileName)); 

			XWPFDocument workDoc = new XWPFDocument(new FileInputStream(outputFileName));
			List<XWPFTable> tbls = workDoc.getTables();
			List<XWPFTableRow> rows = tbls.get(0).getRows();
			int competenceNum = 1;
			boolean fCompetenceWrite = false;
			boolean fProfileWrite = false;
			String profileColor = null;
			boolean fResultWrite = false;
			String resultColor = null;
			int counter;
			
			boolean flagCompetenceListFinish = false;
			int rowForDeleteStart = - 1;
			int rowForDeleteStop = -1;
			
			System.out.println(rows.size());
			for(int i= 0; i < rows.size(); i++) {
				int num = findCompetenceTestResult(rows.get(i));
				if(num >= 0) {
					clearParagraphsAndRuns(rows.get(i).getCell(num));
					rows.get(i).getCell(num).getParagraphs().get(0).getRuns().get(0).setText(String.format("%4.1f", commonResult) + " %", 0);
				}
				if( flagCompetenceListFinish && (num < 0) ) {
					rowForDeleteStart = (rowForDeleteStart < 0 ? i : rowForDeleteStart);
					rowForDeleteStop = i;
				}
				
				resultColor = profileColor = null;
				counter = 0;		
				List<XWPFTableCell> cells = rows.get(i).getTableCells();
				for(int j= 0; j < cells.size(); j++) {
					if(cells.get(j).getText().equalsIgnoreCase("competence") && !flagCompetenceListFinish) {
						clearParagraphsAndRuns(cells.get(j));

						cells.get(j).getParagraphs().get(0).
							getRuns().get(0).setText((String)tk.getValue(competenceNum, 1), 0);
						
						fCompetenceWrite = true;						
						continue;
					}

					if(cells.get(j).getText().equalsIgnoreCase("profile_level") && !flagCompetenceListFinish) {
						clearParagraphsAndRuns(cells.get(j));

						XWPFRun run = cells.get(j).getParagraphs().get(0).getRuns().get(0);
						run.setText(" ", 0);
						profileColor = run.getColor(); 
						
						counter = 1;
						fProfileWrite = true;
						if(counter > (Integer)tk.getValue(competenceNum, 4))
							break;
					}						
					
					if(profileColor != null) {											
						if(counter <= ((Integer)tk.getValue(competenceNum, 4) )) {
							cells.get(j).setColor(profileColor);							
							counter++;	
							continue;
						}
						break;
					}

					if(cells.get(j).getText().equalsIgnoreCase("Result_level") && !flagCompetenceListFinish) {
						clearParagraphsAndRuns(cells.get(j));

						XWPFRun run = cells.get(j).getParagraphs().get(0).getRuns().get(0);
						run.setText(" ", 0);						
						resultColor = run.getColor();
						
						counter = 1;
						fResultWrite = true;
						Double d = (Double)tk.getValue(competenceNum, 3);
						if(counter > d.intValue()) 
							break;												
					}						
					
					if(resultColor != null) {						
						Double d = (Double)tk.getValue(competenceNum, 3);
						if(counter <= d.intValue() ) {
							cells.get(j).setColor(resultColor);
							counter++;
							continue;
						}
						break;
					}
				}				
				
				if(fCompetenceWrite && fProfileWrite && fResultWrite) {
					fCompetenceWrite = fResultWrite = fProfileWrite = false;				
					competenceNum++;
					if(competenceNum > tk.getRowCount())
						flagCompetenceListFinish = true;
				}				
			}

			//remove superfluous rows
			int diff = rowForDeleteStop - rowForDeleteStart + 1;
			for(int i = 0; i < diff; i++)
				tbls.get(0).removeRow(rowForDeleteStart);
			
			workDoc.write(new FileOutputStream(outputFileName));			
		} catch (Exception e) {
			e.printStackTrace();
			tk = null;
		}					
	}
}
