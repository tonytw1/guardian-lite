package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.OpenPlatformApiKeyStore;
import nz.gen.wellington.guardian.android.api.caching.FileService;
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
	public static final String SECTIONS_API_URL = "sections";
	private static final int PAGE_SIZE = 10;	// TODO push to a preference
	
	private OpenPlatformApiKeyStore apiKeyStore;
	private HttpFetcher httpFetcher;

	private Context context;
	
	
	public OpenPlatformJSONApi(Context context, OpenPlatformApiKeyStore apiKeyStore) {
		this.context = context;
		this.apiKeyStore = apiKeyStore;
		httpFetcher = new HttpFetcher(context);		
	}

	
	@Override
	public List<Article> getArticles(ArticleSet articleSet, List<Section> sections) {
		if (apiKeyStore.getApiKey() == null) {
			Log.w(TAG, "API key not set");
			return null;
		}
		
		Log.i(TAG, "Fetching articles for: " + articleSet.getName());
		final String apiUrl = articleSet.getApiUrl();
		
		boolean shouldCache = false;
		InputStream input = null;
		if (FileService.isLocallyCached(context, apiUrl)) {		
			try {
				Log.i(TAG, "Using locally cached copy of: " + apiUrl);
				input = FileService.getFileInputStream(context, apiUrl);
			} catch (FileNotFoundException e) {				
			}
		}
		
		if (input == null) {
			Log.i(TAG, "Fetching article set from live api: " + apiUrl);
			input = getHttpInputStream(buildContentQueryUrl(articleSet));	
			shouldCache = true;
		}		
		
		if (input != null) {
			OpenPlatformJSONParser jsonParser = new OpenPlatformJSONParser();
			List<Article> articles = jsonParser.parseArticlesJSON(input, sections);
			
			if (articles != null && !articles.isEmpty()) {
				if (shouldCache) {
					cacheApiUrlContent(jsonParser.getConsumedContent(), apiUrl);
				}
				return articles;
			}
		}
		return null;
	}

	
	@Override
	public List<Section> getSections() {
		if (apiKeyStore.getApiKey() == null) {
			Log.w(TAG, "API key not set");
			return null;
		}
		
		boolean shouldCache = false;
		InputStream input = null;
		if (FileService.isLocallyCached(context, SECTIONS_API_URL)) {		
			try {
				Log.i(TAG, "Using locally cached copy of: " + SECTIONS_API_URL);
				input = FileService.getFileInputStream(context, SECTIONS_API_URL);
			} catch (FileNotFoundException e) {				
			}
		}
		
		if (input == null) {
			Log.i(TAG, "Fetching section list from live api");
			input = getHttpInputStream(buildSectionsQueryUrl());
			shouldCache = true;
		}
		
		if (input != null) {
			OpenPlatformJSONParser jsonParser = new OpenPlatformJSONParser();
			List<Section> sections = jsonParser.parseSectionsJSON(input);
			if (sections != null) {
				
				if (shouldCache) {
					cacheApiUrlContent(jsonParser.getConsumedContent(), SECTIONS_API_URL);
				}
				
				return stripJunkSections(sections);
			}
		}
		return null;
	}
	
	
	@Override
	public void stopLoading() {
		httpFetcher.stopLoading();
	}
	
	
	private void cacheApiUrlContent(String content, String apiUrl) {		
		Log.i(TAG, "Writing to disk: " + apiUrl);
		try {
			FileWriter writer = FileService.getFileWriter(context, apiUrl);	
			writer.write(content);
			writer.close();
		} catch (IOException ex) {
			Log.e(TAG, "IO Exception while writing cache file for api url: " + apiUrl + ", " + ex.getMessage());
		}
	}
	

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

	
	
	private String buildSectionsQueryUrl() {
		StringBuilder url = new StringBuilder(API_HOST + "/" + SECTIONS_API_URL);
		url.append("?format=json");
		return url.toString();
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
	
	
	private InputStream getHttpInputStream(String url) {
		return httpFetcher.httpFetch(url);		
	}
		
}
