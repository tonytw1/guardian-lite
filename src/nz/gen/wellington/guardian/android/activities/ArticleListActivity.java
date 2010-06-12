package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ArticleClicker;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class ArticleListActivity extends Activity {
	
	private static final String TAG = "ArticleListActivity";
	
	Handler updateArticlesHandler;
	UpdateArticlesRunner updateArticlesRunner;
	List<Article> articles;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
	}
	
	
	@Override
	// TODO this works but is this the correct way todo it
	// http://developer.android.com/guide/topics/fundamentals.html#actlife says use a Broadcast listener
	protected void onStart() {
		super.onStart();
		
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);		
		//mainPane.removeAllViews();
		boolean mainPaneNeedsPopulating = mainPane.getChildCount() ==0;
		if (mainPaneNeedsPopulating) {
			updateArticlesRunner = new UpdateArticlesRunner(ArticleDAOFactory.getDao(this), ArticleDAOFactory.getImageDao(this), this);
			Thread loader = new Thread(updateArticlesRunner);
			loader.start();
			Log.d("UpdateArticlesHandler", "Loader started");			
		}
	}

	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "On stop - want to halt any running threads");
		updateArticlesRunner.stop();
		Log.d(TAG, "Loader stopped");
	}
	
		
	protected void setHeading(String headingText) {
		TextView heading = (TextView) findViewById(R.id.Heading);
		heading.setText(headingText);		
	}
	
	protected void setHeadingColour(String colour) {
		LinearLayout heading = (LinearLayout) findViewById(R.id.HeadingLayout);
		heading.setBackgroundColor(Color.parseColor(colour));
	}
	
	
	protected abstract List<Article> loadArticles();
	
	
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
			
			LayoutInflater mInflater = LayoutInflater.from(context);
			if (articles != null) {
				LinearLayout mainpane = (LinearLayout) findViewById(R.id.MainPane);
				for (Article article : articles) {
					View view = mInflater.inflate(R.layout.list, null);					
					populateArticleListView(article, view);	    	
					mainpane.addView(view);
				}				
				View favourite = mInflater.inflate(R.layout.favourite, null);
				mainpane.addView(favourite);
				
			} else {
				Log.d(TAG, "No articles to populate");
			}
			
		}

		private void populateArticleListView(Article article, View view) {
			Log.d(TAG, "Populating view for article: " + article.getTitle());
			TextView titleText = (TextView) view.findViewById(R.id.TextView01);
			//ImageDecoratedArticle article = articles.get(position);
			titleText.setText(article.getTitle());
			
			if (article.getSection() != null) {
				//titleText.setTextColor(Color.parseColor(SectionColourMap.getColourForSection(article.getSection().getId())));
			}
			
			TextView pubDateText = (TextView) view.findViewById(R.id.TextView02);
			if (article.getPubDate() != null) {
				pubDateText.setText(article.getPubDateString());
			}
			
			//ImageView imageView = (ImageView) view.findViewById(R.id.TrailImage);
			//getArticleThumbnail(article, imageView);
			
			ArticleClicker urlListener = new ArticleClicker(article);
			view.setOnClickListener(urlListener);
		}
		
	}
	
	
	class UpdateArticlesRunner implements Runnable {		
		boolean running;
		ArticleDAO articleDAO;
		ImageDAO imageDAO;
		private Context context;
		
		public UpdateArticlesRunner(ArticleDAO articleDAO, ImageDAO imageDAO, Context context) {
			this.articleDAO = articleDAO;
			this.imageDAO = imageDAO;
			this.running = true;
			this.context = context;
		}
		
		public void run() {
			Log.d("UpdateArticlesRunner", "Loading articles");

			if (running) {
				articles = loadArticles();				
			}
			
			if (articles == null) {
				Toast.makeText(context, "Articles could not be loaded", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (running) {
				//articles = ArticleImageDecorator.decorateNewsitemsWithThumbnails(undecoratedArticles, imageDAO);
				//Log.d("UpdateArticlesRunner", "Articles are available");
			}
			
			if (running) {
				Message m = new Message();
				m.what = 1;
				updateArticlesHandler.sendMessage(m);
			}
		}

		public void stop() {
			this.running = false;
		}
	}
	
		
}
