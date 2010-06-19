package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.OpenPlatformApiKeyStore;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.AuthorArticleSet;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.content.Context;
import android.util.Log;

public class OpenPlatformJSONApi implements ContentSource {
		
	private static final String TAG = "OpenPlatformJSONApi";

	private static final String API_HOST = "http://content.guardianapis.com";	
	private static final String SECTIONS_JSON_URL = API_HOST + "/sections?format=json";
	private static final int PAGE_SIZE = 10;	// TODO push to a preference
	
	private OpenPlatformApiKeyStore apiKeyStore;
	private HttpFetcher httpFetcher;
	protected OpenPlatformJSONParser jsonParser;
	
	
	public OpenPlatformJSONApi(Context context, OpenPlatformApiKeyStore apiKeyStore) {
		this.apiKeyStore = apiKeyStore;
		httpFetcher = new HttpFetcher(context);		
		jsonParser = new  OpenPlatformJSONParser();
	}

	
	
	
	@Override
	public void stopLoading() {
		httpFetcher.stopLoading();
	}




	@Override
	public List<Article> getArticles(ArticleSet articleSet, List<Section> sections) {
		if (apiKeyStore.getApiKey() == null) {
			Log.w(TAG, "API key not set");
			return null;
		}
		
		Log.i(TAG, "Fetching articles for: " + articleSet.getName());
		final String json = getJSON(buildContentQueryUrl(articleSet));		
		if (json != null) {	
			List<Article> articles = jsonParser.parseArticlesJSON(json, sections);			
			return articles;			
		}
		return null;
	}

	
	@Override
	public List<Section> getSections() {
		if (apiKeyStore.getApiKey() == null) {
			Log.w(TAG, "API key not set");
			return null;
		}
		Log.i(TAG, "Fetching section list from Open Platform api");
		String jsonString = getJSON(SECTIONS_JSON_URL);
		if (jsonString != null) {
			List<Section> sections = jsonParser.parseSectionsJSON(jsonString);
			if (sections != null) {
				return stripJunkSections(sections);
			}
		}
		return null;
	}
	

	// TODO this wants to move up
	private List<Section> stripJunkSections(List<Section> sections) {
		List<Section> goodSections = new LinkedList<Section>();
		List<String> badSections = Arrays.asList("Community", "Crosswords", "Extra", "Help", "Info", "Local", "From the Guardian", "From the Observer", "News", "Weather");
		for (Section section : sections) {
			if (!badSections.contains(section.getName())) {
				goodSections.add(section);				
			}
		}
		return goodSections;
	}

	
	protected String buildContentQueryUrl(ArticleSet articleSet) {		
		StringBuilder url = new StringBuilder(API_HOST + "/search");
		url.append("?show-fields=all");
		
		if (articleSet instanceof SectionArticleSet) {
			url.append("&section=" + articleSet.getApiUrl());			
		}
		
		if (articleSet instanceof KeywordArticleSet) {
			url.append("&tag=" + articleSet.getApiUrl());			
		}
		
		if (articleSet instanceof AuthorArticleSet) {
			url.append("&tag=" + articleSet.getApiUrl());			
		}
		
		url.append("&show-tags=all");
		url.append("&api-key=" + apiKeyStore.getApiKey());
		url.append("&page-size=" + PAGE_SIZE);
		url.append("&tag=type%2Farticle");
		url.append("&format=json");
		return url.toString();
	}
	
	
	private String getJSON(String url) {
		return httpFetcher.httpFetch(url);		
	}
		
}
