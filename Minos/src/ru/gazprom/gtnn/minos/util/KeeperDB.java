package ru.gazprom.gtnn.minos.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.gedr.util.tuple.Pair;

public class KeeperDB {
	/**
	 * Make SQL string for execute
	 * @param sourceStr is string with pattern
	 * @param patternStr is pattern string
	 * @param replaceStr is substitute string
	 * @return string of SQL for execute or null
	 */
	public String makeSQLString(String sourceStr, String patternStr, String replaceStr) {
		Pattern p = Pattern.compile(patternStr);
		Matcher m = p.matcher(sourceStr);
		StringBuffer sb = new StringBuffer();
		int count = 0;
		while (m.find()) {
			m.appendReplacement(sb, replaceStr);
			count++;
		}
		return count > 0 ? sb.toString() : null;
	}

}
