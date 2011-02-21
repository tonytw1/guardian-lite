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

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleSetUrlService;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteTagsArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.model.SearchResultsArticleSet;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.model.TagCombinerArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import nz.gen.wellington.guardian.model.Refinement;
import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;
import android.content.Context;
import android.util.Log;

public class ArticleSetFactory {
	
	private static final String TAG = "ArticleSetFactory";
	
	private SettingsDAO settingsDAO;
	private ArticleSetUrlService articleSetUrlService;
	private FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
	private SectionDAO sectionDAO;
	
	public ArticleSetFactory(Context context) {
		this.settingsDAO = SingletonFactory.getSettingsDAO(context);
		this.favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(context);
		this.sectionDAO = new SingletonFactory().getSectionDAO(context);
		this.articleSetUrlService = new ArticleSetUrlService(context);
	}

	public ArticleSet getAboutArticleSet() {
		return addUrl(new AboutArticleSet(settingsDAO.getPageSizePreference()));
	}
	
	public ArticleSet getSavedArticlesArticleSet(List<String> articleIds) {
		SavedArticlesArticleSet savedArticlesArticleSet = new SavedArticlesArticleSet(articleIds);
		savedArticlesArticleSet.setSourceUrl(articleSetUrlService.getUrlForArticleSet(savedArticlesArticleSet));
		return savedArticlesArticleSet;
	}
	
	public ArticleSet getArticleSetForSearchTerm(String searchTerm) {
		return addUrl(new SearchResultsArticleSet(searchTerm, settingsDAO.getPageSizePreference()));
	}
	
	public ArticleSet getFavouritesArticleSet() {
		List<ArticleSet> favouriteArticleSets = favouriteSectionsAndTagsDAO.getFavouriteArticleSets();
		return addUrl(new FavouriteTagsArticleSet(favouriteArticleSets, settingsDAO.getPageSizePreference()));
	}
	
	public ArticleSet getTopStoriesArticleSet() {
		return addUrl(new TopStoriesArticleSet(settingsDAO.getPageSizePreference()));
	}

	public ArticleSet getArticleSetForTag(Tag tag) {
		return addUrl(new TagArticleSet(tag, settingsDAO.getPageSizePreference()));
	}
	
	
	public ArticleSet getArticleSetForTag(Tag tag, String dateDisplayName, String fromDate, String toDate) {
		return addUrl(new TagArticleSet(tag, settingsDAO.getPageSizePreference(), dateDisplayName, fromDate, toDate));
	}
	
	public ArticleSet getArticleSetForTagCombiner(Tag leftTag, Tag rightTag) {
		return addUrl(new TagCombinerArticleSet(leftTag, rightTag, settingsDAO.getPageSizePreference()));
	}
	
	public ArticleSet getArticleSetForSection(Section section) {
		return addUrl(new TagArticleSet(section.getTag(), settingsDAO.getPageSizePreference()));
	}
	
	public ArticleSet getArticleSetForSection(Section section, String dateDisplayName, String fromDate, String toDate) {
		return addUrl(new TagArticleSet(section.getTag(), settingsDAO.getPageSizePreference(), dateDisplayName, fromDate, toDate));
	}
		
	public List<ArticleSet> getArticleSetsForSections(List<Section> favouriteSections) {
		List<ArticleSet> favouriteSectionsArticleSets = new ArrayList<ArticleSet>();			
		for (Section section : favouriteSections) {
			favouriteSectionsArticleSets.add(getArticleSetForSection(section));
		}
		return favouriteSectionsArticleSets;
	}
	
	public List<ArticleSet> getArticleSetsForTags(List<Tag> tags) {
		List<ArticleSet> articleSets = new ArrayList<ArticleSet>();
		for (Tag tag : tags) {			
			if (!tag.isContentTypeTag()) {
				boolean isSectionTag = tag.isSectionKeyword();
				if (isSectionTag) {
					articleSets.add(getArticleSetForSection(tag.getSection()));				
				} else {
					articleSets.add(getArticleSetForTag(tag));
				}
			}
		}
		return articleSets;
	}
	
	// TODO Three different methods - can they be merged?
	public List<ArticleSet> getArticleSetsForSearchTerms(List<String> favouriteSearchTerms) {
		List<ArticleSet> favouriteSearchTermArticleSets = new ArrayList<ArticleSet>();
		for (String searchTerm : favouriteSearchTerms) {
			favouriteSearchTermArticleSets.add(getArticleSetForSearchTerm(searchTerm));			
		}
		return favouriteSearchTermArticleSets;
	}
	
	
	private ArticleSet addUrl(ArticleSet articleSet) {
		articleSet.setSourceUrl(articleSetUrlService.getUrlForArticleSet(articleSet));
		return articleSet;
	}
	
	public ArticleSet getArticleSetForRefinement(Refinement refinement) {		
		if (refinement.getType() == null) {
			return null;
		}		
		Log.d(TAG, "Making article set for refinement: type='" + refinement.getType() + "' id='" + refinement.getId() + "'");
		
		final boolean isSectionBasedTagRefinement = refinement.getType().equals("keyword") || refinement.getType().equals("blog") || refinement.getType().equals("series");
		if (isSectionBasedTagRefinement) { 	
			final String sectionId = refinement.getId().split("/")[0];
			Section section = sectionDAO.getSectionById(sectionId);			
			final Tag refinementTag = new Tag(refinement.getDisplayName(), refinement.getId(), section, refinement.getType());		
			return getArticleSetForTag(refinementTag);
			
		} else if (refinement.getType().equals("contributor")) {
			final Tag refinementTag = new Tag(refinement.getDisplayName(), refinement.getId(), null, refinement.getType());		
			return getArticleSetForTag(refinementTag);
			
		} else if (refinement.getType().equals("date")) {
			/*
			 *  <refinement count="6" 
			 *  	refined-url="http://content.guardianapis.com/search?callback=jsonp1298191201356&format=xml&from-date=2011-02-20&order-by=newest&section=money&show-refinements=all&to-date=2011-02-20"  
			 *  	display-name="Today" id="date/today" api-ur
			 *  <refinement count="7" 
			 *  	refined-url="http://content.guardianapis.com/search?callback=jsonp1298191201357&format=xml&from-date=2011-02-20&order-by=newest&show-refinements=all&tag=money/money&to-date=2011-02-20"  
			 *  	display-name="Today" id="date/today" api-url="http://content.guardianapis.com/search?from-date=2011-02-20&to-date=2011-02-20"></refinement>
			 */
			
			
			//final Tag refinementTag = new Tag(refinement.getDisplayName(), refinement.getId(), section, refinement.getType());
			// TODO regex checking and extraction of these fields.
			//final String fromDate = refinement.getRefinedUrl().split("from-date=")[1].substring(0, 10);			
			//final String toDate = refinement.getRefinedUrl().split("to-date=")[1].substring(0, 10);

			//return getArticleSetForTag(refinementTag, refinement.getDisplayName(), fromDate, toDate);
		}
		
		return null;
	}
	
}
