package nz.gen.wellington.guardian.android.activities.widgets;

import nz.gen.wellington.guardian.android.activities.article;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public abstract class WidgetClickthroughActivity extends Activity {

	private static final String TAG = "WidgetClickthroughActivity";
	
	private PreferencesDAO preferencesDAO;
	protected ArticleSetFactory articleSetFactory;
	protected ArticleDAO articleDAO;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferencesDAO = SingletonFactory.getPreferencesDAO(this.getApplicationContext());
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
		return preferencesDAO.getPageSizePreference();
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
