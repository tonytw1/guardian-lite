package nz.gen.wellington.guardian.android.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.api.caching.FileBasedSectionCache;
import nz.gen.wellington.guardian.android.api.caching.FileService;
import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.AuthorArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;

import org.joda.time.DateTime;

import android.content.Context;
import android.util.Log;

public class ArticleDAO {
		
	private static final String TAG = "ArticleDAO";
	
	InMemorySectionCache sectionCache;
	FileBasedArticleCache fileBasedArticleCache;
	FileBasedSectionCache fileBasedSectionCache;
	ArticleCallback articleCallback;
	ContentSource openPlatformApi;
	NetworkStatusService networkStatusService;
	
	public ArticleDAO(Context context) {
		this.sectionCache = CacheFactory.getSectionCache();
		this.fileBasedArticleCache = new FileBasedArticleCache(context);
		this.fileBasedSectionCache = new FileBasedSectionCache(context);
		openPlatformApi = ArticleDAOFactory.getOpenPlatformApi(context);
		this.networkStatusService = new NetworkStatusService(context);
	}
	
	
	public List<Article> getSectionItems(Section section) {
		ArticleSet sectionArticleSet = new SectionArticleSet(section);
		return getArticleSetArticles(sectionArticleSet);
	}
	
	
	public List<Article> getAuthorItems(Tag author) {
		ArticleSet authorArticleSet = new AuthorArticleSet(author);
		return getArticleSetArticles(authorArticleSet);
	}
	
	public List<Article> getKeywordItems(Tag keyword) {
		ArticleSet keywordArticleSet = new KeywordArticleSet(keyword);
		return getArticleSetArticles(keywordArticleSet);
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
	
	
	private List<Article> getArticleSetArticles(ArticleSet articleSet) {
		Log.i(TAG, "Retrieving articles for article set: " + articleSet.getName());
		
		List<Article> articles = null;
		DateTime modificationTime = fileBasedArticleCache.getModificationTime(articleSet);
		if (modificationTime != null) {
			if (networkStatusService.isConnectionAvailable() && modificationTime.isBefore(new DateTime().minusMinutes(10))) {
				Log.i(TAG, "Checking remote checksum local copy is older than 10 minutes and network is available");
				
				ArticleBundle bundle = fileBasedArticleCache.getArticleSetArticles(articleSet, articleCallback);
				if (bundle != null) {
					articles = bundle.getArticles();
					String localChecksum = bundle.getChecksum();
					String remoteChecksum = this.getArticleSetRemoteChecksum(articleSet);	// TODO this should happen after articles loaded.
					if (localChecksum != null && !localChecksum.equals(remoteChecksum)) {						
						Log.i(TAG, "Remove content checksum is different: " + localChecksum + ":" + remoteChecksum);
					}
				}				
				
			} else {
				ArticleBundle bundle = fileBasedArticleCache.getArticleSetArticles(articleSet, articleCallback);
				if (bundle != null) {
					articles = bundle.getArticles();
				}
			}
		}
		
		if (articles != null) {
			Log.i(TAG, "Got file cache hit for article set: " + articleSet.getName());
			return articles;
		}
		
		List<Section> sections = this.getSections();
		if (sections != null) {
			ArticleBundle bundle = openPlatformApi.getArticles(articleSet, sections, articleCallback);		
			if (articles != null) {
				Log.i(TAG, "Got " + articles.size() + " articles from api call");
				fileBasedArticleCache.putArticleSetArticles(articleSet, bundle);
				return bundle.getArticles();
				
			} else {
				Log.w(TAG, "Article api call failed");
			}
		}
		return null;
	}

	
	private String getArticleSetRemoteChecksum(ArticleSet articleSet) {
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


	public List<Article> getTopStories() {
		return getArticleSetArticles(new TopStoriesArticleSet());
	}
	
	public List<Article> getFavouriteArticles(List<Section> favouriteSections, List<Tag> favouriteTags) {
		return getArticleSetArticles(new FavouriteStoriesArticleSet(favouriteSections, favouriteTags));
	}
		
	public void saveTopStories(List<Article> topStories) {
		fileBasedArticleCache.putArticleSetArticles(new TopStoriesArticleSet(), new ArticleBundle(topStories, null, null));		
	}

	public DateTime getModificationTime(ArticleSet articleSet) {
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
		
}
