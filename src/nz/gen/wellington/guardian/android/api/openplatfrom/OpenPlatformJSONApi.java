package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class OpenPlatformJSONApi implements ContentSource {
		
	private static final String TAG = "OpenPlatformJSONApi";
	
	private static final String SECTIONS_JSON_URL = "http://content.guardianapis.com/sections?format=json";
	private static final int PAGE_SIZE = 10;	// TODO push to a preference
	
	private OpenPlatformApiKeyStore apiKeyStore;
	public HttpFetcher httpFetcher;
	protected OpenPlatformJSONParser jsonParser;
	private Context context;
	
	
	public OpenPlatformJSONApi(Context context, OpenPlatformApiKeyStore apiKeyStore) {
		this.apiKeyStore = apiKeyStore;
		this.context = context;
		httpFetcher = new HttpFetcher();
		jsonParser = new  OpenPlatformJSONParser();
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
					if (isWifiConnection()) {
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
			article.setMainImageUrl(jsonParser.parseArticleJSONForMainPictureUrl(itemJson));
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
		List<Section> goodSections = new ArrayList<Section>();
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
	
	
	// TODO push to a helper class - which will let us evict context from this class
	private boolean isWifiConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {
			return false;
		}		
		if (activeNetworkInfo.getTypeName().equals("WIFI")) {
			return true;
		}
		Log.i(TAG, "Active connection is of type: " + activeNetworkInfo.getTypeName());
		return false;
	}
	
}
