package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.CacheFactory;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;

import org.joda.time.DateTime;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class main extends ArticleListActivity {
		
	private static final String TAG = "main";
	
	private DateTime loaded;
	
	public main() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        setHeading("Most recent");
    	setHeadingColour("#0061A6");
    	updateArticlesHandler = new UpdateArticlesHandler(this);
    	showSeperators = true;
    	showMainImage = false;
	}
	
	
	@Override
	protected boolean shouldRefreshView(LinearLayout mainPane) {
		DateTime modtime = ArticleDAOFactory.getDao(this.getApplicationContext()).getModificationTime(new TopStoriesArticleSet());
		boolean topStoriesFileHasChanged = modtime != null && modtime.isAfter(loaded);
		return super.shouldRefreshView(mainPane) || topStoriesFileHasChanged;
	}

	
	@Override
	protected List<Article> loadArticles() {
		List<Article> topStories = ArticleDAOFactory.getDao(this.getApplicationContext()).getTopStories();
		this.loaded = new DateTime();
		return topStories;
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