package nz.gen.wellington.guardian.android.api.openplatfrom;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;

public class ContentApiUrlService {
	
	private static final String GUARDIAN_LITE_PROXY_API_PREFIX = "http://guardian-lite.appspot.com";
	private static final String CONTENT_API_URL = "http://content.guardianapis.com";
	
	private PreferencesDAO preferencesDAO;
	
	public ContentApiUrlService(Context context) {
		this.preferencesDAO = ArticleDAOFactory.getPreferencesDAO(context);
	}
	
	public String getContentApiUrlForArticleSet(ArticleSet articleSet) {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		populateContentApiUrlBuilderForArticleSet(contentApiUrlBuilder, articleSet);		
		contentApiUrlBuilder.setShowAll(true);
		contentApiUrlBuilder.setShowRefinements(true);	
		return getPreferedApiHost() + contentApiUrlBuilder.toSearchQueryUrl();
	}
	
	public String getContentApiUrlForArticleSetChecksum(ArticleSet articleSet) {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		populateContentApiUrlBuilderForArticleSet(contentApiUrlBuilder, articleSet);		
		contentApiUrlBuilder.setShowAll(false);
		contentApiUrlBuilder.setShowRefinements(false);	
		return getPreferedApiHost() + contentApiUrlBuilder.toSearchQueryUrl();
	}
	
	public String getSectionsQueryUrl() {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		contentApiUrlBuilder.setFormat("json");
		return getPreferedApiHost() + contentApiUrlBuilder.toSectionsQueryUrl();
	}
	
	public String getTagSearchQueryUrl(String searchTerm) {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		contentApiUrlBuilder.setPageSize(20);
		contentApiUrlBuilder.setFormat("json");
		contentApiUrlBuilder.setSearchTerm(searchTerm);
		return getPreferedApiHost() + contentApiUrlBuilder.toTagSearchQueryUrl();
	}
	
	private ContentApiStyleUrlBuilder getContentApiUrlBuilder() {	
		return new ContentApiStyleUrlBuilder(preferencesDAO.getApiKey());
	}	
	
	private void populateContentApiUrlBuilderForArticleSet(ContentApiStyleUrlBuilder contentApiUrlBuilder, ArticleSet articleSet) {
		if (articleSet instanceof SectionArticleSet) {
			contentApiUrlBuilder.addSection(((SectionArticleSet) articleSet).getSection());
		}
		
		if (articleSet instanceof TagArticleSet) {
			contentApiUrlBuilder.addTag(((TagArticleSet) articleSet).getTag());
		}
		
		if (articleSet instanceof FavouriteStoriesArticleSet) {
			FavouriteStoriesArticleSet favouriteStoriesArticleSet = (FavouriteStoriesArticleSet) articleSet;
			for (Section section : favouriteStoriesArticleSet.getSections()) {
				contentApiUrlBuilder.addSection(section);
			}
			for (Tag tag : favouriteStoriesArticleSet.getTags()) {
				contentApiUrlBuilder.addTag(tag);
			}
		}
		contentApiUrlBuilder.setPageSize(articleSet.getPageSize());		
		contentApiUrlBuilder.setFormat("xml");
	}
	
	private String getPreferedApiHost() {
		if (preferencesDAO.useContentApi()) {
			return CONTENT_API_URL;
		}
		return GUARDIAN_LITE_PROXY_API_PREFIX;
	}
	
}