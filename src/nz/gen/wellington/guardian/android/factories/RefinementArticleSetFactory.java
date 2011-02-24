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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
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
	
	public ArticleSet getArticleSetForRefinement(Refinement refinement, ArticleSet articleSet) {		
		final String refinementType = refinement.getType();
		final String refinementId = refinement.getId();
		final String refinedUrl = refinement.getRefinedUrl();			
		
		if (refinementType == null) {
			return null;
		}
		
		Log.d(TAG, "Making article set for refinement: type='" + refinementType + "' id='" + refinementId + "'" + " refinedUrl='" + refinedUrl + "'");		
		if (refinementType.equals("type")) {
			final boolean isGalleryRefinement = refinement.getType().equals("type") && refinement.getId().equals("type/gallery");	// TODO check what the id is for gallery combiners
			if (isGalleryRefinement && articleSet instanceof TagArticleSet) {				
				Tag galleryContentType = new Tag("Gallery content type", "type/gallery", null, "type");				
				return articleSetFactory.getArticleSetForTagCombiner(((TagArticleSet) articleSet).getTag(), galleryContentType);				
			}
		}
		
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
			final String tagId = getUrlTagParameterValue(refinedUrl);
			Log.d(TAG, "Refined url tag paramater is: " + refinedUrl);
			if (tagId != null && isSingleSectionBasedTag(tagId)) {
				// TODO duplicateion and yuck
				final String sectionId = tagId.split("/")[0];
				Section section = sectionDAO.getSectionById(sectionId);			
				final Tag refinementTag = new Tag(refinement.getDisplayName(), tagId, section, null);	// TODO is a null type allowed?
				
				final String fromDate = getUrlDateParameterValue(refinedUrl, "from-date");
				final String toDate = getUrlDateParameterValue(refinedUrl, "to-date");
				return articleSetFactory.getArticleSetForTag(refinementTag, refinement.getDisplayName(), fromDate, toDate);
			}
		}
		
		return null;
	}

	private String getUrlTagParameterValue(String refinedUrl) {
		Pattern pattern = Pattern.compile("^.*?tag=(.*?)&.*?$");
		Matcher matcher = pattern.matcher(refinedUrl);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	private boolean isSingleSectionBasedTag(String tagId) {
		return true;	// TODO implement
	}

	// TODO fully implement
	private String getUrlDateParameterValue(final String refinedUrl, String parameter) {
		return refinedUrl.split(parameter + "=")[1].substring(0, 10);
	}
	
}
