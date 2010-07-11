package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class article extends MenuedActivity {
	
	//private static final String TAG = "article";
	
	ListAdapter adapter;
	private NetworkStatusService networkStatusService;
    private ImageDAO imageDAO;
    private Article article;
       
	private MainImageUpdateHandler mainImageUpdateHandler;
    private MainImageLoader mainImageLoader;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		imageDAO = ArticleDAOFactory.getImageDao(this.getApplicationContext());			
		networkStatusService = new NetworkStatusService(this.getApplicationContext());
		
    	mainImageUpdateHandler = new MainImageUpdateHandler();
    	
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.article);
		
		Article article = (Article) this.getIntent().getExtras().get("article");
		this.article = article;
		
		if (article != null) {
			populateArticle(article);
		} else {
        	Toast.makeText(this, "Could not load article", Toast.LENGTH_SHORT).show();
		}	
	}
	
	
	private void populateArticle(Article article) {		
		if (article.getSection() != null) {
			setHeading(article.getSection().getName());
			setHeadingColour(article.getSection().getColour());
		}
		
        TextView headline = (TextView) findViewById(R.id.Headline);
        TextView pubDate = (TextView) findViewById(R.id.PubDate);
        TextView byline = (TextView) findViewById(R.id.Byline);
        TextView standfirst = (TextView) findViewById(R.id.Standfirst);
        TextView description = (TextView) findViewById(R.id.Description);
        
        headline.setText(article.getTitle());
        if (article.getPubDate() != null) {
        	pubDate.setText(article.getPubDateString());
        }
        byline.setText(article.getByline());
        standfirst.setText(article.getStandfirst());
        description.setText(article.getDescription());
        
    	
    	final boolean connectionAvailable = networkStatusService.isConnectionAvailable();
    	
    	final String mainImageUrl = article.getMainImageUrl();
		if (mainImageUrl != null) {			
			TextView caption = (TextView) findViewById(R.id.Caption);
			caption.setText(article.getCaption());
			
			final boolean isWifiConnectionAvailable = networkStatusService.isConnectionAvailable() && networkStatusService.isWifiConnection();			
			mainImageLoader = new MainImageLoader(imageDAO, article.getMainImageUrl(), isWifiConnectionAvailable);
			Thread loader = new Thread(mainImageLoader);
			loader.start();			
		}
		
		LayoutInflater inflater = LayoutInflater.from(this);
		TagListPopulatingService.populateTags(inflater, connectionAvailable, (LinearLayout) findViewById(R.id.AuthorList), article.getAuthors(), this.getApplicationContext());
		TagListPopulatingService.populateTags(inflater, connectionAvailable, (LinearLayout) findViewById(R.id.TagList), article.getKeywords(), this.getApplicationContext());
	}

	
	private void populateMainImage(String mainImageUrl) {
		if (article != null && mainImageUrl.equals(article.getMainImageUrl())) {
			ImageView imageView = (ImageView) findViewById(R.id.ArticleImage);
			TextView caption = (TextView) findViewById(R.id.Caption);

			Bitmap bitmap = imageDAO.getImage(mainImageUrl);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);			
				imageView.setVisibility(View.VISIBLE);
				caption.setVisibility(View.VISIBLE);
			}
		}
	}
	
		
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home");
		menu.add(0, 2, 0, "Favourites");
	    menu.add(0, 3, 0, "Sections");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case 1:
	    	switchToMain();
	    	return true;
	    case 2: 	    	
	    	switchToFavourites();
	    	return true;	 
	    case 3:
	    	switchToSections();
	    	return true;
	    }
	    return false;
	}
	
	
	
	
	
	class MainImageLoader implements Runnable {		

		private ImageDAO imageDAO;
		private String mainImageUrl;
		private boolean isWifiConnectionAvailable;
			
		public MainImageLoader(ImageDAO imageDAO, String mainImageUrl, boolean isWifiConnectionAvailable) {
			this.imageDAO = imageDAO;
			this.mainImageUrl = mainImageUrl;
			this.isWifiConnectionAvailable = isWifiConnectionAvailable;
		}

		@Override
		public void run() {
			if (imageDAO.isAvailableLocally(mainImageUrl)) {
				sendMainImageAvailableMessage();
				
			} else if (isWifiConnectionAvailable) {
				//Log.i(TAG, "Main image is not available locally, but will fetch because wifi is available");				
				imageDAO.fetchLiveImage(mainImageUrl);
				sendMainImageAvailableMessage();
			}
			return;
		}

		private void sendMainImageAvailableMessage() {
			//Log.i(TAG, "Sending main image available message: " + mainImageUrl);
			Message msg = new Message();
			msg.what = 1;
			msg.getData().putString("mainImageUrl", mainImageUrl);
			mainImageUpdateHandler.sendMessage(msg);
		}
		
	}
		
	
	class MainImageUpdateHandler extends Handler {	
		
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {	   
			    case 1:
			    final String mainImageUrl = msg.getData().getString("mainImageUrl");
			    populateMainImage(mainImageUrl);
			}
		}
		
	}
			
}
