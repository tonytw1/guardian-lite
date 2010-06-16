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
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.Context;
import android.util.Log;

public class OpenPlatformJSONApi implements ContentSource {
		
	private static final String TAG = "OpenPlatformJSONApi";
	
	private static final String SECTIONS_JSON_URL = "http://content.guardianapis.com/sections?format=json";
	private static final int PAGE_SIZE = 10;	// TODO push to a preference
	
	private OpenPlatformApiKeyStore apiKeyStore;
	private HttpFetcher httpFetcher;
	private NetworkStatusService networkStatusService;
	protected OpenPlatformJSONParser jsonParser;
	
	
	public OpenPlatformJSONApi(Context context, OpenPlatformApiKeyStore apiKeyStore) {
		this.apiKeyStore = apiKeyStore;
		httpFetcher = new HttpFetcher(context);		
		jsonParser = new  OpenPlatformJSONParser();
		networkStatusService = new NetworkStatusService(context);
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
			if (articles != null) {	
				if (jsonParser.getUserTier(json).equals("partner")) {
					if (networkStatusService.isWifiConnection()) {
						Log.i(TAG, "Wifi is enabled - downloading full images");
						return getArticleMainPictureUrls(articles);
					}
				}
				Log.i(TAG, "Wifi is not enabled - not downloading full images");
				return articles;
			}
		}
		return null;
	}

	
	private List<Article> getArticleMainPictureUrls(List<Article> articles) {
		for (Article article : articles) {
			queryForMainPictureUrl(article);
		}
		return articles;
	}


	private void queryForMainPictureUrl(Article article) {
		String itemApiUrl = buildContentItemQueryUrl(article.getId());
		final String itemJson = getJSON(itemApiUrl);
		if (itemJson != null) {
			jsonParser.parseArticleJSONForMainPicture(itemJson, article);
			Log.i(TAG, "Found article main picture: " + article.getMainImageUrl());
		}
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
	

	private List<Section> stripJunkSections(List<Section> sections) {
		List<Section> goodSections = new LinkedList<Section>();
		List<String> badSections = Arrays.asList("Community", "Crosswords", "Extra", "Help", "Info", "Local", "From the Guardian", "From the Observer", "Weather");
		for (Section section : sections) {
			if (!badSections.contains(section.getName())) {
				goodSections.add(section);				
			}
		}
		return goodSections;
	}


	private String buildContentItemQueryUrl(String id) {
		StringBuilder url = new StringBuilder("http://content.guardianapis.com/"); // TODO should use the apiUrl Field
		url.append(id);
		url.append("?show-media=all");
		url.append("&api-key=" + apiKeyStore.getApiKey());
		url.append("&format=json");
		return url.toString();
	}
	
	
	protected String buildContentQueryUrl(ArticleSet articleSet) {		
		StringBuilder url = new StringBuilder("http://content.guardianapis.com/search");
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
