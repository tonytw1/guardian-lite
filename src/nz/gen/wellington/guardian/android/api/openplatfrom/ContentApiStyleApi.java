package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.dates.DateTimeHelper;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ContentApiStyleApi implements ContentSource {
		
	private static final String TAG = "ContentApiStyleApi";

	private static final String API_HOST = "http://guardian-lite.appspot.com";	
	public static final String SECTIONS_API_URL = "sections";
	
	private ContentApiStyleXmlParser contentXmlParser;
	private ContentApiStyleJSONParser contentJsonParser;
	private HttpFetcher httpFetcher;

	private Context context;

	
	public ContentApiStyleApi(Context context) {
		this.context = context;
		httpFetcher = new HttpFetcher(context);
		contentXmlParser = new ContentApiStyleXmlParser(context);
		contentJsonParser = new ContentApiStyleJSONParser();
	}

	
	@Override
	public ArticleBundle getArticles(ArticleSet articleSet, List<Section> sections, ArticleCallback articleCallback, int pageSize) {		
		//Log.i(TAG, "Fetching articles for: " + articleSet.getName());		
		//final String apiUrl = articleSet.getApiUrl();
		
		InputStream input = null;		
		if (input == null) {
			//Log.i(TAG, "Fetching article set from live api: " + apiUrl);
			announceDownloadStarted(articleSet.getName() + " article set");
			input = getHttpInputStream(buildContentQueryUrl(articleSet, true, pageSize));
		}		
		
		if (input != null) {
			List<Article> articles = contentXmlParser.parseArticlesXml(input, sections, articleCallback);			
			if (articles != null && !articles.isEmpty()) {
				return new ArticleBundle(articles, contentXmlParser.getRefinements(), contentXmlParser.getChecksum(), DateTimeHelper.now(), contentXmlParser.getDescription());
			}
		}
		return null;
	}
	
	
	private void announceDownloadStarted(String downloadName) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_STARTED);
		intent.putExtra("url", downloadName);
		context.sendBroadcast(intent);
	}
	

	@Override
	public String getRemoteChecksum(ArticleSet articleSet, int pageSize) {		
		InputStream input = null;
		if (input == null) {
			//Log.i(TAG, "Fetching article set checksum from live api: " + articleSet.getApiUrl());
			announceDownloadStarted(articleSet.getName() + " article set checksum");
			input = getHttpInputStream(buildContentQueryUrl(articleSet, false, pageSize));
		}		
		
		if (input != null) {
			contentXmlParser.parseArticlesXml(input, null, null);
			return contentXmlParser.getChecksum();			
		}
		return null;
	}


	@Override
	public List<Section> getSections() {		
		InputStream input = null;		
		if (input == null) {
			//Log.i(TAG, "Fetching section list from live api");
			input = getHttpInputStream(buildSectionsQueryUrl());
		}
		
		if (input != null) {
			return contentJsonParser.parseSectionsJSON(input);
		}
		return null;
	}
	
		
	@Override
	public List<Tag> searchTags(String searchTerm, Map<String, Section> sections) {
		InputStream input = null;		
		if (input == null) {
			Log.i(TAG, "Fetching tag list from live api: " + searchTerm);
			announceDownloadStarted("tag results");
			input = getHttpInputStream(buildTagSearchQueryUrl(searchTerm, 20));
		}
		if (input != null) {
			return contentJsonParser.parseTagsJSON(input, sections);
		}
		return null;
	}


	@Override
	public void stopLoading() {
		contentXmlParser.stop();
		httpFetcher.stopLoading();
	}
	
		
	private String buildSectionsQueryUrl() {
		StringBuilder url = new StringBuilder(API_HOST + "/" + SECTIONS_API_URL);
		url.append("?format=json");
		return url.toString();
	}
	
	
	protected String buildTagSearchQueryUrl(String searchTerm, int pageSize) {		
		StringBuilder url = new StringBuilder("http://content.guardianapis.com" + "/tags");	// TODO proxy!
		url.append("?format=json");
		url.append("&page-size=" + pageSize);
		url.append("&type=keyword%2Ccontributor%2Cblog");	// TODO push to allowed types constant somewhere
		url.append("&q=" + URLEncoder.encode(searchTerm));		
		return url.toString();
	}

	
	protected String buildContentQueryUrl(ArticleSet articleSet, boolean showAll, int pageSize) {		
		StringBuilder url = new StringBuilder(API_HOST + "/search");
		if (articleSet instanceof FavouriteStoriesArticleSet) {
			 url = new StringBuilder(API_HOST + "/favourites");
		}
		if (articleSet instanceof AboutArticleSet) {
			 url = new StringBuilder(API_HOST + "/about");
		}
		
		url.append("?format=xml");
		
		if (articleSet instanceof SectionArticleSet) {
			url.append("&section=" + articleSet.getApiUrl());			
		}
		
		if (articleSet instanceof TagArticleSet) {
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
