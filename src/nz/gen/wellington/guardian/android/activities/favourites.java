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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class favourites extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.favourites);
        
        setHeading("Favourites");
        setHeadingColour("#0061A6");
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
		
		boolean hasFavourites= !favouriteSections.isEmpty() || !favouriteTags.isEmpty();
		TextView description = (TextView) findViewById(R.id.Description);
		if (hasFavourites) {
			LayoutInflater inflater = LayoutInflater.from(this);
			LinearLayout authorList = (LinearLayout) findViewById(R.id.MainPane);
		
			boolean connectionIsAvailable = new NetworkStatusService(this.getApplicationContext()).isConnectionAvailable();
			TagListPopulatingService.populateSections(inflater, connectionIsAvailable, authorList, favouriteSections, this.getApplicationContext());
			TagListPopulatingService.populateTags(inflater, connectionIsAvailable, authorList, favouriteTags, this.getApplicationContext());
		
			description.setText("The following sections and tags have been marked as favourites.");			
			
		} else {
			description.setText("No favourite sections of tags have been set.\n\nAdd favourites to influence the contents of the " +
					"latest articles screen and to control which articles are downloaded for offline viewing.");			
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home");
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
		}
		return false;
	}
	
	
	private void switchToMain() {
		Intent intent = new Intent(this, main.class);
		this.startActivity(intent);	
	}
	
	private void switchToSections() {
		Intent intent = new Intent(this, sections.class);
		this.startActivity(intent);		
	}
	
	
	// TODO duplication
	protected void setHeading(String headingText) {
		TextView heading = (TextView) findViewById(R.id.Heading);
		heading.setText(headingText);		
	}
	// TODO duplication
	protected void setHeadingColour(String colour) {
		LinearLayout heading = (LinearLayout) findViewById(R.id.HeadingLayout);
		heading.setBackgroundColor(Color.parseColor(colour));
	}
	
}
