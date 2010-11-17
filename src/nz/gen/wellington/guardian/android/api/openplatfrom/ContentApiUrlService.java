package nz.gen.wellington.guardian.android.api.openplatfrom;

import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;

public class ContentApiUrlService {
	
	private static final String GUARDIAN_LITE_PROXY_API_PREFIX = "http://guardian-lite.appspot.com";
	public static final String CONTENT_API_URL = "http://content.guardianapis.com";
	
	private PreferencesDAO preferencesDAO;

	// TODO page size should be on article set
	public String getContentApiUrlForArticleSet(ArticleSet articleSet, int pageSize) {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		populateContentApiUrlBuilderForArticleSet(contentApiUrlBuilder, articleSet);		
		contentApiUrlBuilder.setShowAll(true);
		contentApiUrlBuilder.setShowRefinements(true);	
		return contentApiUrlBuilder.toSearchQueryUrl();
	}
	
	// TODO page size should be on article set
	public String getContentApiUrlForArticleSetChecksum(ArticleSet articleSet, int pageSize) {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		populateContentApiUrlBuilderForArticleSet(contentApiUrlBuilder, articleSet);		
		contentApiUrlBuilder.setShowAll(false);
		contentApiUrlBuilder.setShowRefinements(false);	
		return contentApiUrlBuilder.toSearchQueryUrl();
	}
	
	public String getSectionsQueryUrl() {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		contentApiUrlBuilder.setFormat("json");
		return contentApiUrlBuilder.toSectionsQueryUrl();
	}
	
	public String getTagSearchQueryUrl(String searchTerm) {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		contentApiUrlBuilder.setPageSize(20);
		contentApiUrlBuilder.setFormat("json");
		contentApiUrlBuilder.setSearchTerm(searchTerm);
		return contentApiUrlBuilder.toTagSearchQueryUrl();
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
		contentApiUrlBuilder.setFormat("xml");
	}
	
}
