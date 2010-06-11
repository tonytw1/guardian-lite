package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ListKeywordClicker;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Tag;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class article extends Activity {
	
	ListAdapter adapter;
	
	public article() {
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.article);
		
		Article article = (Article) this.getIntent().getExtras().get("article");		
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
        TextView caption = (TextView) findViewById(R.id.Caption);
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
    	
    	caption.setText(article.getCaption());
    	final String mainImageUrl = article.getMainImageUrl();
    	Log.d("article", "main picture url is: " + mainImageUrl);
		if (mainImageUrl != null && imageDAO.isAvailableLocally(mainImageUrl)) {
    		Bitmap bitmap = imageDAO.getImage(mainImageUrl);
    		if (bitmap != null) {
    			imageView.setImageBitmap(bitmap);		
    		}
    	}
        
		LayoutInflater inflater = LayoutInflater.from(this);		
		LinearLayout authorList = (LinearLayout) findViewById(R.id.AuthorList);
		for (Tag tag : article.getAuthors()) {
			View vi = inflater.inflate(R.layout.authorslist, null);			  
			TextView titleText = (TextView) vi.findViewById(R.id.TagName);
	    	titleText.setText(tag.getName());
	    	ListKeywordClicker urlListener = new ListKeywordClicker(tag);
	    	vi.setOnClickListener(urlListener);
	    	authorList.addView(vi);
		}
		
		LinearLayout tagList = (LinearLayout) findViewById(R.id.TagList);
		for (Tag tag : article.getKeywords()) {
			View vi = inflater.inflate(R.layout.authorslist, null);			  
			TextView titleText = (TextView) vi.findViewById(R.id.TagName);
	    	titleText.setText(tag.getName());
	    	ListKeywordClicker urlListener = new ListKeywordClicker(tag);
	    	vi.setOnClickListener(urlListener);
	    	tagList.addView(vi);
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
	
}
