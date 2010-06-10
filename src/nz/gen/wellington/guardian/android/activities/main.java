package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ArticleImageDecorator;
import nz.gen.wellington.guardian.android.activities.ui.ListArticleAdapter;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ImageDecoratedArticle;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

public class main extends ArticleListActivity {
	
	ListAdapter adapter;
	Handler updateArticlesHandler;
	List<ImageDecoratedArticle> articles;
	
	public main() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        setHeading("Most recent");
    	setHeadingColour("#0061A6");    	
    	updateArticlesHandler = new UpdateArticlesHandler(this);
	}
	
	
	@Override
	// TODO this works but is this the correct way todo it.
	protected void onResume() {
		super.onResume();
		Thread loader = new Thread(new UpdateArticlesRunner(ArticleDAOFactory.getDao(this), ArticleDAOFactory.getImageDao(this)));
		loader.start();
		Log.d("UpdateArticlesHandler", "Loader started");
	}

	
	class UpdateArticlesRunner implements Runnable {		
		
		ArticleDAO articleDAO;
		private ImageDAO imageDAO;
		
		public UpdateArticlesRunner(ArticleDAO articleDAO, ImageDAO imageDAO) {
			this.articleDAO = articleDAO;
			this.imageDAO = imageDAO;
		}
		
		public void run() {
			Log.d("UpdateArticlesRunner", "Loading articles");
			List<Article> undecoratedArticles = articleDAO.getTopStories();

			articles = ArticleImageDecorator.decorateNewsitemsWithThumbnails(undecoratedArticles, imageDAO);			
			Log.d("UpdateArticlesRunner", "Articles are available");
			
			Message m = new Message();
			m.what = 1;
			main.this.updateArticlesHandler.sendMessage(m);
		}
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
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, 1, 0, "Sync");
	    menu.add(0, 2, 0, "Sections");
	    menu.add(0, 3, 0, "Settings");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	    case 1: 	    	
	    	swichToSync();
	        return true;
	    case 2: 	    	
	    	switchToSections();
	        return true;	 
	    case 3:
	    	switchToPreferences();
	    	return true;
	    }
	    return false;
	}


	private void swichToSync() {
		Intent intent = new Intent(this, sync.class);
		this.startActivity(intent);	
	}
	
	private void switchToSections() {
		Intent intent = new Intent(this, sections.class);
		this.startActivity(intent);		
	}
	
	private void switchToPreferences() {
		Intent intent = new Intent(this, perferences.class);
		this.startActivity(intent);	
	}
	
}