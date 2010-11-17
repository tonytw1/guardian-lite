package nz.gen.wellington.guardian.android.activities.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// TODO why is this static?
public class TagListPopulatingService {

	
	//private static final String TAG = "TagListPopulatingService";


	public static void populateTags(LayoutInflater inflater, boolean connectionIsAvailable, ViewGroup tagList, List<Tag> tags, Context context) {		
		Set<String> duplicatedTagNames = getDuplicatedTagNames(tags);		
		for (Tag tag : tags) {
			View tagView = inflater.inflate(R.layout.authorslist, null);						
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
			titleText.setText(getDeduplicatedTagName(tag, duplicatedTagNames.contains(tag.getName())));			
			populateClicker(tag, tagView, context);
	    	tagList.addView(tagView);
		}
	}


	
	public static void populateSections(LayoutInflater inflater, boolean connectionIsAvailable,  ViewGroup tagList, List<Section> sections, Context context) {
		NetworkStatusService networkStatusService = new NetworkStatusService(context);	// TODO push out NSS and context
		final boolean isConnectionAvailable = networkStatusService.isConnectionAvailable();
		
		for (Section section: sections) {
			View tagView = inflater.inflate(R.layout.authorslist, null);			
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
	    	titleText.setText(section.getName());
	    	
	    	FileBasedArticleCache fileBasedArticleCache = new FileBasedArticleCache(context);
	    	boolean isLocallyCached = fileBasedArticleCache.isLocallyCached(new SectionArticleSet(section));   	
	    	boolean contentIsAvailable = isLocallyCached || isConnectionAvailable;
	    	
    		TagListPopulatingService.populateSectionClicker(section, tagView, contentIsAvailable);	    	
	    	tagList.addView(tagView);
		}		
	}

	
	public static void populateClicker(Tag tag, View tagView, Context context) {
		NetworkStatusService networkStatusService = new NetworkStatusService(context);	// TODO push out NSS and context
		final boolean isConnectionAvailable = networkStatusService.isConnectionAvailable();

		FileBasedArticleCache fileBasedArticleCache = new FileBasedArticleCache(context);

		if (tag.isSectionTag()) {
			boolean isLocallyCached = fileBasedArticleCache.isLocallyCached(new SectionArticleSet(tag.getSection()));	    	
	    	boolean contentIsAvailable = isLocallyCached || isConnectionAvailable;
			populateSectionClicker(tag.getSection(), tagView, contentIsAvailable);	    		
		
		} else {
			boolean isLocallyCached = fileBasedArticleCache.isLocallyCached(new TagArticleSet(tag));
			boolean contentIsAvailable = isLocallyCached || isConnectionAvailable;
	    	if (contentIsAvailable) {
	    		ListKeywordClicker clicker = new ListKeywordClicker(tag);
	    		tagView.setOnClickListener(clicker);
	    	} else {
				TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
	    		titleText.setTextColor(Color.DKGRAY);
	    	}
			
		}
	}

	
	public static void populateSectionClicker(Section section, View tagView, boolean contentIsAvailable) {
    	if (contentIsAvailable) {
    		SectionClicker clicker = new SectionClicker(section);
    		tagView.setOnClickListener(clicker);
    	} else {
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
    		titleText.setTextColor(Color.DKGRAY);
    	}		
	}
	

	private static Set<String> getDuplicatedTagNames(List<Tag> tags) {
		Set<String> duplicatedTagNames = new HashSet<String>();		
		Set<String> allTagNames = new HashSet<String>();
		for (Tag tag : tags) {
			if (allTagNames.contains(tag.getName())) {
				duplicatedTagNames.add(tag.getName());
			}
			allTagNames.add(tag.getName());
		}
		return duplicatedTagNames;
	}
	
	
	private static String getDeduplicatedTagName(Tag tag, boolean tagNameIsDuplicated) {
		if (tagNameIsDuplicated && tag.getSection() != null) {
			return tag.getSection().getName() + " - " + tag.getName();
		} else {
			return tag.getName();
		}
	}
	
}

