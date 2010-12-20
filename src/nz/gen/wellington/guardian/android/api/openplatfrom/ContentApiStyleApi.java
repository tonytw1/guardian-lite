package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.ArticleSetUrlService;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
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
	private HttpFetcher httpFetcher;
	private ArticleSetUrlService articleSetUrlService;
	private Context context;
	private PreferencesDAO preferencesDAO;
	
	public ContentApiStyleApi(Context context) {
		this.context = context;
		this.contentXmlParser = new ContentApiStyleXmlParser(context);
		this.contentJsonParser = new ContentApiStyleJSONParser();
		this.articleSetUrlService = new ArticleSetUrlService(context);		
		this.preferencesDAO = SingletonFactory.getPreferencesDAO(context);
		this.httpFetcher = new HttpFetcher(context);
	}
	
	@Override
	public ArticleBundle getArticles(ArticleSet articleSet, List<Section> sections, ArticleCallback articleCallback) {
		Log.i(TAG, "Fetching articles for: " + articleSet.getName());
		
		final String contentApiUrl = articleSetUrlService.getUrlForArticleSet(articleSet);
		
		announceDownloadStarted(articleSet.getName() + " article set");
		LoggingBufferedInputStream input = httpFetcher.httpFetch(contentApiUrl);
		if (input != null) {
			ArticleBundle results = contentXmlParser.parseArticlesXml(input, articleCallback);
			if (results != null && !results.getArticles().isEmpty()) {
				String checksum = input.getEtag();
				results.setChecksum(checksum);
				return results;			
			}
		}
		return null;
	}
	
	
	@Override
	public String getRemoteChecksum(ArticleSet articleSet, int pageSize) {		
		Log.i(TAG, "Fetching article set checksum for article set: " + articleSet.getName());		
		final String contentApiUrl = articleSetUrlService.getUrlForArticleSet(articleSet);		
		announceDownloadStarted(articleSet.getName() + " article set checksum");		
		return httpFetcher.httpEtag(contentApiUrl);		
	}
	
	
	@Override
	public List<Section> getSections() {
		Log.i(TAG, "Fetching section list from live api");
		ContentApiUrlService contentApiUrlService = new ContentApiUrlService(preferencesDAO.getPreferedApiHost(), preferencesDAO.getApiKey());
		String contentApiUrl = contentApiUrlService.getSectionsQueryUrl();
		InputStream input = httpFetcher.httpFetch(contentApiUrl);
		if (input != null) {
			return contentJsonParser.parseSectionsJSON(input);
		}
		return null;
	}
	
	
	@Override
	public List<Tag> searchTags(String searchTerm, Map<String, Section> sections) {
		Log.i(TAG, "Fetching tag list from live api: " + searchTerm);
		announceDownloadStarted("tag results");
		ContentApiUrlService contentApiUrlService = new ContentApiUrlService(preferencesDAO.getPreferedApiHost(), preferencesDAO.getApiKey());
		InputStream input = httpFetcher.httpFetch(contentApiUrlService.getTagSearchQueryUrl(searchTerm));
		if (input != null) {
			return contentJsonParser.parseTagsJSON(input, sections);
		}
		return null;
	}


	@Override
	public void stopLoading() {
		Log.i(TAG, "Stopping content api loading");
		contentXmlParser.stop();
		httpFetcher.stopLoading();
	}
	
	private void announceDownloadStarted(String downloadName) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_STARTED);
		intent.putExtra("url", downloadName);
		context.sendBroadcast(intent);
	}
		
}
