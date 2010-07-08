package nz.gen.wellington.guardian.android.activities.ui;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.caching.FileService;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TagListPopulatingService {

	
	private static final String TAG = "TagListPopulatingService";


	public static void populateTags(LayoutInflater inflater, boolean connectionIsAvailable, ViewGroup tagList, List<Tag> tags, Context context) {		
		for (Tag tag : tags) {
			//Log.d(TAG, "Populating tag: " + tag.getId() + " (" + tag.getName() + ")");
			View tagView = inflater.inflate(R.layout.authorslist, null);
						
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
			titleText.setText(tag.getName());
			
			populateClicker(tag, tagView, context);
	    	tagList.addView(tagView);
		}
	}
	
	
	public static void populateSections(LayoutInflater inflater, boolean connectionIsAvailable,  ViewGroup tagList, List<Section> sections, Context context) {
		for (Section section: sections) {
			View tagView = inflater.inflate(R.layout.authorslist, null);			
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
	    	titleText.setText(section.getName());
    		TagListPopulatingService.populateSectionClicker(section, tagView, context);	    	
	    	tagList.addView(tagView);
		}
		
	}

	
	public static void populateClicker(Tag tag, View tagView, Context context) {
		if (tag.isSectionTag()) {
			populateSectionClicker(tag.getSection(), tagView, context);	    		
		
		} else {			
			NetworkStatusService networkStatusService = new NetworkStatusService(context);
			boolean isLocallyCached = FileService.isLocallyCached(context, new KeywordArticleSet(tag).getApiUrl());
	    	boolean contentIsAvailable = isLocallyCached || networkStatusService.isConnectionAvailable();
	    	if (contentIsAvailable) {
	    		ListKeywordClicker clicker = new ListKeywordClicker(tag);
	    		tagView.setOnClickListener(clicker);
	    	} else {
				TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
	    		titleText.setTextColor(Color.DKGRAY);
	    	}
			
		}
	}

	
	public static void populateSectionClicker(Section section, View tagView, Context context) {
		NetworkStatusService networkStatusService = new NetworkStatusService(context);
		boolean isLocallyCached = FileService.isLocallyCached(context, new SectionArticleSet(section).getApiUrl());	    	
    	boolean contentIsAvailable = isLocallyCached || networkStatusService.isConnectionAvailable();
    	if (contentIsAvailable) {
    		SectionClicker clicker = new SectionClicker(section);
    		tagView.setOnClickListener(clicker);
    	} else {
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
    		titleText.setTextColor(Color.DKGRAY);
    	}	    
		
	}
	
}
