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
import java.util.Map;

import nz.gen.wellington.guardian.android.api.ArticleSetUrlService;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.ContributorArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteTagsArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.model.SearchResultsArticleSet;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.model.TagCombinerArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import nz.gen.wellington.guardian.android.usersettings.SqlLiteFavouritesDAO;
import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;
import android.content.Context;

public class ArticleSetFactory {
		
	private SettingsDAO settingsDAO;
	private ArticleSetUrlService articleSetUrlService;
	private SqlLiteFavouritesDAO sqlLiteDAO;
	private SectionDAO sectionDAO;
	
	public ArticleSetFactory(Context context) {
		this.settingsDAO = SingletonFactory.getSettingsDAO(context);
		this.articleSetUrlService = new ArticleSetUrlService(context);
		this.sqlLiteDAO = new SqlLiteFavouritesDAO(context);
		this.sectionDAO = SingletonFactory.getSectionDAO(context);
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
		List<ArticleSet> favouriteArticleSets = getFavouriteArticleSets();
		return addUrl(new FavouriteTagsArticleSet(favouriteArticleSets, settingsDAO.getPageSizePreference()));
	}
	
	public ArticleSet getTopStoriesArticleSet() {
		return addUrl(new TopStoriesArticleSet(settingsDAO.getPageSizePreference()));
	}

	public ArticleSet getArticleSetForTag(Tag tag) {
		ArticleSet articleSet;
		if (tag.isContributorTag()) {
			articleSet = new ContributorArticleSet(tag, settingsDAO.getPageSizePreference());
		} else {
			articleSet = new TagArticleSet(tag, settingsDAO.getPageSizePreference());			
		}
		return addUrl(articleSet);
	}
	
	public ArticleSet getArticleSetForTag(Tag tag, String dateDisplayName, String fromDate, String toDate, int count) {
		return addUrl(new TagArticleSet(tag, settingsDAO.getPageSizePreference(), dateDisplayName, fromDate, toDate, count));
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
	
	
	private List<ArticleSet> getFavouriteArticleSets() {	
		List<ArticleSet> favouriteArticleSets = new ArrayList<ArticleSet>();	
		List<Map<String, String>> favouriteRows = sqlLiteDAO.getFavouriteRows();	// TODO would be nice to be able to push sqllite depend back to favorites DAO.
		for (Map<String, String> row : favouriteRows) {
			ArticleSet articleSet = favouriteTagRowToArticleSet(row);
			if (articleSet != null) {
				favouriteArticleSets.add(articleSet);
			}
		}
		return favouriteArticleSets;
	}
	
	
	private ArticleSet favouriteTagRowToArticleSet(Map<String, String> row) {
		if(row.get(SqlLiteFavouritesDAO.TYPE).equals("tag")) {				
			Section section = sectionDAO.getSectionById(row.get(SqlLiteFavouritesDAO.SECTIONID));
			Tag tag = new Tag(row.get(SqlLiteFavouritesDAO.NAME), row.get(SqlLiteFavouritesDAO.APIID), section, null);	// TODO Do we know the types of favourited tags?
			return getArticleSetForTag(tag);
			
		} else if (row.get(SqlLiteFavouritesDAO.TYPE).equals("section")) {				
			Section section = sectionDAO.getSectionById(row.get(SqlLiteFavouritesDAO.SECTIONID));
			if (section != null) {
				return getArticleSetForSection(section);
			}
			
		} else if (row.get(SqlLiteFavouritesDAO.TYPE).equals("searchterm")) {
			return getArticleSetForSearchTerm(row.get(SqlLiteFavouritesDAO.SEARCHTERM));
		}
		return null;
	}
	
}
