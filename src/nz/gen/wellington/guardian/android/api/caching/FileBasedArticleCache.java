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
		final String filepath = this.getLocalFilename(articleSet.getApiUrl());		
		Log.i(TAG, "Writing to disk: " + filepath);
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = context.openFileOutput(filepath, Context.MODE_PRIVATE);
			out = new ObjectOutputStream(fos);
			out.writeObject(articles);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
		
	@SuppressWarnings("unchecked")
	public List<Article> getArticleSetArticles(ArticleSet articleSet) {
		if (!isLocallyCached(articleSet.getApiUrl())) {
			return null;
		}
		
		final String filepath = this.getLocalFilename(articleSet.getApiUrl());
		Log.i(TAG, "Reading from disk: " + filepath);

		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = context.openFileInput(filepath);
			in = new ObjectInputStream(fis);
			List<Article> loaded = (List<Article>) in.readObject();
			in.close();
			return loaded;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	public void clear(ArticleSet articleSet) {
		Log.i(TAG, "Clearing article set: " + articleSet.getName());
		final String filepath = getLocalFilename(articleSet.getApiUrl());
		if (isLocallyCached(articleSet.getApiUrl())) {
			File localFile = context.getFileStreamPath(filepath);			
			localFile.delete();
			Log.i(TAG, "Cleared: " + filepath);
		} else {
			Log.i(TAG, "No local copy to clear:" + filepath);
		}
	}
	
	private boolean isLocallyCached(String apiUrl) {
		File localFile = context.getFileStreamPath(getLocalFilename(apiUrl));
		return localFile.exists() && localFile.canRead();
	}
		
	protected String getLocalFilename(String apiUrl) {
		return apiUrl.replaceAll("/", "").replaceAll(":", "");
	}
	
}
