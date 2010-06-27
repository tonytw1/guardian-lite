package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class favourites extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);
	}

	
	@Override
	protected void onStart() {
		super.onStart();		
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);
		mainPane.removeAllViews();
		populateFavourites();		
	}


	private void populateFavourites() {
		List<Section> favouriteSections = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext()), this.getApplicationContext()).getFavouriteSections();
		List<Tag> favouriteTags = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext()), this.getApplicationContext()).getFavouriteTags();
		
		LayoutInflater inflater = LayoutInflater.from(this);		
		LinearLayout authorList = (LinearLayout) findViewById(R.id.MainPane);
		
		boolean connectionIsAvailable = new NetworkStatusService(this.getApplicationContext()).isConnectionAvailable();
		TagListPopulatingService.populateSections(inflater, connectionIsAvailable, authorList, favouriteSections, this.getApplicationContext());
		TagListPopulatingService.populateTags(inflater, connectionIsAvailable, authorList, favouriteTags, this.getApplicationContext());
	}
	
}
