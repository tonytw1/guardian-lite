package nz.gen.wellington.guardian.android.activities;

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
        hideHeading();
        showSeperators = true;
    	showMainImage = false;
	}
	
	
	@Override
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getTopStoriesArticleSet();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.FAVOURITES, 0, "Favourites");
	    menu.add(0, MenuedActivity.SECTIONS, 0, "Sections");
	    menu.add(0, MenuedActivity.ABOUT, 0, "About");
	    MenuItem refreshMenuOption = menu.add(0, MenuedActivity.REFRESH, 0, "Refresh");
	    menu.add(0, MenuedActivity.SYNC, 0, "Sync");
	    menu.add(0, MenuedActivity.SETTING, 0, "Settings");
	    
	    enableMenuItemIfConnectionIsAvailable(refreshMenuOption);
	    return true;
	}
		
}