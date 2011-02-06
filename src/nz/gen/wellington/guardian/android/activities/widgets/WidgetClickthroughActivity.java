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

package nz.gen.wellington.guardian.android.activities.widgets;

import nz.gen.wellington.guardian.android.activities.article;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public abstract class WidgetClickthroughActivity extends Activity {

	private static final String TAG = "WidgetClickthroughActivity";
	
	private SettingsDAO settingsDAO;
	protected ArticleSetFactory articleSetFactory;
	protected ArticleDAO articleDAO;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settingsDAO = SingletonFactory.getSettingsDAO(this.getApplicationContext());
		articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		articleDAO = SingletonFactory.getArticleDao(this.getApplicationContext());
	}
	
	protected void onResume() {
		super.onResume();		
		Intent intent = new Intent(this, getDefaultActivity());
		
		Uri dataUri = this.getIntent().getData();
		if (dataUri != null) {
			final String articleId = extractArticleIdFromUri(dataUri);
			
			Log.d(TAG, "Requested article id was: " + articleId);
			if (articleId != null) {
				final Article article = getArticleById(articleId);				
				if (article != null) {
					intent = new Intent(this, article.class);
					intent.putExtra("article", article);
					
				} else {
					Log.d(TAG, "Failed to find article: " + articleId);
				}
			}
			
		} else {
			Log.w(TAG, "No data uri was found on intent");
		}
		this.startActivity(intent);
	}
	
	
	protected int getPageSize() {
		return settingsDAO.getPageSizePreference();
	}

	protected abstract Class<? extends Activity> getDefaultActivity();
	
	protected abstract ArticleSet getArticleSet();
	
	
	private String extractArticleIdFromUri(Uri dataUri) {
		final String path = dataUri.getPath();
		if (path.startsWith("/id/")) {
			return path.replaceFirst("/id/", "");	
		}
		return null;
	}
		
	private Article getArticleById(final String articleId) {
		ArticleSet articleSet = getArticleSet();
		ArticleBundle bundle = articleDAO.getArticleSetArticles(articleSet, ContentFetchType.LOCAL_ONLY);
		if (bundle != null) {
			for (Article article : bundle.getArticles()) {
				if (article.getId() !=null && article.getId().equals(articleId)) {
					return article;
				}
			}
		}
		return null;
	}
	
}
