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

package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.util.List;

import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteTagsArticleSet;
import nz.gen.wellington.guardian.android.model.SearchResultsArticleSet;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.model.TagCombinerArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import nz.gen.wellington.guardian.model.Tag;

public class ContentApiUrlService {
	
	private String apiHost;
	private String apiKey;
	private List<Tag> supportedContentTypes;
	
	public ContentApiUrlService(String apiHost, String apiKey, List<Tag> supportedContentTypes) {
		this.apiHost = apiHost;
		this.apiKey = apiKey;
		this.supportedContentTypes = supportedContentTypes;	// TODO should come from the article sets.
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
	
	public String getTagSearchQueryUrl(String searchTerm, List<String> allowedTypes) {
		ContentApiStyleUrlBuilder contentApiUrlBuilder = getContentApiUrlBuilder();
		contentApiUrlBuilder.setPageSize(20);		
		contentApiUrlBuilder.setFormat("json");
		contentApiUrlBuilder.setSearchTerm(searchTerm);
		
		for (String allowedType : allowedTypes) {
			contentApiUrlBuilder.addTagType(allowedType);			
		}
				
		return contentApiUrlBuilder.toTagSearchQueryUrl();
	}
	
	private ContentApiStyleUrlBuilder getContentApiUrlBuilder() {	
		return new ContentApiStyleUrlBuilder(apiHost, apiKey);
	}	
	
	private void populateContentApiUrlBuilderForArticleSet(ContentApiStyleUrlBuilder contentApiUrlBuilder, ArticleSet articleSet) {
		
		if (articleSet instanceof TagArticleSet) {
			TagArticleSet tagArticleSet = (TagArticleSet) articleSet;
			contentApiUrlBuilder.addTag(tagArticleSet.getTag());
			if (tagArticleSet.getFromDate() != null) {
				contentApiUrlBuilder.setFromDate(tagArticleSet.getFromDate());
				contentApiUrlBuilder.setToDate(tagArticleSet.getToDate());
			}
			contentApiUrlBuilder.andMustBeOneOff(supportedContentTypes);
		}
		
		if (articleSet instanceof TagCombinerArticleSet) {
			TagCombinerArticleSet tagCombinerArticleSet = (TagCombinerArticleSet) articleSet;
			contentApiUrlBuilder.addTag(tagCombinerArticleSet.getLeftTag());
			contentApiUrlBuilder.andMustBeOneOff(tagCombinerArticleSet.getRightTag());
		}
		
		if (articleSet instanceof FavouriteTagsArticleSet) {
			FavouriteTagsArticleSet favouriteStoriesArticleSet = (FavouriteTagsArticleSet) articleSet;
			for (ArticleSet favouriteArticleSet : favouriteStoriesArticleSet.getArticleSets()) {
				contentApiUrlBuilder.addTag(((TagArticleSet) favouriteArticleSet).getTag());				
			}
			contentApiUrlBuilder.andMustBeOneOff(supportedContentTypes);
		}
		
		if (articleSet instanceof SearchResultsArticleSet) {
			contentApiUrlBuilder.setSearchTerm(((SearchResultsArticleSet) articleSet).getSearchTerm());
			contentApiUrlBuilder.andMustBeOneOff(supportedContentTypes);
		}
		
		if (articleSet instanceof TopStoriesArticleSet) {
			contentApiUrlBuilder.andMustBeOneOff(supportedContentTypes);
		}
					
		contentApiUrlBuilder.setPageSize(articleSet.getPageSize());
		contentApiUrlBuilder.setShowMedia(articleSet.getShowMedia());
		contentApiUrlBuilder.setFormat("xml");
	}
	
}