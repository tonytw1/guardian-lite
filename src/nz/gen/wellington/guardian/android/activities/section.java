package nz.gen.wellington.guardian.android.activities;

import java.util.Arrays;
import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class section extends ArticleListActivity implements FontResizingActivity {
	
	private static final String TAG = "section";	
	private Section section;
	private MenuItem favouriteMenuItem;
	private FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
    private ArticleSetFactory articleSetFactory;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());
        this.articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());        
        section = (Section) this.getIntent().getExtras().get("section");
    	setHeading(section.getName());
    	setHeadingColour(section.getColour());	
	}
	
	
	@Override
	public void setFontSize(int baseSize) {
		super.setFontSize(baseSize);		
		View view =  findViewById(R.id.Main);
		view.setBackgroundColor(ColourScheme.BACKGROUND);
	}


	protected ArticleSet getArticleSet() {
		return articleSetFactory.getArticleSetForSection(section);
	}
	
	protected List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
		
	@Override
	protected String getRefinementDescription(String refinementType) {
		if (refinementType.equals("blog")) {
			return "These blog tags have been used recently in the " + getArticleSet().getName() + " section:";
		} else if (refinementType.equals("contributor")) { 
			return "These contributors have appeared recently in the " + getArticleSet().getName() + " section:";
		}	
		return "These keywords have been used recently within the " + getArticleSet().getName() + " section:";
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home");
		menu.add(0, 5, 0, "Refresh");
		
		if (favouriteSectionsAndTagsDAO.isFavourite(section)) {
			favouriteMenuItem = menu.add(0, 4, 0, "Remove Favourite");
		} else {
			favouriteMenuItem = menu.add(0, 4, 0, "Add to Favourites");
		}		
	    return true;
	}
	
	@Override
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
		case 4:
			addToFavourites();
			return true;
		case 5:
			refresh();
			return true;
		}
		return false;
	}
	
	
	private void addToFavourites() {		
		if (!favouriteSectionsAndTagsDAO.isFavourite(section)) {
			Log.i(TAG, "Adding current section to favourites: " + section.getName());			
			if (favouriteSectionsAndTagsDAO.addSection(section)) {
				favouriteMenuItem.setTitle("Remove Favourite");
			} else {
	        	Toast.makeText(this, "Favourites list is full", Toast.LENGTH_LONG).show();
			}
	
		} else {
			Log.i(TAG, "Removing current section from favourites: " + section.getName());			
			favouriteSectionsAndTagsDAO.removeSection(section);
			favouriteMenuItem.setTitle("Add to Favourites");
		}		
	}
	
}