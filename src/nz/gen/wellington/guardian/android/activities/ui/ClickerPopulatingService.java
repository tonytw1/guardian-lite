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

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.section;
import nz.gen.wellington.guardian.android.activities.tag;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SearchResultsArticleSet;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import android.view.View;
import android.widget.TextView;

public class ClickerPopulatingService {
		
	public static void populateTagClicker(ArticleSet articleSet, View tagView, boolean contentIsAvailable, int contentAvailableColour, int contentUnavailableColour) {
		TextView tagName = (TextView) tagView.findViewById(R.id.TagName);
		setColour(tagName, contentIsAvailable, contentAvailableColour, contentUnavailableColour);
		populateClicker(articleSet, tagView, contentIsAvailable);
		
	}

	private static void populateClicker(ArticleSet articleSet, View tagView,
			boolean contentIsAvailable) {
		if (contentIsAvailable) {		
			// TODO suggests article sets should have knowledge about which activity renders them?
			if (articleSet instanceof SectionArticleSet) {				
				ArticleSetClicker clicker = new ArticleSetClicker(articleSet, section.class);
				tagView.setOnClickListener(clicker);
				
			} else if (articleSet instanceof SearchResultsArticleSet) {
				tagView.setOnClickListener(new SearchTermClicker(((SearchResultsArticleSet) articleSet).getSearchTerm()));
				
			} else if (contentIsAvailable) {
				ArticleSetClicker clicker = new ArticleSetClicker(articleSet, tag.class);
				tagView.setOnClickListener(clicker);
			}
			
		}
	}

	private static void setColour(TextView titleText, boolean contentIsAvailable, int contentAvailableColour, int contentUnavailableColour) {
		if (contentIsAvailable) {
			titleText.setTextColor(contentAvailableColour);			
		} else {
			titleText.setTextColor(contentUnavailableColour);			
		}
	}
	
}
