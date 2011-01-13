package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class favourites extends ArticleListActivity {
	
	private PreferencesDAO preferencesDAO;
    private ArticleSetFactory articleSetFactory;
    private NetworkStatusService networkStatusService;
	private TagListPopulatingService tagListPopulatingService;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        preferencesDAO = SingletonFactory.getPreferencesDAO(this.getApplicationContext());
        articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
        networkStatusService = SingletonFactory.getNetworkStatusService(this.getApplicationContext());
        tagListPopulatingService = SingletonFactory.getTagListPopulator(this.getApplicationContext());
        
        setContentView(R.layout.favourites);        
        setHeading("Favourites");
        setHeadingColour("#0061A6");
    	showSeperators = true;
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		final int baseSize = preferencesDAO.getBaseFontSize();
		setFontSize(baseSize);
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		
		LinearLayout favouritesPane = (LinearLayout) findViewById(R.id.FavouritesPane);
		favouritesPane.removeAllViews();
		populateFavourites();		
	}


	private void populateFavourites() {
		FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());		

		TextView description = (TextView) findViewById(R.id.Description);
		
		// TODO - this implies three sqllite queries in a row - needs to be done in one open open and close if possible.
		List<Section> favouriteSections = favouriteSectionsAndTagsDAO.getFavouriteSections();
		List<Tag> favouriteTags = favouriteSectionsAndTagsDAO.getFavouriteTags();
		List<String> favouriteSearchTerms = favouriteSectionsAndTagsDAO.getFavouriteSearchTerms();
		
		boolean favouritesLoadedCorrectly = (favouriteSections != null && favouriteTags != null && favouriteSearchTerms != null);
		if (!favouritesLoadedCorrectly) {
			description.setText("There was a problem loading your favorite sections and tags.");			
			return;
		}
		
		boolean hasFavourites = !favouriteSections.isEmpty() || !favouriteTags.isEmpty() || !favouriteSearchTerms.isEmpty();
		if (hasFavourites) {
			LayoutInflater inflater = LayoutInflater.from(this);
			LinearLayout authorList = (LinearLayout) findViewById(R.id.FavouritesPane);
		
			// TODO move to a layout
			LinearLayout tagGroup = new LinearLayout(this.getApplicationContext());
			tagGroup.setOrientation(LinearLayout.VERTICAL);
			tagGroup.setPadding(2, 0, 2, 0);
			
			final boolean connectionIsAvailable = networkStatusService.isConnectionAvailable();
			
			tagListPopulatingService.populateTags(inflater, connectionIsAvailable, tagGroup, articleSetFactory.getArticleSetsForSections(favouriteSections));
			tagListPopulatingService.populateTags(inflater, connectionIsAvailable, tagGroup, articleSetFactory.getArticleSetsForTags(favouriteTags));
			tagListPopulatingService.populateTags(inflater, connectionIsAvailable, tagGroup, articleSetFactory.getArticleSetsForSearchTerms(favouriteSearchTerms));

			authorList.addView(tagGroup);
			
			description.setText("The following sections and tags have been marked as favourites.");			
			
		} else {
			description.setText("No favourite sections of tags have been set.\n\nAdd favourites to populate the articles on this screen and to " +
					"indicate which articles should be downloaded for offline viewing.");			
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.HOME, 0, "Home");
		menu.add(0, MenuedActivity.SECTIONS, 0, "Sections");
		menu.add(0, MenuedActivity.SAVED, 0, "Saved articles");
		MenuItem refreshOption = menu.add(0, MenuedActivity.REFRESH, 0, "Refresh");
		enableMenuItemIfConnectionIsAvailable(refreshOption);
	    return true;
	}
	
	@Override
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getFavouritesArticleSet();
	}
	
	@Override
	public void setFontSize(int baseSize) {
		super.setFontSize(baseSize);
		TextView description = (TextView) findViewById(R.id.Description);
        description.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseSize);
        description.setTextColor(ColourScheme.BODYTEXT);
	}
	
}
