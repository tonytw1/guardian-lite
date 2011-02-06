/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.api.filtering;

import java.util.regex.Pattern;

public class HtmlCleaner {

	private Pattern h2end = Pattern.compile("</h2>");
	private Pattern p = Pattern.compile("</p>");
	private Pattern br = Pattern.compile("<br ?/>");
	private Pattern tags = Pattern.compile("<.*?>");
	
	public String stripHtml(String content) {
		if (content == null) {
			return null;
		}
		
		content = h2end.matcher(content).replaceAll("\n\n");
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
