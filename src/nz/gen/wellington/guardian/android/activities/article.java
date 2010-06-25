package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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

public class article extends Activity {
	
	ListAdapter adapter;
	private NetworkStatusService networkStatusService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.networkStatusService = new NetworkStatusService(this.getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.article);
		
		Article article = (Article) this.getIntent().getExtras().get("article");		
		if (article != null) {
			populateArticle(article);
		} else {
        	Toast.makeText(this, "Could not load article", Toast.LENGTH_SHORT).show();
		}
	}
	

	// TODO duplication
	protected void setHeading(String headingText) {
		TextView heading = (TextView) findViewById(R.id.Heading);
		heading.setText(headingText);		
	}
	
	
	// TODO duplication
	protected void setHeadingColour(String colour) {
		LinearLayout heading = (LinearLayout) findViewById(R.id.HeadingLayout);
		heading.setBackgroundColor(Color.parseColor(colour));
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
        
        ImageDAO imageDAO = ArticleDAOFactory.getImageDao(this);
    	ImageView imageView = (ImageView) findViewById(R.id.ArticleImage);
    	
    	final String mainImageUrl = article.getMainImageUrl();
		if (mainImageUrl != null && imageDAO.isAvailableLocally(mainImageUrl)) {
    		populateMainImage(article, imageDAO, imageView, mainImageUrl);
    	}
        		
		final boolean connectionAvailable = networkStatusService.isConnectionAvailable();
		
		LayoutInflater inflater = LayoutInflater.from(this);
		TagListPopulatingService.populateTags(inflater, connectionAvailable, (LinearLayout) findViewById(R.id.AuthorList), article.getAuthors(), this.getApplicationContext());
		TagListPopulatingService.populateTags(inflater, connectionAvailable, (LinearLayout) findViewById(R.id.TagList), article.getKeywords(), this.getApplicationContext());
	}

	
	private void populateMainImage(Article article, ImageDAO imageDAO, ImageView imageView, final String mainImageUrl) {
		Bitmap bitmap = imageDAO.getImage(mainImageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			TextView caption = (TextView) findViewById(R.id.Caption);
			caption.setText(article.getCaption());
			
			imageView.setVisibility(View.VISIBLE);
			caption.setVisibility(View.VISIBLE);
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
	
	
	private void switchToMain() {
		Intent intent = new Intent(this, main.class);
		this.startActivity(intent);	
	}
	
	private void switchToFavourites() {
		Intent intent = new Intent(this, favourites.class);
		this.startActivity(intent);		
	}
	
	private void switchToSections() {
		Intent intent = new Intent(this, sections.class);
		this.startActivity(intent);		
	}
		
}
