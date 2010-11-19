package nz.gen.wellington.guardian.android.api.caching;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiUrlService;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.content.Context;
import android.util.Log;

public class FileBasedArticleCache {
	
	private static final String TAG = "FileBasedArticleCache";

	private Context context;
	
	public FileBasedArticleCache(Context context) {
		this.context = context;
	}
	
	
	 public void putArticleSetArticles(ArticleSet articleSet, ArticleBundle bundle) {
		 //Log.i(TAG, "Writing to disk: " + articleSet.getName());
		 try {
			 FileOutputStream fos = FileService.getFileOutputStream(context, getLocalFileKeyForArticleSet(articleSet));
			 ObjectOutputStream out = new ObjectOutputStream(fos);
			 out.writeObject(bundle);
			 out.close();
		 } catch (IOException ex) {
			 //Log.e(TAG, "IO Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		 }
	 }
	 
	 
	 public void touchArticleSet(ArticleSet articleSet, Date modTime) {
		 FileService.touchFile(context, getLocalFileKeyForArticleSet(articleSet), modTime);
	 }
	 
	 
	 public boolean isLocallyCached(ArticleSet articleSet) {		 
		 String localFileKeyForArticleSet = getLocalFileKeyForArticleSet(articleSet);
		 boolean locallyCached = FileService.isLocallyCached(context, localFileKeyForArticleSet);
		 if (locallyCached) {
			 Log.i(TAG, "Article set '" + articleSet.getName() + "' is locally cached at: " + localFileKeyForArticleSet);
		 }
		return locallyCached;
	 }
	 
	 public ArticleBundle getArticleSetArticles(ArticleSet articleSet, ArticleCallback articleCallback) {
		if (!FileService.isLocallyCached(context, getLocalFileKeyForArticleSet(articleSet))) {
			return null;
		}
		
		String localFileKey = getLocalFileKeyForArticleSet(articleSet);
		try {
			FileInputStream fis = FileService.getFileInputStream(context, localFileKey);

			//Log.i(TAG, "Reading from disk: " + filepath);
			ObjectInputStream in = new ObjectInputStream(fis);
			ArticleBundle loaded = (ArticleBundle) in.readObject();
			in.close();
			//Log.i(TAG, "Finished reading from disk: " + filepath);
			if (loaded != null) {
								
				if (articleCallback != null) {
					for (Article article : loaded.getArticles()) {
						articleCallback.articleReady(article);
					}
				}
					
				//Log.i(TAG, "Loaded " + loaded.getArticles().size() + " articles");
				//Log.i(TAG, "Content checksum is: " + loaded.getChecksum());
				return loaded;
			}
			return null;

		} catch (IOException ex) {
			//Log.e(TAG, "IO Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			//Log.e(TAG, "Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		}
		return null;
	}
	
	
	public void clear() {
		FileService.clearAll(context);
	}
	
		
	public void clear(ArticleSet articleSet) {
		//Log.i(TAG, "Clearing article set: " + articleSet.getName());
		if (FileService.isLocallyCached(context, getLocalFileKeyForArticleSet(articleSet))) {
			FileService.clear(context, getLocalFileKeyForArticleSet(articleSet));
		} else {
			//Log.i(TAG, "No local copy to clear:" + articleSet.getApiUrl());
		}
	}


	public Date getModificationTime(ArticleSet articleSet) {
		return FileService.getModificationTime(context, getLocalFileKeyForArticleSet(articleSet));
	}
	
	
	
	private String getLocalFileKeyForArticleSet(ArticleSet articleSet) {
		ContentApiUrlService contentApiUrlService = new ContentApiUrlService(context);
		return contentApiUrlService.getContentApiUrlForArticleSet(articleSet);
	}
}
