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

package nz.gen.wellington.guardian.android.utils;

import nz.gen.wellington.guardian.model.Article;

public class ShareTextComposingService {
	
	private static final String SPACE = " ";
	private static final int MAX_LENGTH = 140;

	public static String composeShareText(Article article) {
		String url = article.getShortUrl();
		if (url == null) {
			url = article.getWebUrl();
		}
		if (article.getHeadline() != null && url != null) {
			if (article.getHeadline().length() + SPACE.length() + url.length() <= MAX_LENGTH) {
				return article.getHeadline() + SPACE + url;
			}
		}
		return null;
	}
	
}
