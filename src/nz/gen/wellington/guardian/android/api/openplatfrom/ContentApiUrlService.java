package nz.gen.wellington.guardian.android.api.openplatfrom;

import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteTagsArticleSet;
import nz.gen.wellington.guardian.android.model.SearchResultsArticleSet;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.TagArticleSet;

public class ContentApiUrlService {
	
	private String apiHost;
	private String apiKey;
	
	public ContentApiUrlService(String apiHost, String apiKey) {
		this.apiHost = apiHost;
		this.apiKey = apiKey;
	}

	public String getContentApiUrlForArticleSet(ArticleSet articleSet) {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		populateContentApiUrlBuilderForArticleSet(contentApiUrlBuilder, articleSet);		
		contentApiUrlBuilder.setShowAll(true);
		contentApiUrlBuilder.setShowRefinements(true);	
		return contentApiUrlBuilder.toSearchQueryUrl();
	}
	
	public String getContentApiUrlForArticleSetChecksum(ArticleSet articleSet) {
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
		return new ContentApiStyleUrlBuilder(apiHost, apiKey);
	}	
	
	private void populateContentApiUrlBuilderForArticleSet(ContentApiStyleUrlBuilder contentApiUrlBuilder, ArticleSet articleSet) {
		if (articleSet instanceof SectionArticleSet) {
			contentApiUrlBuilder.addSection(((SectionArticleSet) articleSet).getSection());
		}
		
		if (articleSet instanceof TagArticleSet) {
			contentApiUrlBuilder.addTag(((TagArticleSet) articleSet).getTag());
		}
		
		if (articleSet instanceof FavouriteTagsArticleSet) {
			FavouriteTagsArticleSet favouriteStoriesArticleSet = (FavouriteTagsArticleSet) articleSet;
			for (ArticleSet favouroteArticleSet : favouriteStoriesArticleSet.getArticleSets()) {
				if (articleSet instanceof SectionArticleSet) {
					contentApiUrlBuilder.addSection(((SectionArticleSet) favouroteArticleSet).getSection());					
				} else if (articleSet instanceof TagArticleSet) {
					contentApiUrlBuilder.addTag(((TagArticleSet) favouroteArticleSet).getTag());
				}
			};
		}
		
		if (articleSet instanceof SearchResultsArticleSet) {
			contentApiUrlBuilder.setSearchTerm(((SearchResultsArticleSet) articleSet).getSearchTerm());
		}
		
		contentApiUrlBuilder.setPageSize(articleSet.getPageSize());		
		contentApiUrlBuilder.setFormat("xml");
	}
	
}