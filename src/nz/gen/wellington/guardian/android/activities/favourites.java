package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class favourites extends ArticleListActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);        
        setHeading("Favourites");
        setHeadingColour("#0061A6");
    	showSeperators = true;
    	showMainImage = false;
		updateArticlesHandler = new UpdateArticlesHandler(this, getArticleSet());
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		
		LinearLayout favouritesPane = (LinearLayout) findViewById(R.id.FavouritesPane);
		favouritesPane.removeAllViews();
		populateFavourites();		
	}


	private void populateFavourites() {
		FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO = ArticleDAOFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());		

		TextView description = (TextView) findViewById(R.id.Description);
		
		// TODO - this implies two sqllite queries in a row - needs to be done in one open open and close if possible.
		List<Section> favouriteSections = favouriteSectionsAndTagsDAO.getFavouriteSections();
		List<Tag> favouriteTags = favouriteSectionsAndTagsDAO.getFavouriteTags();
		
		boolean favouritesLoadedCorrectly = (favouriteSections != null && favouriteTags != null);
		if (!favouritesLoadedCorrectly) {
			description.setText("There was a problem loading your favorite sections and tags.");			
			return;
		}
		
		boolean hasFavourites = !favouriteSections.isEmpty() || !favouriteTags.isEmpty();
		if (hasFavourites) {		
			LayoutInflater inflater = LayoutInflater.from(this);
			LinearLayout authorList = (LinearLayout) findViewById(R.id.FavouritesPane);
		
			boolean connectionIsAvailable = new NetworkStatusService(this.getApplicationContext()).isConnectionAvailable();
			TagListPopulatingService.populateSections(inflater, connectionIsAvailable, authorList, favouriteSections, this.getApplicationContext());
			TagListPopulatingService.populateTags(inflater, connectionIsAvailable, authorList, favouriteTags, this.getApplicationContext());
		
			description.setText("The following sections and tags have been marked as favourites.");			
			
		} else {
			description.setText("No favourite sections of tags have been set.\n\nAdd favourites to populate the articles on this screen and to " +
					"indicate which articles should be downloaded for offline viewing.");			
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home");
		menu.add(0, 5, 0, "Refresh");
		menu.add(0, 2, 0, "Sections");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			switchToMain();
			return true;
		case 2:
			switchToSections();
			return true;		
		case 5:
			refresh();
			return true;
		}
		
		return false;
	}

	
	@Override
	protected ArticleSet getArticleSet() {	
		FavouriteSectionsAndTagsDAO favouriteSectionAndTagsDAO = ArticleDAOFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());		
		List<Section> favouriteSections = favouriteSectionAndTagsDAO.getFavouriteSections();
		List<Tag> favouriteTags = favouriteSectionAndTagsDAO.getFavouriteTags();
		
		if (!favouriteSections.isEmpty() || !favouriteTags.isEmpty()) {
			return new FavouriteStoriesArticleSet(favouriteSections, favouriteTags);			
		}
		return null;	// TODO this needs to be null safed upstream
	}


	@Override
	protected String getRefinementDescription(String refinementType) {
		return null;
	}
	
}
