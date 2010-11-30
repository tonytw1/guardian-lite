package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.api.filtering.SectionSorter;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

public class sections extends DownloadProgressAwareActivity {
		
	private SectionDAO sectionDAO;
	private NetworkStatusService networkStatusService;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sectionDAO = SingletonFactory.getSectionDAO(this.getApplicationContext());
		networkStatusService = new NetworkStatusService(this.getApplicationContext());
		
		setContentView(R.layout.sections);		
		setHeading("Sections");
		setHeadingColour("#0061A6");
	}
	
		
	@Override
	public void onResume() {
		super.onResume();
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);
		mainPane.removeAllViews();
		populateSections();        
	}

			
	private void populateSections() {
		List<Section> sections = sectionDAO.getSections();		
		if (sections != null) {
			sections = SectionSorter.sortByName(sections);	// TODO push this back behind the section dao for performance		
			LayoutInflater inflater = LayoutInflater.from(this);		
			LinearLayout authorList = (LinearLayout) findViewById(R.id.MainPane);			
			TagListPopulatingService.populateTags(inflater, networkStatusService.isConnectionAvailable(), authorList, ArticleSetFactory.getArticleSetsForSections(sections), this.getApplicationContext());
			
		} else {
        	Toast.makeText(this, "Could not load sections", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home");
		menu.add(0, 2, 0, "Favourites");
		menu.add(0, 3, 0, "Search tags");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			switchToMain();
			return true;
		case 2:
			switchToFavourites();
			return true;
		case 3:
			switchToTagSearch();
			return true;
		}
		return false;
	}
			
}