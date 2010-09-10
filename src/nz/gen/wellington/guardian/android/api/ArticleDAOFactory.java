package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.openplatfrom.OpenPlatformJSONApi;
import nz.gen.wellington.guardian.android.services.TaskQueue;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;

public class ArticleDAOFactory {

	private static TaskQueue taskQueue;
	private static ImageDAO imageDAO;
	private static SectionDAO sectionDAO;
	private static FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
	private static PreferencesDAO preferencesDAO;
	
	public static ArticleDAO getDao(Context context) {
		return new ArticleDAO(context);	
	}
	
	public static ContentSource getOpenPlatformApi(Context context) {
		return new OpenPlatformJSONApi(context);
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
	
	public static FavouriteSectionsAndTagsDAO getFavouriteSectionsAndTagsDAO(Context context) {
		if (favouriteSectionsAndTagsDAO == null) {
			favouriteSectionsAndTagsDAO = new FavouriteSectionsAndTagsDAO(getSectionDAO(context), context);			
		}
		return favouriteSectionsAndTagsDAO;
	}

	public static SectionDAO getSectionDAO(Context context) {
		if (sectionDAO == null) {
			sectionDAO = new SectionDAO(context);
		}
		return sectionDAO;
	}

	public static PreferencesDAO getPreferencesDAO(Context context) {
		if (preferencesDAO == null) {
			preferencesDAO = new PreferencesDAO(context);
		}
		return preferencesDAO;
	}
	
}
