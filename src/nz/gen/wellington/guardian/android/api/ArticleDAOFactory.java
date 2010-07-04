package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.openplatfrom.OpenPlatformJSONApi;
import nz.gen.wellington.guardian.android.services.TaskQueue;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ArticleDAOFactory {

	private static TaskQueue taskQueue;
	private static ImageDAO imageDAO;
	
	public static ArticleDAO getDao(Context context) {
		return new ArticleDAO(context);	
	}
	
	public static ContentSource getOpenPlatformApi(Context context) {
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
		final String pageSizeString = prefs.getString("pageSize", "10");
		int pageSize = Integer.parseInt(pageSizeString);
		return new OpenPlatformJSONApi(context, pageSize);
	}
	
	public static TaskQueue getTaskQueue(Context context) {
		if (taskQueue == null) {
			taskQueue = new TaskQueue(context);
		}
		return taskQueue;		
	}

	public static ImageDAO getImageDao(Context context) {
		if (imageDAO == null) {
			imageDAO = new ImageDAO(context);
		}
		return imageDAO;	
	}
	
}
