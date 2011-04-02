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

package nz.gen.wellington.guardian.android.api;

import java.net.URLEncoder;
import java.util.Iterator;

import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiUrlService;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.content.Context;

public class ArticleSetUrlService {
	
	private static final String ABOUT_ENDPOINT_URI = "/about";
	private static final String ENDPOINT_URI = "/saved";
	private static final String URL_ENCODED_COMMA = URLEncoder.encode(",");	
	
	private SettingsDAO settingsDAO;
	
	public ArticleSetUrlService(Context context) {
		settingsDAO = SingletonFactory.getSettingsDAO(context);
	}
	
	public String getUrlForArticleSet(ArticleSet articleSet) {
		ContentApiUrlService contentApiUrlService = new ContentApiUrlService(settingsDAO.getPreferedApiHost(), settingsDAO.getApiKey(), settingsDAO.getSupportedContentTypes());
		if (articleSet instanceof AboutArticleSet) {
			return getAboutArticleSetUrl();
		}
		
		articleSet.setShowMedia(settingsDAO.shouldShowMedia());
		
		if (articleSet instanceof SavedArticlesArticleSet) {
			return getSavedArticlesArticleSetUrl(((SavedArticlesArticleSet) articleSet));
		}
		return contentApiUrlService.getContentApiUrlForArticleSet(articleSet);
	}
	
	private String getAboutArticleSetUrl() {
		return settingsDAO.getGuardianLiteProxyHost() + ABOUT_ENDPOINT_URI;
	}
		
	private String getSavedArticlesArticleSetUrl(SavedArticlesArticleSet articleSet) {
		StringBuilder url = new StringBuilder(settingsDAO.getGuardianLiteProxyHost() + ENDPOINT_URI);
		if (!articleSet.getArticlesIds().isEmpty()) {
			url.append("?content=");
			for (Iterator<String> iterator = articleSet.getArticlesIds().iterator(); iterator.hasNext();) {
				String articleId = (String) iterator.next();
				url.append(URLEncoder.encode(articleId));
				if (iterator.hasNext()) {
					url.append(URL_ENCODED_COMMA);
				}
			}
		}
		return url.toString();
	}
	
}
