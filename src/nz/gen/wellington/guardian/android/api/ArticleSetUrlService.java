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

import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiUrlService;
import nz.gen.wellington.guardian.android.content.AboutArticlesDAO;
import nz.gen.wellington.guardian.android.content.SavedArticlesDAO;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.content.Context;

public class ArticleSetUrlService {
	
	private SettingsDAO settingsDAO;
	private AboutArticlesDAO aboutArticlesDAO;
	private SavedArticlesDAO savedArticlesDAO;
	
	public ArticleSetUrlService(Context context) {
		settingsDAO = SingletonFactory.getSettingsDAO(context);
		aboutArticlesDAO = new AboutArticlesDAO(context);
		savedArticlesDAO = new SavedArticlesDAO(context);
	}
	
	public String getUrlForArticleSet(ArticleSet articleSet) {
		ContentApiUrlService contentApiUrlService = new ContentApiUrlService(settingsDAO.getPreferedApiHost(), settingsDAO.getApiKey(), settingsDAO.getSupportedContentTypes(), settingsDAO.shouldShowMedia());
		if (articleSet instanceof AboutArticleSet) {
			return aboutArticlesDAO.getArticleSetUrl();
		}
		if (articleSet instanceof SavedArticlesArticleSet) {
			return savedArticlesDAO.getArticleSetUrl(((SavedArticlesArticleSet) articleSet));
		}
		return contentApiUrlService.getContentApiUrlForArticleSet(articleSet);
	}
	
}
