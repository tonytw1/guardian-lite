package nz.gen.wellington.guardian.android.activities;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ArticleImageDecorator;
import nz.gen.wellington.guardian.android.activities.ui.ListArticleAdapter;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ImageDecoratedArticle;
import nz.gen.wellington.guardian.android.model.Tag;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class ArticleListActivity extends Activity {
	
	Handler updateArticlesHandler;
	List<ImageDecoratedArticle> articles;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
	}	
	
	protected void populateNewsitemList(List<Article> articles) {
		if (articles != null) {			
			List<Article> newsitems = articles;	
			ListView listView = (ListView) findViewById(R.id.ArticlesListView);
			ListAdapter adapter = new ListArticleAdapter(this, ArticleImageDecorator.decorateNewsitemsWithThumbnails(newsitems, ArticleDAOFactory.getImageDao(this)));		   
			listView.setAdapter(adapter);
		}
	}
	
	protected void setHeading(String headingText) {
		TextView heading = (TextView) findViewById(R.id.Heading);
		heading.setText(headingText);		
	}
	
	protected void setHeadingColour(String colour) {
		LinearLayout heading = (LinearLayout) findViewById(R.id.HeadingLayout);
		heading.setBackgroundColor(Color.parseColor(colour));
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, 1, 0, "Most recent");
	    menu.add(0, 2, 0, "Sections");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	    case 1: 	    	
	    	switchToMostRecent();
	        return true;
	    case 2: 	    	
	    	switchToSections();
	        return true;	 
	    }	    	
	    return false;
	}


	private void switchToMostRecent() {
		Intent intent = new Intent(this, main.class);
		this.startActivity(intent);
	}
	
	private void switchToSections() {
		Intent intent = new Intent(this, sections.class);
		this.startActivity(intent);		
	}
	
	
	class UpdateArticlesHandler extends Handler {		

		private Context context;

		public UpdateArticlesHandler(Context context) {
			super();
			this.context = context;
		}
		
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d("UpdateArticlesHandler", "Populating articles");
			ListView listView = (ListView) findViewById(R.id.ArticlesListView);			
			ListAdapter adapter = new ListArticleAdapter(context, articles);		   
			listView.setAdapter(adapter);
		}
		
	}
	
	
	class UpdateArticlesRunner implements Runnable {		
		
		ArticleDAO articleDAO;
		ImageDAO imageDAO;
		Tag tag;
		
		public UpdateArticlesRunner(ArticleDAO articleDAO, ImageDAO imageDAO, Tag tag) {
			this.articleDAO = articleDAO;
			this.imageDAO = imageDAO;
			this.tag = tag;
		}
		
		public void run() {
			Log.d("UpdateArticlesRunner", "Loading articles");

			List<Article> undecoratedArticles = new ArrayList<Article>();
			if (tag != null) {
				undecoratedArticles = articleDAO.getKeywordItems(tag);
			} else {
				undecoratedArticles = articleDAO.getTopStories();				
			}
			articles = ArticleImageDecorator.decorateNewsitemsWithThumbnails(undecoratedArticles, imageDAO);
			Log.d("UpdateArticlesRunner", "Articles are available");
			
			Message m = new Message();
			m.what = 1;
			updateArticlesHandler.sendMessage(m);
		}
	}
	
	
	
}
