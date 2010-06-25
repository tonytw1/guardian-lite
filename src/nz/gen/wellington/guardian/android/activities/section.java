package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.sqllite.DataHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class section extends ArticleListActivity {
	
	private static final String TAG = "section";	
	private Section section;
	private MenuItem favouriteMenuItem;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        section = (Section) this.getIntent().getExtras().get("section");
    	setHeading(section.getName());
    	setHeadingColour(section.getColour());
    	updateArticlesHandler = new UpdateArticlesHandler(this);
	}


	@Override
	protected List<Article> loadArticles() {
		return articleDAO.getSectionItems(section);
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		DataHelper dh = new DataHelper(this);
		if (dh.isFavourite(section)) {
			favouriteMenuItem = menu.add(0, 1, 0, "Remove from Favourites");
		} else {
			favouriteMenuItem = menu.add(0, 1, 0, "Add to Favourites");
		}
		dh.close();
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	    case 1: 	    	
	    	addToFavourites();
	    	return true;	 
	    }
	    return false;
	}
	
	
	private void addToFavourites() {
		DataHelper dh = new DataHelper(this);
		if (!dh.isFavourite(section)) {
			Log.i(TAG, "Adding current tag to favourites: " + section.getName());
			dh.addSection(section);
			favouriteMenuItem.setTitle("Remove from Favourites");			
	
		} else {
			Log.i(TAG, "Removing current section from favourites: " + section.getName());			
			dh.removeSection(section);
			favouriteMenuItem.setTitle("Add to Favourites");
		}
		dh.close();	
	}
		
}