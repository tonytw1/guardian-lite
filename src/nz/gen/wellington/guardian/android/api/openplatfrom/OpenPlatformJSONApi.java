package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.dates.DateTimeHelper;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class OpenPlatformJSONApi implements ContentSource {
		
	private static final String TAG = "OpenPlatformJSONApi";

	private static final String API_HOST = "http://content-api-proxy.appspot.com";	
	public static final String SECTIONS_API_URL = "sections";
	
	private OpenPlatformJSONParser contentParser;
	private HttpFetcher httpFetcher;

	private Context context;

	
	public OpenPlatformJSONApi(Context context) {
		this.context = context;
		httpFetcher = new HttpFetcher(context);
		contentParser = new OpenPlatformJSONParser(context);		
	}

	
	@Override
	public ArticleBundle getArticles(ArticleSet articleSet, List<Section> sections, ArticleCallback articleCallback, int pageSize) {		
		Log.i(TAG, "Fetching articles for: " + articleSet.getName());		
		final String apiUrl = articleSet.getApiUrl();
		
		InputStream input = null;		
		if (input == null) {
			Log.i(TAG, "Fetching article set from live api: " + apiUrl);
			announceDownloadStarted(articleSet.getName());
			input = getHttpInputStream(buildContentQueryUrl(articleSet, true, pageSize));
		}		
		
		if (input != null) {
			List<Article> articles = contentParser.parseArticlesXml(input, sections, articleCallback);			
			if (articles != null && !articles.isEmpty()) {
				return new ArticleBundle(articles, contentParser.getRefinements(), contentParser.getChecksum(), DateTimeHelper.now(), contentParser.getDescription());
			}
		}
		return null;
	}
	
	
	private void announceDownloadStarted(String url) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_STARTED);
		intent.putExtra("url", url);
		context.sendBroadcast(intent);
	}
	

	@Override
	public String getRemoteChecksum(ArticleSet articleSet) {
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);	// TODO page size moves up to argument
		final String pageSizeString = prefs.getString("pageSize", "10");
		int pageSize = Integer.parseInt(pageSizeString);
		
		InputStream input = null;
		if (input == null) {
			Log.i(TAG, "Fetching article set checksum from live api: " + articleSet.getApiUrl());
			input = getHttpInputStream(buildContentQueryUrl(articleSet, false, pageSize));
		}		
		
		if (input != null) {
			contentParser.parseArticlesXml(input, null, null);
			return contentParser.getChecksum();			
		}
		return null;
	}


	@Override
	public List<Section> getSections() {		
		InputStream input = null;		
		if (input == null) {
			Log.i(TAG, "Fetching section list from live api");
			input = getHttpInputStream(buildSectionsQueryUrl());
		}
		
		if (input != null) {
			OpenPlatformJSONParser jsonParser = new OpenPlatformJSONParser(context);
			List<Section> sections = jsonParser.parseSectionsJSON(input);
			if (sections != null) {				
				return stripJunkSections(sections);
			}
		}
		return null;
	}
	
	
	@Override
	public void stopLoading() {
		contentParser.stop();
		httpFetcher.stopLoading();
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

	
	protected String buildContentQueryUrl(ArticleSet articleSet, boolean showAll, int pageSize) {		
		StringBuilder url = new StringBuilder(API_HOST + "/search");
		if (articleSet instanceof FavouriteStoriesArticleSet) {
			 url = new StringBuilder(API_HOST + "/favourites");
		}
		
		url.append("?format=xml");
		
		if (articleSet instanceof SectionArticleSet) {
			url.append("&section=" + articleSet.getApiUrl());			
		}
		
		if (articleSet instanceof KeywordArticleSet) {
			url.append("&tag=" + articleSet.getApiUrl());			
		}
		
		if (articleSet instanceof FavouriteStoriesArticleSet) {
			 url.append(articleSet.getApiUrl());
		}

		if (showAll) {
			url.append("&show-fields=true");	// TODO is all in api
			url.append("&show-tags=true");
		}
		url.append("&page-size=" + pageSize);
		return url.toString();
	}
	
	
	private InputStream getHttpInputStream(String url) {
		return httpFetcher.httpFetch(url);		
	}
		
}
