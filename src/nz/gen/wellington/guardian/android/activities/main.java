package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;

import org.joda.time.DateTime;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class main extends ArticleListActivity {
	
	private DateTime loaded;
	
	public main() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        hindHeading();
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
		List<Article> topStories = articleDAO.getTopStories();
		this.loaded = new DateTime();
		return topStories;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Favourites");
	    menu.add(0, 2, 0, "Sections");
	    menu.add(0, 3, 0, "Offline");
	    menu.add(0, 4, 0, "Settings");
	    menu.add(0, 5, 0, "About");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	    case 1: 	    	
	    	switchToFavourites();
	    	return true;	 
	    case 2:
	    	switchToSections();
	    	return true;	 
	    case 3: 	    	
	    	swichToSync();
	        return true;
	    case 4:
	    	switchToPreferences();
	    	return true;
	    case 5:
	    	showAbout();
	    }
	    return false;
	}


	private void showAbout() {
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.about_dialog);
		ImageView heading = (ImageView) dialog.findViewById(R.id.KingsPlace);
		heading.setImageResource(R.drawable.kingsplace);
		
		ImageView image = (ImageView) dialog.findViewById(R.id.GuardianLogo);
		image.setImageResource(R.drawable.poweredbyguardian);		
		dialog.setTitle(R.string.app_name);
		dialog.show();
	}

	private void swichToSync() {
		Intent intent = new Intent(this, sync.class);
		this.startActivity(intent);	
	}
	
	private void switchToSections() {
		Intent intent = new Intent(this, sections.class);
		this.startActivity(intent);		
	}
	
	private void switchToFavourites() {
		Intent intent = new Intent(this, favourites.class);
		this.startActivity(intent);		
	}
	
	private void switchToPreferences() {
		Intent intent = new Intent(this, perferences.class);
		this.startActivity(intent);	
	}
	
}