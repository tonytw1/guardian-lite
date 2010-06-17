package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ListKeywordClicker;
import nz.gen.wellington.guardian.android.activities.ui.SectionClicker;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.caching.FileService;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
		List<Tag> favouriteTags = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext())).getFavouriteTags();
		List<Section> favouriteSections = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext())).getFavouriteSections();
		
		LayoutInflater inflater = LayoutInflater.from(this);		
		LinearLayout authorList = (LinearLayout) findViewById(R.id.MainPane);
		
		populateTags(inflater, true, authorList, favouriteTags);	// TODO true connection status		
		populateSections(inflater, true, authorList, favouriteSections);	// TODO true connection status		

	}
	
	
	private void populateSections(LayoutInflater inflater, boolean connectionIsAvailable,  ViewGroup tagList, List<Section> sections) {
		for (Section section: sections) {
			View tagView = inflater.inflate(R.layout.authorslist, null);
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
	    	titleText.setText(section.getName());
	    	tagList.addView(tagView);
		}
		
	}


	// TODO duplicated with from article
	private void populateTags(LayoutInflater inflater, boolean connectionIsAvailable, ViewGroup tagList, List<Tag> tags) {		
		for (Tag tag : tags) {
			boolean isLocallyCached = false;
			if (tag.isSectionTag()) {
				isLocallyCached = FileService.isLocallyCached(this.getApplicationContext(), new SectionArticleSet(tag.getSection()).getApiUrl());
			} else {
				isLocallyCached = FileService.isLocallyCached(this.getApplicationContext(), new KeywordArticleSet(tag).getApiUrl());
			}
			
			View tagView = inflater.inflate(R.layout.authorslist, null);		  
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
	    	titleText.setText(tag.getName());
	    	
	    	boolean contentIsAvailable = isLocallyCached || connectionIsAvailable;
	    	if (contentIsAvailable) {
	    		populateTagClicker(tag, tagView);
	    	} else {
	    		titleText.setTextColor(Color.DKGRAY);
	    	}
	    	tagList.addView(tagView);
		}
	}

	// TODO duplicated
	private void populateTagClicker(Tag tag, View tagView) {
		if (tag.isSectionTag()) {
			SectionClicker clicker = new SectionClicker(tag.getSection());
			tagView.setOnClickListener(clicker);	    		
		} else {
			ListKeywordClicker clicker = new ListKeywordClicker(tag);
			tagView.setOnClickListener(clicker);
		}
	}
	
}
