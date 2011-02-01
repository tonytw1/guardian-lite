package nz.gen.wellington.guardian.android.activities.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TagListPopulatingService {
	
	private ArticleDAO articleDAO;
	
	public TagListPopulatingService(Context context) {
		articleDAO = SingletonFactory.getArticleDao(context);
	}

	public void populateTags(LayoutInflater inflater, boolean connectionIsAvailable, ViewGroup tagList, List<ArticleSet> articleSets, ColourScheme colourScheme) {
		Set<String> duplicatedArticleSetNames = getDuplicatedArticleSetNames(articleSets);		
		for (ArticleSet articleSet : articleSets) {
			final boolean isContentAvailable = articleDAO.isAvailable(articleSet);
			View tagView = inflater.inflate(R.layout.authorslist, null);
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
			
			titleText.setText(getDeduplicatedArticleSetName(articleSet, duplicatedArticleSetNames));
			
			ClickerPopulatingService.populateTagClicker(articleSet, tagView, isContentAvailable, colourScheme.getAvailableTag(), colourScheme.getUnavailableTag());
			tagList.addView(tagView);
		}
	}
	
	private Set<String> getDuplicatedArticleSetNames(List<ArticleSet> articleSets) {
		Set<String> duplicatedArticleSetNames = new HashSet<String>();
		
		Set<String> allArticleSetNames = new HashSet<String>();
		for (ArticleSet articleSet : articleSets) {
			final String shortName = articleSet.getShortName();
			if (allArticleSetNames.contains(shortName)) {
				duplicatedArticleSetNames.add(shortName);
			}
			allArticleSetNames.add(shortName);
		}
		return duplicatedArticleSetNames;
	}
		
	private String getDeduplicatedArticleSetName(ArticleSet articleSet, Set<String> duplicatedArticleSetNames) {
		final String articleSetShortName = articleSet.getShortName();
		final boolean articleSetNameIsDuplicated = duplicatedArticleSetNames.contains(articleSetShortName);
		if (articleSetNameIsDuplicated && articleSet instanceof TagArticleSet) {
			return articleSet.getName();
		}
		return articleSetShortName;
	}

}

