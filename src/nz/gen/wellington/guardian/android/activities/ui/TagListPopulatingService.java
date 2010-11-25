package nz.gen.wellington.guardian.android.activities.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.ArticleSet;
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

	public static void populateTags(LayoutInflater inflater, boolean connectionIsAvailable, ViewGroup tagList, List<ArticleSet> articleSets, Context context) {
		NetworkStatusService networkStatusService = new NetworkStatusService(context);	// TODO push out NSS and context
		final boolean isConnectionAvailable = networkStatusService.isConnectionAvailable();
		
		//Set<String> duplicatedTagNames = getDuplicatedTagNames(tags);		
		for (ArticleSet articleSet : articleSets) {
			View tagView = inflater.inflate(R.layout.authorslist, null);						
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
			titleText.setText(articleSet.getName());			
			populateClicker(articleSet, tagView, isConnectionAvailable);
			tagList.addView(tagView);
		}
	}
	
	
	public static void populateClicker(ArticleSet articleSet, View tagView, boolean contentIsAvailable) {
		TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
		if (contentIsAvailable) {
			
			if (articleSet instanceof SectionArticleSet) {
				SectionClicker clicker = new SectionClicker(((SectionArticleSet) articleSet).getSection());
				tagView.setOnClickListener(clicker);

			} else if (contentIsAvailable) {
				ListKeywordClicker clicker = new ListKeywordClicker(((TagArticleSet) articleSet).getTag());
				tagView.setOnClickListener(clicker);
			}
			
		} else {
			titleText.setTextColor(Color.DKGRAY);
		}		
	}
	
	
	
	// TODO make this work for article sets
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
	
	// TODO make this work for article sets
	private static String getDeduplicatedTagName(Tag tag, boolean tagNameIsDuplicated) {
		if (tagNameIsDuplicated && tag.getSection() != null) {
			return tag.getSection().getName() + " - " + tag.getName();
		} else {
			return tag.getName();
		}
	}
	
}

