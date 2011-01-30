package nz.gen.wellington.guardian.android.activities;

import java.util.HashMap;
import java.util.Map;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.ImageDownloadDecisionService;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import nz.gen.wellington.guardian.android.utils.ShareTextComposingService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class ContentRenderingActivity extends MenuedActivity implements FontResizingActivity {
		
	private static final String REMOVE_SAVED_ARTICLE = "Remove saved article";
	private static final String SAVE_ARTICLE = "Save article";
	
	protected NetworkStatusService networkStatusService;
	protected ImageDAO imageDAO;
	protected ArticleSetFactory articleSetFactory;
	protected FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
    protected Article article;
       

    protected Map<String, Bitmap> images;
    protected MenuItem saveArticleMenuItem;
    private String shareText;
    protected TagListPopulatingService tagListPopulatingService;
    protected ImageDownloadDecisionService imageDownloadDecisionService;
    protected GridView thumbnails;
        
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		imageDAO = SingletonFactory.getImageDao(this.getApplicationContext());
		articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		networkStatusService = SingletonFactory.getNetworkStatusService(this.getApplicationContext());
		favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());
		tagListPopulatingService = SingletonFactory.getTagListPopulator(this.getApplicationContext());
		imageDownloadDecisionService = SingletonFactory.getImageDownloadDecisionService(this.getApplicationContext());
		
		images = new HashMap<String, Bitmap>();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.article);
		
		this.article = (Article) this.getIntent().getExtras().get("article");		
		if (article != null) {
			populateContent(article, colourScheme.getBodytext(), colourScheme.getHeadline());			
		} else {
        	Toast.makeText(this, "Could not load article", Toast.LENGTH_SHORT).show();
		}		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		setFontSize();	
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		images.clear();
	}
	
	
	@Override
	public void setFontSize() {
		super.setFontSize();
		
		TextView headline = (TextView) findViewById(R.id.Headline);
		TextView pubDate = (TextView) findViewById(R.id.PubDate);
		TextView byline = (TextView) findViewById(R.id.Byline);
		TextView standfirst = (TextView) findViewById(R.id.Standfirst);
		
		headline.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize + 1);
		pubDate.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize - 2);
		byline.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
        standfirst.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
        		
		TextView tagLabel =  (TextView) findViewById(R.id.TagLabel);
		if (tagLabel != null) {
			tagLabel.setTextColor(colourScheme.getBodytext());
		}
	}
	
	protected abstract void populateContent(Article article, int bodytextColour, int headlineColour);
	
	protected void populateTags(Article article, final boolean connectionAvailable) {
		LayoutInflater inflater = LayoutInflater.from(this);
		findViewById(R.id.TagLabel).setVisibility(View.VISIBLE);
		
		tagListPopulatingService.populateTags(inflater, connectionAvailable, (LinearLayout) findViewById(R.id.AuthorList), articleSetFactory.getArticleSetsForTags(article.getAuthors()), colourScheme);
		tagListPopulatingService.populateTags(inflater, connectionAvailable, (LinearLayout) findViewById(R.id.TagList), articleSetFactory.getArticleSetsForTags(article.getKeywords()), colourScheme);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.HOME, 0, "Home");
		
		if (article != null && article.getId() != null) {
			if (favouriteSectionsAndTagsDAO.isSavedArticle(article)) {
				saveArticleMenuItem = menu.add(0, MenuedActivity.SAVE_REMOVE_ARTICLE, 0, REMOVE_SAVED_ARTICLE);				
			} else {
				saveArticleMenuItem = menu.add(0, MenuedActivity.SAVE_REMOVE_ARTICLE, 0, SAVE_ARTICLE);
			}
		}
		
	    MenuItem showInBrowserMenuOption = menu.add(0, MenuedActivity.BROWSER, 0, "Open in browser");
		if (article != null && article.getWebUrl() != null) {
			showInBrowserMenuOption.setEnabled(true);
		} else {
			showInBrowserMenuOption.setEnabled(false);
		}
	    
	    MenuItem shareMenuOption = menu.add(0, MenuedActivity.SHARE, 0, "Share");
		shareText = ShareTextComposingService.composeShareText(article);
		if (article != null && shareText != null) {
			shareMenuOption.setEnabled(true);
		} else {
			shareMenuOption.setEnabled(false);
		}
	    return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!super.onOptionsItemSelected(item)) {
			switch (item.getItemId()) {
			case MenuedActivity.SAVE_REMOVE_ARTICLE:
				processSavedArticle(article);
				return true;
			case MenuedActivity.BROWSER:
				showArticleInBrowser(article);
				return true;
			case MenuedActivity.SHARE:
				shareArticle(article);
				return true;
			}
		}
		return false;
	}
	
	
	private void processSavedArticle(Article article) {
		if (!favouriteSectionsAndTagsDAO.isSavedArticle(article)) {
			if (favouriteSectionsAndTagsDAO.addSavedArticle(article)) {
				saveArticleMenuItem.setTitle(REMOVE_SAVED_ARTICLE);
			} else {
				Toast.makeText(this, "Saved articles list is full", Toast.LENGTH_LONG).show();
			}			
		} else {
			if (favouriteSectionsAndTagsDAO.removeSavedArticle(article)) {
				saveArticleMenuItem.setTitle(SAVE_ARTICLE);
			} else {
				Toast.makeText(this, "Saved articles list is full", Toast.LENGTH_LONG).show();
			}
		}
	}

	
	private void shareArticle(Article article) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "guardian.co.uk article");
		shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
		startActivity(Intent.createChooser(shareIntent, "Share"));
	}
	
	
	private void showArticleInBrowser(Article article) {
		Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(article.getWebUrl()));
		startActivity(browserIntent);		
	}
	
}
