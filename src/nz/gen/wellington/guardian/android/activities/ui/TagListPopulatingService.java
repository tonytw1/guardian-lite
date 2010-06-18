package nz.gen.wellington.guardian.android.activities.ui;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.caching.FileService;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TagListPopulatingService {

	
	public static void populateTags(LayoutInflater inflater, boolean connectionIsAvailable, ViewGroup tagList, List<Tag> tags, Context context) {		
		for (Tag tag : tags) {
			boolean isLocallyCached = false;
			if (tag.isSectionTag()) {
				isLocallyCached = FileService.isLocallyCached(context, new SectionArticleSet(tag.getSection()).getApiUrl());
			} else {
				isLocallyCached = FileService.isLocallyCached(context, new KeywordArticleSet(tag).getApiUrl());
			}
			
			View tagView = inflater.inflate(R.layout.authorslist, null);		  
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
	    	titleText.setText(tag.getName());
	    	
	    	boolean contentIsAvailable = isLocallyCached || connectionIsAvailable;
	    	if (contentIsAvailable) {
	    		TagListPopulatingService.populateClicker(tag, tagView);
	    	} else {
	    		titleText.setTextColor(Color.DKGRAY);
	    	}
	    	tagList.addView(tagView);
		}
	}

	
	public static void populateClicker(Tag tag, View tagView) {
		if (tag.isSectionTag()) {
			populateSectionClicker(tag.getSection(), tagView);	    		
		} else {
			ListKeywordClicker clicker = new ListKeywordClicker(tag);
			tagView.setOnClickListener(clicker);
		}
	}

	
	public static void populateSectionClicker(Section section, View tagView) {
		SectionClicker clicker = new SectionClicker(section);
		tagView.setOnClickListener(clicker);
	}
	
}
