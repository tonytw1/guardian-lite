package nz.gen.wellington.guardian.android.api.caching;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.content.Context;
import android.util.Log;

public class FileBasedArticleCache {
	
	private static final String TAG = "FileBasedArticleCache";

	private Context context;
	
	public FileBasedArticleCache(Context context) {
		this.context = context;
	}

	
	public void putArticleSetArticles(ArticleSet articleSet, List<Article> articles) {		
		Log.i(TAG, "Writing to disk: " + articleSet.getName());
		try {	
			FileOutputStream fos = FileService.getFileOutputStream(context, articleSet.getApiUrl());	
			ObjectOutputStream out = new ObjectOutputStream(fos);			
			out.writeObject(articles);
			out.close();			
		} catch (IOException ex) {
			Log.e(TAG, "IO Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		}
	}
		
	
	@SuppressWarnings("unchecked")
	public List<Article> getArticleSetArticles(ArticleSet articleSet) {
		if (!FileService.isLocallyCached(context, articleSet.getApiUrl())) {
			return null;
		}
		
		final String filepath = FileService.getLocalFilename(articleSet.getApiUrl());
		Log.i(TAG, "Reading from disk: " + filepath);
		try {
			FileInputStream fis = FileService.getFileInputStream(context, articleSet.getApiUrl());
			ObjectInputStream in = new ObjectInputStream(fis);
			List<Article> loaded = (List<Article>) in.readObject();
			in.close();
			return loaded;
			
		} catch (IOException ex) {
			Log.e(TAG, "IO Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Exception while writing article set: " + articleSet.getName() + ex.getMessage());
		}
		return null;
	}
	
	
	// TODO only clear article json files
	// TODO move to file service?
	public void clear() {
		Log.i(TAG, "Clearing all cache files");
		File cacheDir = context.getCacheDir();
		if (cacheDir == null) {
			Log.i(TAG, "No cache folder found");
			return;
		}
		Log.i(TAG, "Cache dir path is: " + cacheDir.getPath());
		Log.i(TAG, "Cache dir absolute path is: " + cacheDir.getAbsolutePath());
		
		File[] listFiles = cacheDir.listFiles();
		Log.i(TAG, "Cache dir file count: " + listFiles.length);
		for (int i = 0; i < listFiles.length; i++) {
			File cacheFile = listFiles[i];
			if (cacheFile.getPath().endsWith("json")) {	// TODO this is abit of a hack to preserve images.
				Log.i(TAG, "Found cache file: " + cacheFile.getAbsolutePath());
				if (cacheFile.delete()) {
					Log.i(TAG, "Deleted cache file: " + cacheFile.getAbsolutePath());				
				}
			}
		}
	}

	
	public void clear(ArticleSet articleSet) {
		Log.i(TAG, "Clearing article set: " + articleSet.getName());
		if (FileService.isLocallyCached(context, articleSet.getApiUrl())) {
			FileService.clear(context, articleSet.getApiUrl());
		} else {
			Log.i(TAG, "No local copy to clear:" + articleSet.getApiUrl());
		}
	}
		
}
