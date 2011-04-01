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

package nz.gen.wellington.guardian.android.content;

import java.net.URLEncoder;
import java.util.Iterator;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleXmlParser;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.network.LoggingBufferedInputStream;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.content.Context;
import android.util.Log;

public class SavedArticlesDAO implements ArticleSource {
	
	private static final String TAG = "SavedArticlesDAO";
	private static final String ENDPOINT_URI = "/saved";
	private static final String URL_ENCODED_COMMA = URLEncoder.encode(",");	
	
	private HttpFetcher httpFetcher;
	private ContentApiStyleXmlParser contentXmlParser;
	private SettingsDAO settingsDAO;
	
		
	public SavedArticlesDAO(Context context) {
		this.contentXmlParser = new ContentApiStyleXmlParser(context);
		this.httpFetcher = new HttpFetcher(context);
		this.settingsDAO = SingletonFactory.getSettingsDAO(context);
	}
	
	
	// TODO Should probably move to an url service.
	public String getArticleSetUrl(SavedArticlesArticleSet articleSet) {
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
	
	// TODO Likely to be duplicated now that an article set holds all of it's own vital statistics.
	public ArticleBundle getArticles(ArticleSet articleSet, ArticleCallback articleCallback) {
		Log.i(TAG, "Fetching saved items");		
		LoggingBufferedInputStream input = httpFetcher.httpFetch(articleSet.getSourceUrl(), articleSet.getName());
		if (input != null) {
			ArticleBundle results = contentXmlParser.parseArticlesXml(input, articleCallback);
			if (results != null && !results.getArticles().isEmpty()) {
				String checksum = input.getEtag();
				results.setChecksum(checksum);
				return results;
			}
		}
		return null;
	}
	
}