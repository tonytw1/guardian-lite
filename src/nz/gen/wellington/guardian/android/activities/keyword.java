package nz.gen.wellington.guardian.android.activities;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ListArticleAdapter;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ImageDecoratedArticle;
import nz.gen.wellington.guardian.android.model.Keyword;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

public class keyword extends Activity {

	ListAdapter adapter;
	
	public keyword() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);		
				
		final Keyword keyword = (Keyword) this.getIntent().getExtras().get("keyword");
		this.setTitle(keyword.getName());
		
    	if (keyword != null) {
    		List<Article> articles = ArticleDAOFactory.getDao(this).getKeywordItems(keyword);
    		populateNewsitemList(articles);
    	}
	}
	
	
	private void populateNewsitemList(List<Article> articles) {
		if (articles != null) {
			List<Article> newsitems = articles;				
			ListView listView = (ListView) findViewById(R.id.ArticlesListView); 
			adapter = new ListArticleAdapter(this, decorateNewsitemsWithThumbnails(newsitems));		   
			listView.setAdapter(adapter);
		}
	}

	private List<ImageDecoratedArticle> decorateNewsitemsWithThumbnails(List<Article> newsitems) {
		List<ImageDecoratedArticle> decorated = new ArrayList<ImageDecoratedArticle>();
		ImageDAO imageDAO = new ImageDAO();
		for (Article article : newsitems) {
			Bitmap image = null;			
			if (article.getThumbnailUrl() != null) {
				image = imageDAO.getImage(article.getThumbnailUrl());
			}
			decorated.add(new ImageDecoratedArticle(article, image));
		}
		return decorated;
	}
	
}
