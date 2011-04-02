/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.api;

import java.util.Date;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.content.ArticleSetDAO;
import nz.gen.wellington.guardian.android.content.ArticleSource;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.utils.DateTimeHelper;
import android.content.Context;
import android.util.Log;

public class ArticleDAO {
		
	private static final String TAG = "ArticleDAO";
	
	private FileBasedArticleCache fileBasedArticleCache;
	private ArticleCallback articleCallback;
	private NetworkStatusService networkStatusService;
	private ArticleSource articleSetDAO;
	private Context context;
	private ContentSource activeContentSource;
	
	public ArticleDAO(Context context) {
		this.context = context;
		fileBasedArticleCache = new FileBasedArticleCache(context);		
		networkStatusService = SingletonFactory.getNetworkStatusService(context);
		articleSetDAO = new ArticleSetDAO(context);
	}
	
	public boolean isAvailable(ArticleSet articleSet) {
		return fileBasedArticleCache.isLocallyCached(articleSet) || networkStatusService.isConnectionAvailable();
	}
	
	public ArticleBundle getArticleSetArticles(ArticleSet articleSet, ContentFetchType fetchType) {
		Log.i(TAG, "Retrieving articles for article set: " + articleSet.getName() + " (" + fetchType.name() + ")");
		
		if (articleSet.isEmpty()) {
			return null;
		}
		
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
		return openPlatformApi.getRemoteChecksum(articleSet);
	}
	
	
	private ArticleBundle getLocalBundle(ArticleSet articleSet) {
		return fileBasedArticleCache.getArticleSetArticles(articleSet, articleCallback);
	}
	
		
	// TODO Not thread safe
	private ArticleBundle fetchFromLive(ArticleSet articleSet) {
		Log.i(TAG, "Fetching from live");
		
		ArticleBundle bundle = null;
		if (articleSet instanceof AboutArticleSet) {
			bundle = articleSetDAO.getArticles(articleSet, articleCallback);

		} else if (articleSet instanceof SavedArticlesArticleSet) {
			bundle = articleSetDAO.getArticles(articleSet, articleCallback);
			
		} else {
			ContentSource openPlatformApi = getContentSource();
			bundle = openPlatformApi.getArticles(articleSet, articleCallback);			
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
