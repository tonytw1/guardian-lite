package nz.gen.wellington.guardian.android.factories;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleApi;
import nz.gen.wellington.guardian.android.contentupdate.TaskQueue;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;

public class SingletonFactory {

	private static TaskQueue taskQueue;
	private static ImageDAO imageDAO;
	private static SectionDAO sectionDAO;
	private static FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
	private static PreferencesDAO preferencesDAO;
	
	public static ArticleDAO getDao(Context context) {
		return new ArticleDAO(context);	
	}
	
	public static ContentSource getOpenPlatformApi(Context context) {
		return new ContentApiStyleApi(context);
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
