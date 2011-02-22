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

package nz.gen.wellington.guardian.android.api.caching;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.model.Article;
import android.content.Context;
import android.util.Log;

public class FileBasedArticleCache {
	
	private static final String TAG = "FileBasedArticleCache";
	private static final String VERSION_SUFFIX = "v9";

	private Context context;
	
	public FileBasedArticleCache(Context context) {
		this.context = context;
	}
	
	
	public void putArticleSetArticles(ArticleSet articleSet, ArticleBundle bundle) {
		 Log.d(TAG, "Writing to disk '" + articleSet.getName() + "' with checksum: " + bundle.getChecksum());
		 try {
			 FileOutputStream fos = FileService.getFileOutputStream(context, getLocalFilename(getUrlFor(articleSet)));
			 ObjectOutputStream out = new ObjectOutputStream(fos);
			 out.writeObject(bundle);
			 out.close();
			 
		 } catch (IOException ex) {
			 Log.e(TAG, "IO Exception while writing article set: " + articleSet.getName());
			 Log.e(TAG, ex.getMessage());
		 }
		 Log.d(TAG, "Finished writing to disk '" + articleSet.getName());
	}
	
	public void touchArticleSet(ArticleSet articleSet, Date modTime) {
		FileService.touchFile(context, getLocalFilenameForArticleSet(articleSet), modTime);
	}	 
	
	
	public boolean isLocallyCached(ArticleSet articleSet) {
		boolean locallyCached = FileService.existsLocally(context, getLocalFilenameForArticleSet(articleSet));
		if (locallyCached) {
			Log.i(TAG, "Article set '" + articleSet.getName() + "' is locally cached");
		}
		return locallyCached;
	}
	 
	public ArticleBundle getArticleSetArticles(ArticleSet articleSet, ArticleCallback articleCallback) {
		if (!isLocallyCached(articleSet)) {
			return null;
		}
		
		Log.i(TAG, "Reading from disk: " + articleSet.getName());
		try {
			final String localFilename = getLocalFilenameForArticleSet(articleSet);
			FileInputStream fis = FileService.getFileInputStream(context, localFilename);
			ObjectInputStream in = new ObjectInputStream(fis);
			ArticleBundle loaded = (ArticleBundle) in.readObject();
			in.close();
			
			Log.i(TAG, "Finished reading from disk: " + articleSet.getName());
			if (loaded != null) {								
				if (articleCallback != null) {
					for (Article article : loaded.getArticles()) {
						articleCallback.articleReady(article);
					}
				}
					
				Log.i(TAG, "Loaded " + loaded.getArticles().size() + " articles");
				Log.i(TAG, "Content checksum is: " + loaded.getChecksum());
				return loaded;

			} else {
				Log.w(TAG, "Article bundle was null after read attempt");
			}
			return null;

		} catch (IOException ex) {
			Log.e(TAG, "IO Exception while reading article set: " + articleSet.getName() + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Exception while reading article set: " + articleSet.getName() + ex.getMessage());
		}
		return null;
	}
	
	public void clearExpiredFiles(Context context) {
		FileService.clearExpiredCacheFiles(context);
	}
	
	public void clear(ArticleSet articleSet) {
		Log.i(TAG, "Clearing article set: " + articleSet.getName());
		final String localFilenameForArticleSet = getLocalFilenameForArticleSet(articleSet);
		if (FileService.existsLocally(context, localFilenameForArticleSet)) {
			FileService.clear(context,  localFilenameForArticleSet);
		} else {
			Log.i(TAG, "No local copy to clear:" + articleSet.getName());
		}
	}

	public Date getModificationTime(ArticleSet articleSet) {
		return FileService.getModificationTime(context, getLocalFilenameForArticleSet(articleSet));
	}
	
	private String getLocalFilenameForArticleSet(ArticleSet articleSet) {
		return getLocalFilename(getUrlFor(articleSet));
	}
	
	private String getUrlFor(ArticleSet articleSet) {
		return articleSet.getSourceUrl();
	}
	
	private String getLocalFilename(String url) {
		Log.d(TAG, "Getting local filename for url: " + url);
		return FileCacheLocalFilenameService.getLocalFilenameFor(url) + VERSION_SUFFIX;
	}
	
}
