package nz.gen.wellington.guardian.android.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.api.caching.FileBasedSectionCache;
import nz.gen.wellington.guardian.android.api.caching.FileService;
import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;
import nz.gen.wellington.guardian.android.dates.DateTimeHelper;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ArticleDAO {
		
	private static final String TAG = "ArticleDAO";
	
	InMemorySectionCache sectionCache;
	FileBasedArticleCache fileBasedArticleCache;
	FileBasedSectionCache fileBasedSectionCache;
	ArticleCallback articleCallback;
	ContentSource openPlatformApi;
	NetworkStatusService networkStatusService;
	private Context context;
	
	public ArticleDAO(Context context) {
		this.sectionCache = CacheFactory.getSectionCache();
		this.fileBasedArticleCache = new FileBasedArticleCache(context);
		this.fileBasedSectionCache = new FileBasedSectionCache(context);
		openPlatformApi = ArticleDAOFactory.getOpenPlatformApi(context);
		this.networkStatusService = new NetworkStatusService(context);
		this.context = context;
	}
	
	
	public List<Section> getSections() {
		 List<Section> sections = fileBasedSectionCache.getSections();
		 if (sections != null) {
			 return sections;
		 }
		 
		 sections = openPlatformApi.getSections();
		 if (sections != null) {
			Log.i(TAG, "Found " + sections.size() + " sections");
			sectionCache.addAll(sections);
			fileBasedSectionCache.putSections(sections);
		}
		return sections;
	}
	
	
	public ArticleBundle getArticleSetArticles(ArticleSet articleSet, boolean uncached) {
		Log.i(TAG, "Retrieving articles for article set: " + articleSet.getName());
		
		ArticleBundle bundle = null;
		if (!uncached) {
			bundle = fileBasedArticleCache.getArticleSetArticles(articleSet, articleCallback);		
			if (bundle != null) {
				Log.i(TAG, "Got file cache hit for article set: " + articleSet.getName());
				return bundle;
			}		
		}
		
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
		final String pageSizeString = prefs.getString("pageSize", "10");
		int pageSize = Integer.parseInt(pageSizeString);
		
		List<Section> sections = this.getSections();
		if (sections != null) {
			bundle = openPlatformApi.getArticles(articleSet, sections, articleCallback, pageSize);		
			if (bundle != null) {
				Log.i(TAG, "Got article bundle from api call");
				fileBasedArticleCache.putArticleSetArticles(articleSet, bundle);
				return bundle;
				
			} else {
				Log.w(TAG, "Article api call failed");
			}
		}
		return null;
	}

	
	public String getArticleSetRemoteChecksum(ArticleSet articleSet) {
		return openPlatformApi.getRemoteChecksum(articleSet);
	}
	
	
	public void evictSections() {
		sectionCache.clear();
		fileBasedSectionCache.clear();
	}
	
	public void clearExpiredCacheFiles(Context context) {
		FileService.clearExpiredCacheFiles(context);
	}
	
	public void evictAll() {
		fileBasedArticleCache.clear();
	}
	
	
	public void evictArticleSet(ArticleSet articleSet) {
		fileBasedArticleCache.clear(articleSet);
	}
	
	public void saveTopStories(List<Article> topStories) {
		fileBasedArticleCache.putArticleSetArticles(new TopStoriesArticleSet(), new ArticleBundle(topStories, null, null, DateTimeHelper.now(), null));
	}

	public Date getModificationTime(ArticleSet articleSet) {
		return fileBasedArticleCache.getModificationTime(articleSet);
	}


	public void stopLoading() {
		Log.i(TAG, "Stopping loading");
		openPlatformApi.stopLoading();
	}


	public void setArticleReadyCallback(ArticleCallback articleCallback) {
		this.articleCallback = articleCallback;		
	}


	public Map<String, Section> getSectionsMap() {
		Map<String, Section> sectionsMap = new HashMap<String, Section>();
		List<Section> sections = this.getSections();
		if (sections != null) {
			for (Section section : sections) {
				sectionsMap.put(section.getId(), section);
			}
		}
		return sectionsMap;
	}


	public void touchFile(ArticleSet articleSet) {
		FileService.touchFile(context, articleSet.getApiUrl());		
	}
		
}
