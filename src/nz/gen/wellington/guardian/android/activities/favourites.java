package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.caching.FileService;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.sqllite.DataHelper;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class favourites extends Activity {

	private DataHelper dh;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);
          
		this.dh = new DataHelper(this);
		this.dh.deleteAll();
		this.dh.insert("keyword", "environment/bp-oil-spill", "BP oil spill", "environment");
		
		
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);
		mainPane.removeAllViews();
		populateFavourites();		
	}


	private void populateFavourites() {
		List<Tag> favouriteTags = this.dh.selectAll();
		List<Section> favouriteSections = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext())).getFavouriteSections();
		
		LayoutInflater inflater = LayoutInflater.from(this);		
		LinearLayout authorList = (LinearLayout) findViewById(R.id.MainPane);
		
		boolean connectionIsAvailable = NetworkStatusService.isConnectionAvailable(this.getApplicationContext());
		TagListPopulatingService.populateTags(inflater, connectionIsAvailable, authorList, favouriteTags, this.getApplicationContext());
		populateSections(inflater, connectionIsAvailable, authorList, favouriteSections, this.getApplicationContext());
	}
	
	
	private void populateSections(LayoutInflater inflater, boolean connectionIsAvailable,  ViewGroup tagList, List<Section> sections, Context context) {
		for (Section section: sections) {
			View tagView = inflater.inflate(R.layout.authorslist, null);
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
	    	titleText.setText(section.getName());
	    	
	    	boolean isLocallyCached = FileService.isLocallyCached(context, new SectionArticleSet(section).getApiUrl());	    	
	    	boolean contentIsAvailable = isLocallyCached || connectionIsAvailable;
	    	if (contentIsAvailable) {
	    		TagListPopulatingService.populateSectionClicker(section, tagView);

	    	} else {
	    		titleText.setTextColor(Color.DKGRAY);
	    	}	    	
	    	tagList.addView(tagView);
		}
		
	}

}
