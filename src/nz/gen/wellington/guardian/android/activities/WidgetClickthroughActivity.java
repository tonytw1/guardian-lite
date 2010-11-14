package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public abstract class WidgetClickthroughActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void onResume() {
		super.onResume();
		Uri dataUri = this.getIntent().getData();
		if (dataUri != null) {
			final String articleId = dataUri.getLastPathSegment();

			ArticleSet articleSet = getArticleSet();
			ArticleDAO articleDAO = ArticleDAOFactory.getDao(this.getApplicationContext());
			ArticleBundle bundle = articleDAO.getArticleSetArticles(articleSet, ContentFetchType.LOCAL_ONLY);
			if (bundle != null) {
				for (Article article : bundle.getArticles()) {
					if (article.getId().equals(articleId)) {
						Intent intent = new Intent(this, article.class);
						intent.putExtra("article", article);
						this.startActivity(intent);
						return;
					}
				}
			}
			
		}
		Intent intent = new Intent(this, main.class);
		this.startActivity(intent);
	}
	
	protected abstract ArticleSet getArticleSet();
	
}
