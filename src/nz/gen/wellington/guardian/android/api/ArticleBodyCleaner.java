package nz.gen.wellington.guardian.android.api;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class ArticleBodyCleaner {
	
	static Pattern p = Pattern.compile("</p>");
	static Pattern tags = Pattern.compile("<.*?>");
	
	public static String stripHtml(String content) {
		 if (content == null) {
			 return null;
		 }		 
		 content = p.matcher(content).replaceAll("\n\n");
		 content = StringEscapeUtils.unescapeHtml(tags.matcher(content).replaceAll(""));
		 
		 content = content.replaceAll("&amp;", "&");
		 content = content.replaceAll("&nbsp;", " ");			
		 return content;
	}

}
