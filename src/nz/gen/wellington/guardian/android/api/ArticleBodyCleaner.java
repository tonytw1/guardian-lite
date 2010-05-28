package nz.gen.wellington.guardian.android.api;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class ArticleBodyCleaner {
	
	
	public static String stripHtml(String content) {
		 if (content == null) {
			 return null;
		 }		 
		 Pattern p = Pattern.compile("</p>");
		 content = p.matcher(content).replaceAll("\n\n");
		 
		 Pattern tags = Pattern.compile("<.*?>");
		 return StringEscapeUtils.unescapeHtml(tags.matcher(content).replaceAll(""));
	}

}
