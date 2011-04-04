/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.api.filtering.SectionSorter;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.model.Section;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

public class sections extends DownloadProgressAwareActivity implements FontResizingActivity {
		
	private SectionDAO sectionDAO;
	private NetworkStatusService networkStatusService;
	private ArticleSetFactory articleSetFactory;
	private TagListPopulatingService tagListPopulatingService;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sectionDAO = SingletonFactory.getSectionDAO(this.getApplicationContext());
		articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		networkStatusService = SingletonFactory.getNetworkStatusService(this.getApplicationContext());
		tagListPopulatingService = SingletonFactory.getTagListPopulator(this.getApplicationContext());
		setContentView(R.layout.sections);
		
		setHeading("Sections");
		setHeadingColour("#0061A6");
	}
	
		
	@Override
	public void onResume() {
		super.onResume();
		setFontSize();
		
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);
		mainPane.removeAllViews();
		
		if (sectionDAO.areSectionsAvailable()) {
			populateSections();
		} else {
			outputNoSectionsWarning();
		}
	}

				
	private void populateSections() {
		List<Section> sections = sectionDAO.getSections();		
		if (sections != null) {
			sections = SectionSorter.sortByName(sections);	// TODO push this back behind the section dao for performance		
			LayoutInflater inflater = LayoutInflater.from(this);		
			LinearLayout authorList = (LinearLayout) findViewById(R.id.MainPane);
			
			tagListPopulatingService.populateTags(inflater,
					networkStatusService.isConnectionAvailable(), authorList,
					articleSetFactory.getArticleSetsForSections(sections),
					colourScheme, baseFontSize, false
			);
			
		} else {
        	outputNoSectionsWarning();
		}
	}
	
	
	private void outputNoSectionsWarning() {
		LinearLayout mainpane;
		mainpane = (LinearLayout) findViewById(R.id.MainPane);
		TextView noArticlesMessage = new TextView(this.getApplicationContext());
		noArticlesMessage.setText("No sections available.");
		
		noArticlesMessage.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
		noArticlesMessage.setTextColor(colourScheme.getHeadline());
		noArticlesMessage.setPadding(2, 3, 2, 3);					
		mainpane.addView(noArticlesMessage, 0);
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.HOME, 0, "Home");
		menu.add(0, MenuedActivity.FAVOURITES, 0, "Favourites");
	    return true;
	}
	
}