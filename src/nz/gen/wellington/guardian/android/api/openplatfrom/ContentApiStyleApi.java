package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.network.LoggingBufferedInputStream;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ContentApiStyleApi implements ContentSource {
		
	private static final String TAG = "ContentApiStyleApi";
	
	private ContentApiStyleXmlParser contentXmlParser;
	private ContentApiStyleJSONParser contentJsonParser;
	PreferencesDAO preferencesDAO;
	private HttpFetcher httpFetcher;
	private ContentApiUrlService contentApiUrlService;

	private Context context;
	
	public ContentApiStyleApi(Context context) {
		this.context = context;
		httpFetcher = new HttpFetcher(context);
		contentXmlParser = new ContentApiStyleXmlParser(context);
		contentJsonParser = new ContentApiStyleJSONParser();		
		contentApiUrlService = new ContentApiUrlService(context);		
		preferencesDAO = SingletonFactory.getPreferencesDAO(context);		
	}

	
	@Override
	public ArticleBundle getArticles(ArticleSet articleSet, List<Section> sections, ArticleCallback articleCallback, int pageSize) {
		Log.i(TAG, "Fetching articles for: " + articleSet.getName());
		
		final String contentApiUrl = contentApiUrlService.getContentApiUrlForArticleSet(articleSet);
		
		announceDownloadStarted(articleSet.getName() + " article set");
		LoggingBufferedInputStream input = getHttpInputStream(contentApiUrl);
		if (input != null) {
			List<Article> articles = contentXmlParser.parseArticlesXml(input, articleCallback);
			if (articles != null && !articles.isEmpty()) {
				String checksum = input.getEtag();
				return new ArticleBundle(articles, contentXmlParser.getRefinements(), checksum, contentXmlParser.getDescription());
			}
		}
		return null;
	}
	
	
	@Override
	public String getRemoteChecksum(ArticleSet articleSet, int pageSize) {		
		Log.i(TAG, "Fetching article set checksum for article set: " + articleSet.getName());
		
		String contentApiUrl = contentApiUrlService.getContentApiUrlForArticleSetChecksum(articleSet);
		
		announceDownloadStarted(articleSet.getName() + " article set checksum");		
		InputStream input = getHttpInputStream(contentApiUrl);		
		if (input != null) {
			contentXmlParser.parseArticlesXml(input, null);
			return contentXmlParser.getChecksum();			
		}
		return null;
	}


	@Override
	public List<Section> getSections() {
		Log.i(TAG, "Fetching section list from live api");
		String contentApiUrl = contentApiUrlService.getSectionsQueryUrl();
		InputStream input = getHttpInputStream(contentApiUrl);
		if (input != null) {
			return contentJsonParser.parseSectionsJSON(input);
		}
		return null;
	}
	
	
	@Override
	public List<Tag> searchTags(String searchTerm, Map<String, Section> sections) {
		Log.i(TAG, "Fetching tag list from live api: " + searchTerm);
		announceDownloadStarted("tag results");
		InputStream input = getHttpInputStream(contentApiUrlService.getTagSearchQueryUrl(searchTerm));
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
	
	private void announceDownloadStarted(String downloadName) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_STARTED);
		intent.putExtra("url", downloadName);
		context.sendBroadcast(intent);
	}
	
	private LoggingBufferedInputStream getHttpInputStream(String url) {
		return httpFetcher.httpFetch(url);		
	}
		
}
