package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.openplatfrom.OpenPlatformJSONApi;
import nz.gen.wellington.guardian.android.services.TaskQueue;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ArticleDAOFactory {

	private static TaskQueue taskQueue;
	private static ImageDAO imageDAO;
	private static OpenPlatformApiKeyStore apiKeyStore;
	
	public static ArticleDAO getDao(Context context) {
		return new ArticleDAO(context);	
	}
	
	public static ContentSource getOpenPlatformApi(Context context) {
		return new OpenPlatformJSONApi(context, getOpenPlatformApiKeyStore(context));
	}

		
	public static OpenPlatformApiKeyStore getOpenPlatformApiKeyStore(Context context) {
		if (apiKeyStore == null) {
			SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
			final String apiKey = prefs.getString("apikey", null);
			apiKeyStore = new OpenPlatformApiKeyStore(apiKey);
		}
		return apiKeyStore;
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
