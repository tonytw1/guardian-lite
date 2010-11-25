package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class tag extends ArticleListActivity {

	private static final String TAG = "tag";

	private Tag tag;
	private MenuItem favouriteMenuItem;
	private FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		this.favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());
		tag = (Tag) this.getIntent().getExtras().get("keyword");		
		if (tag.getSection() != null) {
			setHeading(tag.getSection().getName() + " - " + tag.getName());
			setHeadingColour(tag.getSection().getColour());
		} else {
			setHeading(tag.getName());
		}
	}
	
	protected String getRefinementDescription(String refinementType) {
		if (refinementType.equals("keyword")) {
			return "Articles tagged with " + getArticleSet().getName() + " have also been tagged with:";
		}
		return null;
	}
	
	protected ArticleSet getArticleSet() {
		return ArticleSetFactory.getArticleSetForTag(tag);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home");	
		menu.add(0, 5, 0, "Refresh");
		
		if (favouriteSectionsAndTagsDAO.isFavourite(tag)) {
			favouriteMenuItem = menu.add(0, 4, 0, "Remove Favourite");
		} else {
			favouriteMenuItem = menu.add(0, 4, 0, "Add to Favourites");
		}
		
	    return true;
	}
	
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			switchToMain();
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
		
		if (!favouriteSectionsAndTagsDAO.isFavourite(tag)) {
			Log.i(TAG, "Adding current tag to favourites: " + tag.getName());
			if (favouriteSectionsAndTagsDAO.addTag(tag)) {
				favouriteMenuItem.setTitle("Remove Favourite");			
			} else {
	        	Toast.makeText(this, "Favourites list is full", Toast.LENGTH_LONG).show();
			}
		
		} else {
			Log.i(TAG, "Removing current tag from favourites: " + tag.getName());			
			favouriteSectionsAndTagsDAO.removeTag(tag);
			favouriteMenuItem.setTitle("Add to Favourites");
		}		
	}
	
}
