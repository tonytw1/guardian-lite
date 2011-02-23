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

package nz.gen.wellington.guardian.android.factories;

import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.model.Refinement;
import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;
import android.content.Context;
import android.util.Log;

public class RefinementArticleSetFactory {
	
	private static final String TAG = "RefinementArticleSetFactory";
	
	private SectionDAO sectionDAO;
	private ArticleSetFactory articleSetFactory;

	public RefinementArticleSetFactory(Context context) {
		this.sectionDAO = SingletonFactory.getSectionDAO(context);
		this.articleSetFactory = SingletonFactory.getArticleSetFactory(context);
	}
	
	public ArticleSet getArticleSetForRefinement(Refinement refinement) {		
		final String refinementType = refinement.getType();
		final String refinementId = refinement.getId();
		final String refinedUrl = refinement.getRefinedUrl();			
		
		if (refinementType == null) {
			return null;
		}
		
		Log.d(TAG, "Making article set for refinement: type='" + refinementType + "' id='" + refinementId + "'" + " refinedUrl='" + refinedUrl + "'");
		final boolean isSectionBasedTagRefinement = refinementType.equals("keyword") || refinementType.equals("blog") || refinementType.equals("series");
		if (isSectionBasedTagRefinement) { 	
			final String sectionId = refinementId.split("/")[0];
			Section section = sectionDAO.getSectionById(sectionId);			
			final Tag refinementTag = new Tag(refinement.getDisplayName(), refinementId, section, refinementType);		
			return articleSetFactory.getArticleSetForTag(refinementTag);
			
		} else if (refinementType.equals("contributor")) {
			final Tag refinementTag = new Tag(refinement.getDisplayName(), refinementId, null, refinementType);		
			return articleSetFactory.getArticleSetForTag(refinementTag);
			
		} else if (refinementType.equals("date")) {
			/*
			 *  <refinement count="6" 
			 *  	refined-url="http://content.guardianapis.com/search?callback=jsonp1298191201356&format=xml&from-date=2011-02-20&order-by=newest&section=money&show-refinements=all&to-date=2011-02-20"  
			 *  	display-name="Today" id="date/today" api-ur
			 *  <refinement count="7" 
			 *  	refined-url="http://content.guardianapis.com/search?callback=jsonp1298191201357&format=xml&from-date=2011-02-20&order-by=newest&show-refinements=all&tag=money/money&to-date=2011-02-20"  
			 *  	display-name="Today" id="date/today" api-url="http://content.guardianapis.com/search?from-date=2011-02-20&to-date=2011-02-20"></refinement>
			 */			
			final String tagId = "football/football";	// TODO
			if (tagId != null && isSingleSectionBasedTag(tagId)) {
				// TODO duplicateion and yuck
				final String sectionId = tagId.split("/")[0];
				Section section = sectionDAO.getSectionById(sectionId);			
				final Tag refinementTag = new Tag(refinement.getDisplayName(), tagId, section, null);	// TODO is a null type allowed?
				
				final String fromDate = getUrlParameterValue(refinedUrl, "from-date");
				final String toDate = getUrlParameterValue(refinedUrl, "to-date");
				return articleSetFactory.getArticleSetForTag(refinementTag, refinement.getDisplayName(), fromDate, toDate);
			}
		}
		
		return null;
	}

	private boolean isSingleSectionBasedTag(String tagId) {
		return true;	// TODO implement
	}

	// TODO fully implement
	private String getUrlParameterValue(final String refinedUrl, String parameter) {
		return refinedUrl.split(parameter + "=")[1].substring(0, 10);
	}
	
}
