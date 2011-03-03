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

package nz.gen.wellington.guardian.android.activities.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.model.colourscheme.ColourScheme;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TagListPopulatingService {
	
	private ArticleDAO articleDAO;
	
	public TagListPopulatingService(Context context) {
		this.articleDAO = SingletonFactory.getArticleDao(context);
	}

	public void populateTags(LayoutInflater inflater, boolean connectionIsAvailable, ViewGroup tagList, List<ArticleSet> articleSets, ColourScheme colourScheme, int baseFontSize) {
		Set<String> duplicatedArticleSetNames = getDuplicatedArticleSetNames(articleSets);		
		for (ArticleSet articleSet : articleSets) {

			View tagView = inflater.inflate(R.layout.authorslist, null);
			TextView titleText = (TextView) tagView.findViewById(R.id.TagName);			
			titleText.setText(composeTitleText(duplicatedArticleSetNames, articleSet));
			titleText.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
			
			final boolean isContentAvailable = articleDAO.isAvailable(articleSet);
			ClickerPopulatingService.populateTagClicker(articleSet, tagView, isContentAvailable, colourScheme.getAvailableTag(), colourScheme.getUnavailableTag());
			tagList.addView(tagView);
		}
	}

	private String composeTitleText(Set<String> duplicatedArticleSetNames, ArticleSet articleSet) {
		String title = getDeduplicatedArticleSetName(articleSet, duplicatedArticleSetNames);
		if (articleSet.getCount() > 0) {
			title = title + " (" + articleSet.getCount() + ")";
		}
		return title;
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
