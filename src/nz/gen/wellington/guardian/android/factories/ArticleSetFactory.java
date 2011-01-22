package nz.gen.wellington.guardian.android.factories;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleSetUrlService;
import nz.gen.wellington.guardian.android.api.openplatfrom.Refinement;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteTagsArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.model.SearchResultsArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;

public class ArticleSetFactory {
	
	private PreferencesDAO preferencesDAO;
	private ArticleSetUrlService articleSetUrlService;
	private FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
	
	public ArticleSetFactory(Context context) {
		this.preferencesDAO = SingletonFactory.getPreferencesDAO(context);
		this.favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(context);
		this.articleSetUrlService = new ArticleSetUrlService(context);
	}

	public ArticleSet getAboutArticleSet() {
		return addUrl(new AboutArticleSet(preferencesDAO.getPageSizePreference()));
	}
	
	public ArticleSet getSavedArticlesArticleSet(List<String> articleIds) {
		SavedArticlesArticleSet savedArticlesArticleSet = new SavedArticlesArticleSet(articleIds);
		savedArticlesArticleSet.setSourceUrl(articleSetUrlService.getUrlForArticleSet(savedArticlesArticleSet));
		return savedArticlesArticleSet;
	}
	
	public ArticleSet getArticleSetForSearchTerm(String searchTerm) {
		return addUrl(new SearchResultsArticleSet(searchTerm, preferencesDAO.getPageSizePreference()));
	}
	
	public ArticleSet getFavouritesArticleSet() {
		List<ArticleSet> favouriteArticleSets = favouriteSectionsAndTagsDAO.getFavouriteArticleSets();
		return addUrl(new FavouriteTagsArticleSet(favouriteArticleSets, preferencesDAO.getPageSizePreference()));
	}
	
	public ArticleSet getTopStoriesArticleSet() {
		return addUrl(new TopStoriesArticleSet(preferencesDAO.getPageSizePreference()));
	}

	public ArticleSet getArticleSetForTag(Tag tag) {
		return addUrl(new TagArticleSet(tag, preferencesDAO.getPageSizePreference()));
	}
	
	
	public ArticleSet getArticleSetForTag(Tag tag, String dateDisplayName, String fromDate, String toDate) {
		return addUrl(new TagArticleSet(tag, preferencesDAO.getPageSizePreference(), dateDisplayName, fromDate, toDate));
	}
	
	public ArticleSet getArticleSetForSection(Section section) {
		return addUrl(new SectionArticleSet(section, preferencesDAO.getPageSizePreference()));
	}
	
	public ArticleSet getArticleSetForSection(Section section, String dateDisplayName, String fromDate, String toDate) {
		return addUrl(new SectionArticleSet(section, preferencesDAO.getPageSizePreference(), dateDisplayName, fromDate, toDate));
	}
	
	
	public List<ArticleSet> getArticleSetsForSections(List<Section> favouriteSections) {
		List<ArticleSet> favouriteSectionsArticleSets = new ArrayList<ArticleSet>();			
		for (Section section : favouriteSections) {
			favouriteSectionsArticleSets.add(getArticleSetForSection(section));
		}
		return favouriteSectionsArticleSets;
	}
	
	public List<ArticleSet> getArticleSetsForTags(List<Tag> favouriteTags) {
		List<ArticleSet> favouriteTagsArticleSets = new ArrayList<ArticleSet>();
		for (Tag tag : favouriteTags) {
			
			boolean isSectionTag = tag.isSectionKeyword();
			if (isSectionTag) {
				favouriteTagsArticleSets.add(getArticleSetForSection(tag.getSection()));				
			} else {
				favouriteTagsArticleSets.add(getArticleSetForTag(tag));
			}
		}
		return favouriteTagsArticleSets;
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

	public Refinement getRefinementForTag(Tag tag) {
		return new Refinement(tag);
	}

	public Refinement getRefinementForSection(Section section) {
		return new Refinement(section);
	}

	public ArticleSet getArticleSetForRefinement(ArticleSet articleSet, Refinement refinement) {		
		if (refinement.getTag() != null) {
			return getArticleSetForTag(refinement.getTag());		
		}
		
		if (refinement.getFromDate() != null && articleSet instanceof TagArticleSet) {
			return getArticleSetForTag(((TagArticleSet) articleSet).getTag(), refinement.getDisplayName(), refinement.getFromDate(), refinement.getToDate());			
		}
		
		if (refinement.getFromDate() != null && articleSet instanceof SectionArticleSet) {
			return getArticleSetForSection(((SectionArticleSet) articleSet).getSection(), refinement.getDisplayName(), refinement.getFromDate(), refinement.getToDate());			
		}
				
		return null;
	}

	public Refinement getRefinementForDate(String displayName, String fromDate, String toDate) {
		return new Refinement(displayName, fromDate, toDate);
	}
	
}
