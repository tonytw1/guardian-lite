package nz.gen.wellington.guardian.android.api.caching;

import nz.gen.wellington.guardian.android.model.ArticleSet;

import org.joda.time.DateTime;

import android.content.Context;
import android.util.Log;

public class FileBasedArticleCache {
	
	private static final String TAG = "FileBasedArticleCache";

	private Context context;
	
	public FileBasedArticleCache(Context context) {
		this.context = context;
	}	
	
	public void clear() {
		FileService.clearAll(context);
	}

	
	public void clear(ArticleSet articleSet) {
		Log.i(TAG, "Clearing article set: " + articleSet.getName());
		if (FileService.isLocallyCached(context, articleSet.getApiUrl())) {
			FileService.clear(context, articleSet.getApiUrl());
		} else {
			Log.i(TAG, "No local copy to clear:" + articleSet.getApiUrl());
		}
	}


	public DateTime getModificationTime(ArticleSet articleSet) {
		return new FileService().getModificationTime(context, articleSet.getApiUrl());
	}
		
}
