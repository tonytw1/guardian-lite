package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class section extends ArticleListActivity implements FontResizingActivity {
	
	private static final String TAG = "section";
	private SectionArticleSet articleSet;
	private MenuItem favouriteMenuItem;
	private FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());
        this.articleSet = (SectionArticleSet) this.getIntent().getExtras().get("articleset");
    	setHeading(articleSet.getName());
    	if (articleSet.getSection() != null) {
    		setHeadingColour(articleSet.getSection().getColour());    		
    	}
	}
	
	protected ArticleSet getArticleSet() {
		return articleSet;
	}
	
	@Override
	protected String getRefinementDescription(String refinementType) {
		if (refinementType.equals("blog")) {
			return "These blog tags have been used recently in the " + getArticleSet().getName() + " section:";
		} else if (refinementType.equals("contributor")) { 
			return "These contributors have appeared recently in the " + getArticleSet().getName() + " section:";
		} else if (refinementType.equals("date")) {
			return "Articles have been tagged with " + getArticleSet().getName() + " on these dates:";
		}	
		return "These keywords have been used recently within the " + getArticleSet().getName() + " section:";
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.HOME, 0, "Home");
		MenuItem refreshOption = menu.add(0, MenuedActivity.REFRESH, 0, "Refresh");
		enableMenuItemIfConnectionIsAvailable(refreshOption);
		
		if (favouriteSectionsAndTagsDAO.isFavourite(articleSet.getSection())) {
			favouriteMenuItem = menu.add(0, MenuedActivity.ADD_REMOVE_FAVOURITE, 0, "Remove Favourite");
		} else {
			favouriteMenuItem = menu.add(0, MenuedActivity.ADD_REMOVE_FAVOURITE, 0, "Add to Favourites");
		}		
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!super.onOptionsItemSelected(item)) {
			switch (item.getItemId()) {
			case MenuedActivity.ADD_REMOVE_FAVOURITE:
				addToFavourites();
				return true;			
			}
		}
		return false;
	}
	
	private void addToFavourites() {		
		if (!favouriteSectionsAndTagsDAO.isFavourite(articleSet.getSection())) {
			Log.i(TAG, "Adding current section to favourites: " + articleSet.getSection().getName());			
			if (favouriteSectionsAndTagsDAO.addSection( articleSet.getSection())) {
				favouriteMenuItem.setTitle("Remove Favourite");
			} else {
	        	Toast.makeText(this, "Favourites list is full", Toast.LENGTH_LONG).show();
			}
	
		} else {
			Log.i(TAG, "Removing current section from favourites: " +  articleSet.getSection().getName());			
			favouriteSectionsAndTagsDAO.removeSection( articleSet.getSection());
			favouriteMenuItem.setTitle("Add to Favourites");
		}		
	}
	
}