package nz.gen.wellington.guardian.android.api;

import java.util.Date;
import java.util.List;

import nz.gen.wellington.guardian.android.about.AboutArticlesDAO;
import nz.gen.wellington.guardian.android.about.ArticleSource;
import nz.gen.wellington.guardian.android.about.SavedArticlesDAO;
import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import nz.gen.wellington.guardian.android.utils.DateTimeHelper;
import android.content.Context;
import android.util.Log;

public class ArticleDAO {
		
	private static final String TAG = "ArticleDAO";
	
	private FileBasedArticleCache fileBasedArticleCache;
	private ArticleCallback articleCallback;
	private SectionDAO sectionsDAO;
	private PreferencesDAO preferencesDAO;
	private NetworkStatusService networkStatusService;
	private ArticleSource aboutArticlesDAO;
	private ArticleSource savedArticlesDAO;
	private Context context;
	private ContentSource activeContentSource;
	
	public ArticleDAO(Context context) {
		this.context = context;
		fileBasedArticleCache = new FileBasedArticleCache(context);		
		sectionsDAO = SingletonFactory.getSectionDAO(context);
		preferencesDAO = SingletonFactory.getPreferencesDAO(context);
		networkStatusService = new NetworkStatusService(context);
		aboutArticlesDAO = new AboutArticlesDAO(context);
		savedArticlesDAO = new SavedArticlesDAO(context);
	}
	
	public boolean isAvailable(ArticleSet articleSet) {
		return fileBasedArticleCache.isLocallyCached(articleSet) || networkStatusService.isConnectionAvailable();
	}
	
	public ArticleBundle getArticleSetArticles(ArticleSet articleSet, ContentFetchType fetchType) {
		Log.i(TAG, "Retrieving articles for article set: " + articleSet.getName() + " (" + fetchType.name() + ")");
		
		if (fetchType.equals(ContentFetchType.LOCAL_ONLY)) {
			return getLocalBundle(articleSet);
		}
		
		if (fetchType.equals(ContentFetchType.UNCACHED)) {
			return fetchFromLive(articleSet);	
		}
		
		if (fetchType.equals(ContentFetchType.CHECKSUM)) {
			ArticleBundle localCopy = fileBasedArticleCache.getArticleSetArticles(articleSet, null);
			if (localCopy != null && localCopy.getChecksum() != null) {				
				Log.i(TAG, "Checking for checksum sync - local article set has checksum: " + localCopy.getChecksum());
				final String remoteChecksum = getArticleSetRemoteChecksum(articleSet);
				Log.i(TAG, "Remote checksum is: " + remoteChecksum);
				boolean checksumsMatch = remoteChecksum != null && remoteChecksum.equals(localCopy.getChecksum());
				if (checksumsMatch) {
					Log.i(TAG, "Remote checksum matches local copy. Not refetching");
					fileBasedArticleCache.touchArticleSet(articleSet, DateTimeHelper.now());
					return getLocalBundle(articleSet);		// TODO duplicate read, for the proposes of triggering the call back only is abit rubbish. Should be able to reset on localcopy bundle
					
				} else {
					return fetchFromLive(articleSet);								
				}
				
			} else {
				Log.i(TAG, "No checksumed local copy available - fetching from live");
				return fetchFromLive(articleSet);
			}
		}
		
		if (fetchType.equals(ContentFetchType.NORMAL)) {
			ArticleBundle localCopy = getLocalBundle(articleSet);
			if (localCopy != null) {
				return localCopy;
			} else {
				return fetchFromLive(articleSet);
			}			
		}
		
		return null;
	}

		
	public String getArticleSetRemoteChecksum(ArticleSet articleSet) {
		ContentSource openPlatformApi = getContentSource();
		return openPlatformApi.getRemoteChecksum(articleSet, preferencesDAO.getPageSizePreference());		
	}
	
		
	private ArticleBundle getLocalBundle(ArticleSet articleSet) {
		return fileBasedArticleCache.getArticleSetArticles(articleSet, articleCallback);
	}
	
		
	private ArticleBundle fetchFromLive(ArticleSet articleSet) {
		Log.i(TAG, "Fetching from live");
		
		ArticleBundle bundle = null;
		if (articleSet instanceof AboutArticleSet) {
			bundle = aboutArticlesDAO.getArticles(articleSet, articleCallback);

		} else if (articleSet instanceof SavedArticlesArticleSet) {
			bundle = savedArticlesDAO.getArticles(articleSet, articleCallback);

		} else {
			List<Section> sections = sectionsDAO.getSections();
			if (sections != null) {
				ContentSource openPlatformApi = getContentSource();
				bundle = openPlatformApi.getArticles(articleSet, sections, articleCallback);
			}
		}
		
		if (bundle != null) {
			fileBasedArticleCache.putArticleSetArticles(articleSet, bundle);
			return bundle;
		}		
		return null;
	}

	private ContentSource getContentSource() {
		ContentSource openPlatformApi = SingletonFactory.getOpenPlatformApi(context);
		activeContentSource = openPlatformApi;
		return openPlatformApi;		
	}

	public void clearExpiredCacheFiles(Context context) {
		Log.i(TAG, "Purging expired content");
		fileBasedArticleCache.clearExpiredFiles(context);
	}
	
	public void evictArticleSet(ArticleSet articleSet) {
		fileBasedArticleCache.clear(articleSet);
	}
	
	
	public Date getModificationTime(ArticleSet articleSet) {
		return fileBasedArticleCache.getModificationTime(articleSet);
	}


	public void stopLoading() {
		if (activeContentSource != null) {
			activeContentSource.stopLoading();
		}
	}
	
	public void setArticleReadyCallback(ArticleCallback articleCallback) {
		this.articleCallback = articleCallback;		
	}
	
}
