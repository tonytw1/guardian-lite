package nz.gen.wellington.guardian.android.api.filtering;

import java.util.regex.Pattern;

public class HtmlCleaner {

	static Pattern p = Pattern.compile("</p>");
	static Pattern br = Pattern.compile("<br />");

	static Pattern tags = Pattern.compile("<.*?>");

	public static String stripHtml(String content) {
		if (content == null) {
			return null;
		}
		content = p.matcher(content).replaceAll("\n\n");
		content = br.matcher(content).replaceAll("\n");

		content  = tags.matcher(content).replaceAll("");
		// TODO content = StringEscapeUtils.unescapeHtml(content);
		
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("&nbsp;", " ");
		return content.trim();
	}

}
