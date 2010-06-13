package nz.gen.wellington.guardian.android.activities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ArticleClicker;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class ArticleListActivity extends Activity {
	
	private static final String TAG = "ArticleListActivity";
	
	Handler updateArticlesHandler;
	UpdateArticlesRunner updateArticlesRunner;
	List<Article> articles;
	Map<String, View> viewsWaitingForTrailImages;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		viewsWaitingForTrailImages = new HashMap<String, View>();
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);		
		//mainPane.removeAllViews();
		boolean mainPaneNeedsPopulating = mainPane.getChildCount() == 0;
		if (mainPaneNeedsPopulating) {
			updateArticlesRunner = new UpdateArticlesRunner(ArticleDAOFactory.getDao(this), ArticleDAOFactory.getImageDao(this));
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
			
			Log.d(TAG, "Message: " + msg.toString());
			switch (msg.what) {	   
			    case 1:  		
					Log.d("UpdateArticlesHandler", "Populating articles");
				
					LayoutInflater mInflater = LayoutInflater.from(context);
					if (articles != null) {
						LinearLayout mainpane = (LinearLayout) findViewById(R.id.MainPane);
						for (Article article : articles) {
							View view = mInflater.inflate(R.layout.list, null);					
							populateArticleListView(article, view);	    	
							mainpane.addView(view);
						}				
						//View favourite = mInflater.inflate(R.layout.favourite, null);
						//mainpane.addView(favourite);
					
					} else {
						Log.d(TAG, "No articles to populate");
					}
					return;
			    
			    
			    case 2: 
			    	Toast.makeText(context, "Articles could not be loaded", Toast.LENGTH_SHORT).show();
			    	return;
			    
			    
			    case 3: 

			    	Bundle data = msg.getData();
			    	if (data.containsKey("url")) {
			    		final String url = data.getString("url");
			    		Log.d(TAG, "Pushing trail image: " + url);
			    		if( viewsWaitingForTrailImages.containsKey(url)) {
			    			View view = viewsWaitingForTrailImages.get(url);
			    			ImageView trialImage = (ImageView) view.findViewById(R.id.TrailImage);
			    			Bitmap image = ArticleDAOFactory.getImageDao(context).getImage(url);
			    			trialImage.setImageBitmap(image);
			    			viewsWaitingForTrailImages.remove(url);
			    		}
			    	}			    
			    	return;
			}
		}

		private void populateArticleListView(Article article, View view) {
			Log.d(TAG, "Populating view for article: " + article.getTitle());
			TextView titleText = (TextView) view.findViewById(R.id.TextView01);
			titleText.setText(article.getTitle());
			
			if (article.getSection() != null) {
				//titleText.setTextColor(Color.parseColor(SectionColourMap.getColourForSection(article.getSection().getId())));
			}
			
			TextView pubDateText = (TextView) view.findViewById(R.id.TextView02);
			if (article.getPubDate() != null) {
				pubDateText.setText(article.getPubDateString() + article.getStandfirst());
			}
			
			if (article.getThumbnailUrl() != null) {
				Log.d(TAG, "Need trailimage: " + article.getThumbnailUrl());
				viewsWaitingForTrailImages.put(article.getThumbnailUrl(), view);
				Log.d(TAG, "Size: " + Integer.toString(viewsWaitingForTrailImages.keySet().size()));
			
			} else {
    			//ImageView trailImage = (ImageView) view.findViewById(R.id.TrailImage);
    			//trailImage.setImageResource(R.drawable.icon);    			
			}
			
			ArticleClicker urlListener = new ArticleClicker(article);
			view.setOnClickListener(urlListener);
		}
		
	}
	
	
	class UpdateArticlesRunner implements Runnable {		
		boolean running;
		ArticleDAO articleDAO;
		ImageDAO imageDAO;
		
		public UpdateArticlesRunner(ArticleDAO articleDAO, ImageDAO imageDAO) {
			this.articleDAO = articleDAO;
			this.imageDAO = imageDAO;
			this.running = true;
		}
		
		public void run() {
			Log.d("UpdateArticlesRunner", "Loading articles");

			if (running) {
				articles = loadArticles();
			}
			
			if (articles == null) {
				Message m = new Message();
				m.what = 2;
				Log.d(TAG, "Sending message; articles failed to load");
				updateArticlesHandler.sendMessage(m);
				return;

			} else {
				Message m = new Message();
				m.what = 1;
				Log.d(TAG, "Sending message; articles are loaded");
				updateArticlesHandler.sendMessage(m);								
			}
			
			
			List<String> requiredTrailImages = new LinkedList<String>();
			for (Article article : articles) {
				if (article.getThumbnailUrl() != null) {
					Log.d(TAG, "Need trailimage: " + article.getThumbnailUrl());
					requiredTrailImages.add(article.getThumbnailUrl());
					Log.d(TAG, "Size: " + Integer.toString(requiredTrailImages.size()));
				}
			}
			
			List<String> downloadTrailImages = new LinkedList<String>();
			if (running) {
				Log.d(TAG, "Views awaiting trailimages: " + requiredTrailImages);
				for (String trailImageUrl : requiredTrailImages) {
					Log.d(TAG, "Loading trail image: " + trailImageUrl);
					if (imageDAO.isAvailableLocally(trailImageUrl)) {
						Message m = new Message();
						m.what = 3;						
					    Bundle bundle = new Bundle();
                        bundle.putString("url", trailImageUrl); 
						m.setData(bundle);					
						Log.d(TAG, "Sending message; trailimage url is available locally: " + trailImageUrl);
						updateArticlesHandler.sendMessage(m);
					} else {
						downloadTrailImages.add(trailImageUrl);
					}
				}		
			}
			
			if (running) {			
				for (String trailImageUrl : downloadTrailImages) {
					Log.d(TAG, "Downloading trail image: " + downloadTrailImages);
					imageDAO.fetchLiveImage(trailImageUrl);
					Message m = new Message();
					m.what = 3;						
					Bundle bundle = new Bundle();
					bundle.putString("url", trailImageUrl); 
					m.setData(bundle);					
					Log.d(TAG, "Sending message; trailimage url is available locally: " + trailImageUrl);
					updateArticlesHandler.sendMessage(m);
				}		
			}
						
			return;				
		}

		public void stop() {
			this.running = false;
		}
	}
	
		
}
