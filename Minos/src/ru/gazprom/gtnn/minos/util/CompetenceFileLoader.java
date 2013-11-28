package ru.gazprom.gtnn.minos.util;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ru.gazprom.gtnn.minos.entity.CatalogNode;
import ru.gazprom.gtnn.minos.entity.CompetenceNode;
import ru.gazprom.gtnn.minos.entity.IndicatorNode;
import ru.gazprom.gtnn.minos.entity.LevelNode;
import ru.gazprom.gtnn.minos.entity.ProfileNode;
import ru.gazprom.gtnn.minos.entity.StringAttrNode;
import ru.gazprom.gtnn.minos.models.BasicModel;
import ru.gedr.util.tuple.Triplet;

public class CompetenceFileLoader {	
/**
 * The function load TXT file. The TXT file contain competence, description and indicators line, 
 * and may be contain a command instruction for initialize profiles
 * @param file - java.nio.Pah for load file
 * @param catalog - CatalogNode where load competence
 * @param kdb - object for work with database 
 * @throws Exception
 */
	public static void load(Path file, CatalogNode catalog, DatabaseConnectionKeeper kdb) throws Exception {
		if( (file == null) || (catalog == null) || (kdb == null) )
			return;

		Charset charset = Charset.forName("UTF-8");
		BufferedReader reader = Files.newBufferedReader(file, charset);

		// variable initialization 
		String line = null;
		int step = 0;	
		int itemIndicator = 1;
		int itemCompetence = 1;
		int itemStringAttr = 1;

		List<ProfileNode> profileNodes = null;
		int profileNodeCount = 0;

		// objects create and initialization
		CompetenceNode nodeCompetence = new CompetenceNode();
		nodeCompetence.competenceIncarnatio = 0;
		nodeCompetence.competenceCatalogID = catalog.catalogID;
		nodeCompetence.competenceVariety = catalog.catalogVariety;
		nodeCompetence.competenceChainNumber = 0;
		nodeCompetence.competenceRemove = BasicModel.endTime;

		IndicatorNode nodeIndicator = new IndicatorNode();
		nodeIndicator.indicatorRemove = BasicModel.endTime;   
		nodeIndicator.indicatorChild = 0;

		StringAttrNode stringAttrNode = new StringAttrNode();
		stringAttrNode.stringAttrVariety = StringAttrNode.VARIETY_PROFILE;
		stringAttrNode.stringAttrRemove = BasicModel.endTime;

		while ((line = reader.readLine()) != null) {
			if ( line.contains("$") ) {
				step = 1;
				itemIndicator = 1;
				itemStringAttr = 1;
				itemCompetence++;
				profileNodeCount = 0;

				if(line.contains("$#")) { // line contain profile attributes
					int rsh = line.indexOf("#");
					List<Triplet<Integer, Integer, Integer>> cmds = parseCmdString(line.substring(rsh + 1));

					profileNodeCount = (cmds == null ? 0 : cmds.size() ); 

					if(profileNodeCount > 0) {
						if(profileNodes == null) { 
							profileNodes = new ArrayList<>();
						}

						if(profileNodes.size() < profileNodeCount) { // make additional object
							int dif = cmds.size() - profileNodes.size();
							for(int i = 0; i < dif; i++)
								profileNodes.add(new ProfileNode());
						}

						for(int i = 0; i < profileNodeCount; i++) { // common initialization profile's list
							profileNodes.get(i).profileID = -1;
							profileNodes.get(i).profileVariety = ProfileNode.VARIETY__DIVISION_AND_POSITION;
							profileNodes.get(i).profileDivisionID = cmds.get(i).getFirst();
							profileNodes.get(i).profilePositionID = cmds.get(i).getSecond();
							profileNodes.get(i).profileRemove = BasicModel.endTime;
							profileNodes.get(i).profileMinLevel = ( ((1 <= cmds.get(i).getThird()) && 
									(cmds.get(i).getThird() <= LevelNode.LEVEL_COUNT)) ? cmds.get(i).getThird() : 1);
						}
					}		    				
					cmds.clear();
				}
				continue;
			}

			if(line.isEmpty()) { 
				step++;
				itemIndicator = 1;
				continue;
			}

			if(step == 1) {
				nodeCompetence.competenceName = line;
				continue;
			}

			if(step == 2) {
				nodeCompetence.competenceDescr = line;
				nodeCompetence.competenceItem = itemCompetence;

				nodeCompetence.insert(kdb,
						CompetenceNode.COMPETENCE_NAME | CompetenceNode.COMPETENCE_DESCR | 
						CompetenceNode.COMPETENCE_ITEM | CompetenceNode.COMPETENCE_CATALOG | 
						CompetenceNode.COMPETENCE_INCARNATIO | CompetenceNode.COMPETENCE_CHAIN_NUMBER |
						CompetenceNode.COMPETENCE_VARIETY | CompetenceNode.COMPETENCE_REMOVE, 
						true);

				if(profileNodeCount > 0) {
					for(int i = 0; i < profileNodeCount; i++) {
						profileNodes.get(i).profileCompetenceIncarnatio = nodeCompetence.competenceIncarnatio;
						profileNodes.get(i).insert(kdb, 
								ProfileNode.PROFILE_DIVISION | ProfileNode.PROFILE_POSITION | 
								ProfileNode.PROFILE_MIN_LEVEL | ProfileNode.PROFILE_VARIETY |
								ProfileNode.PROFILE_COMPETENCE_INCARNATIO | ProfileNode.PROFILE_REMOVE);
					}
				}
				continue;
			}

			if( (3 <= step) && (step < 3 + LevelNode.LEVEL_COUNT) ) {
				nodeIndicator.indicatorName = line;
				nodeIndicator.indicatorItem = itemIndicator++;
				nodeIndicator.indicatorLevelID = step - 2;
				nodeIndicator.indicatorCompetenceIncarnatio = nodeCompetence.competenceIncarnatio; // save to DB and TreeModel

				nodeIndicator.insert(kdb, 
						IndicatorNode.INDICATOR_NAME | IndicatorNode.INDICATOR_ITEM |
						IndicatorNode.INDICATOR_LEVEL | IndicatorNode.INDICATOR_COMPETENCE |
						IndicatorNode.INDICATOR_CHILD | IndicatorNode.INDICATOR_REMOVE );
				continue;					
			}

			if( (step == (3 + LevelNode.LEVEL_COUNT)) && ((profileNodeCount > 0)) ) {
				for(int i = 0; i < profileNodeCount; i++) {    						
					stringAttrNode.stringAttrExternalID1 = profileNodes.get(i).profileID;
					stringAttrNode.stringAttrValue = line;
					stringAttrNode.stringAttrItem = itemStringAttr++;
					stringAttrNode.insert(kdb, 
							StringAttrNode.STRING_ATTR_ITEM | StringAttrNode.STRING_ATTR_EXTERNAL_ID1 |
							StringAttrNode.STRING_ATTR_VALUE | StringAttrNode.STRING_ATTR_REMOVE |
							StringAttrNode.STRING_ATTR_VARIETY);
				}	
			}
		}
	}

	/**
	 * The function parse string use ":" symbol as number delimiter
	 * @param str is string what contain number with delimiter
	 * @return The list of profile attributes as (<DIVISION_ID>, <POSITION_ID>, <MIN_LEVEL>)
	 */
	private static List<Triplet<Integer, Integer, Integer>> parseCmdString(String str) throws Exception {		
		List<Triplet<Integer, Integer, Integer>> lst = null;
		Triplet<Integer, Integer, Integer> triplet = null;
		StringTokenizer st = new StringTokenizer(str, ":");
		int step = 1;

		while (st.hasMoreTokens()){
			switch(step) { 
			case 1:
				if(triplet == null) {
					triplet = new Triplet<>();
				}
				triplet.setFirst( Integer.valueOf(st.nextToken()) );
				break;
			case 2:
				triplet.setSecond( Integer.valueOf(st.nextToken()) );				
				break;
			case 3:
				triplet.setThird( Integer.valueOf(st.nextToken()) );
				break;
			}

			step++;

			if(step == 4) {
				step = 1;
				if(lst == null) {
					lst = new ArrayList<>();
				}
				lst.add(triplet);
				triplet = null;					
			}		   
		}

		return lst;
	}

}
