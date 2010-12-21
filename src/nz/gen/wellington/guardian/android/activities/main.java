package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class main extends ArticleListActivity {
		
	private ArticleSetFactory articleSetFactory;


	public main() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
        setContentView(R.layout.main);        
        hideHeading();
        showSeperators = true;
    	showMainImage = false;
	}
	
	
	@Override
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getTopStoriesArticleSet();
	}
	
	
	@Override
	protected String getRefinementDescription(String refinementType) {
		return null;
	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Favourites");
	    menu.add(0, 2, 0, "Sections");
	    menu.add(0, 6, 0, "Saved articles");
	    menu.add(0, 5, 0, "Refresh");
	    menu.add(0, 3, 0, "Sync");
	    menu.add(0, 4, 0, "Settings");
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
			refresh();
			return true;	
	    case 6:
	    	switchToSavedArticles();
	    	return true;
	    }
	    return false;
	}
	
}