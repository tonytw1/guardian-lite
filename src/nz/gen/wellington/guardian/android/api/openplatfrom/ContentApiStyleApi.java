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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.network.LoggingBufferedInputStream;
import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;
import android.content.Context;
import android.util.Log;

public class ContentApiStyleApi implements ContentSource {
		
	private static final String TAG = "ContentApiStyleApi";
		
	private ContentApiStyleXmlParser contentXmlParser;
	private ContentApiStyleJSONParser contentJsonParser;
	private HttpFetcher httpFetcher;
	final private int clientVersion;
	final private String apiHost;
	final private String apiKey;
	final private List<Tag> supportedContentTypes;
	
	public ContentApiStyleApi(Context context, int clientVersion, String apiHost, String apiKey, List<Tag> supportedContentTypes) {
		this.contentXmlParser = new ContentApiStyleXmlParser(context);
		this.contentJsonParser = new ContentApiStyleJSONParser();
		this.httpFetcher = new HttpFetcher(context);
		this.clientVersion = clientVersion;
		this.apiHost = apiHost;
		this.apiKey = apiKey;
		this.supportedContentTypes = supportedContentTypes;
	}
	
	
	@Override
	public ArticleBundle getArticles(ArticleSet articleSet, List<Section> sections, ArticleCallback articleCallback) {
		Log.i(TAG, "Fetching articles for: " + articleSet.getName());
		
		final String contentApiUrl = articleSet.getSourceUrl() + "&v=" + clientVersion;
		LoggingBufferedInputStream input = httpFetcher.httpFetch(contentApiUrl, articleSet.getName() + " article set");	
		if (input != null) {
			ArticleBundle results = contentXmlParser.parseArticlesXml(input, articleCallback);
			if (results != null && !results.getArticles().isEmpty()) {
				String checksum = input.getEtag();
				results.setChecksum(checksum);
				try {
					input.close();
				} catch (IOException e) {
					Log.w(TAG, "Failed to close input stream");
				}
				return results;
			}
		}
		return null;
	}
	
	
	@Override
	public String getRemoteChecksum(ArticleSet articleSet, int pageSize) {		
		Log.i(TAG, "Fetching article set checksum for article set: " + articleSet.getName());		
		final String contentApiUrl = articleSet.getSourceUrl();	
		return httpFetcher.httpEtag(contentApiUrl, articleSet.getName() + " article set checksum");
	}
	
	
	public String getUserTierForKey() {
		Log.i(TAG, "Fetching empty item query from live api to check user tier");
		final String contentApiUrl = "http://content.guardianapis.com/?format=json&order-by=newest&api-key=" + apiKey;	// TODO push to builder		
		InputStream input = httpFetcher.httpFetch(contentApiUrl, "api user tier");
		if (input != null) {
			final String userTier = contentJsonParser.parseUserTier(input);
			try {
				input.close();
			} catch (IOException e) {
				Log.w(TAG, "Failed to close input stream");
			}
			return userTier;
		}
		return null;
	}
	
	
	@Override
	public List<Section> getSections() {
		Log.i(TAG, "Fetching section list from live api");
		ContentApiUrlService contentApiUrlService = initContentApiUrlService();
		String contentApiUrl = contentApiUrlService.getSectionsQueryUrl();
		InputStream input = httpFetcher.httpFetch(contentApiUrl, "sections");
		if (input != null) {
			List<Section> results = contentJsonParser.parseSectionsJSON(input);
			try {
				input.close();
			} catch (IOException e) {
				Log.w(TAG, "Failed to close input stream");
			}
			return results;
		}
		return null;
	}
	
	
	@Override
	public List<Tag> searchTags(String searchTerm, List<String> allowedTagSearchTypes, Map<String, Section> sections) {
		Log.i(TAG, "Fetching tag list from live api: " + searchTerm);
		ContentApiUrlService contentApiUrlService = initContentApiUrlService();
		
		InputStream input = httpFetcher.httpFetch(contentApiUrlService.getTagSearchQueryUrl(searchTerm, allowedTagSearchTypes), "tag results");
		if (input != null) {
			List<Tag> results = contentJsonParser.parseTagsJSON(input, sections);
			try {
				input.close();
			} catch (IOException e) {
				Log.w(TAG, "Failed to close input stream");
			}
			return results;
		}
		return null;
	}


	@Override
	public void stopLoading() {
		Log.i(TAG, "Stopping content api loading");
		contentXmlParser.stop();
		httpFetcher.stopLoading();
	}
	
	private ContentApiUrlService initContentApiUrlService() {
		return new ContentApiUrlService(apiHost, apiKey, supportedContentTypes);
	}
	
}
