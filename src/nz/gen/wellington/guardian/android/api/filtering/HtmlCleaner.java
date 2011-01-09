package nz.gen.wellington.guardian.android.api.filtering;

import java.util.regex.Pattern;

public class HtmlCleaner {

	private Pattern p = Pattern.compile("</p>");
	private Pattern br = Pattern.compile("<br />");
	private Pattern tags = Pattern.compile("<.*?>");
	
	public String stripHtml(String content) {
		if (content == null) {
			return null;
		}
		content = p.matcher(content).replaceAll("\n\n");
		content = br.matcher(content).replaceAll("\n");

		content  = tags.matcher(content).replaceAll("");
		// TODO content = StringEscapeUtils.unescapeHtml(content);
		
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("&nbsp;", " ");
		
		content = content.replaceAll("\n{2,}", "\n\n");
		return content.trim();
	}

}
