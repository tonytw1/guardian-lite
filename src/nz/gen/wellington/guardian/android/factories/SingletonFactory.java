package nz.gen.wellington.guardian.android.factories;

import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.api.filtering.HtmlCleaner;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleApi;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentResultsHandler;
import nz.gen.wellington.guardian.android.contentupdate.TaskQueue;
import nz.gen.wellington.guardian.android.network.DownProgressAnnouncer;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;

public class SingletonFactory {

	private static TaskQueue taskQueue;
	private static ImageDAO imageDAO;
	private static SectionDAO sectionDAO;
	private static FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
	private static PreferencesDAO preferencesDAO;
	private static ArticleSetFactory articleSetFactory;
	private static NetworkStatusService networkStatusService;
	private static DownProgressAnnouncer downloadProgressAnnouncer;
	private static TagListPopulatingService tagListPopulatingService;
	
	public static ArticleDAO getArticleDao(Context context) {
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
			favouriteSectionsAndTagsDAO = new FavouriteSectionsAndTagsDAO(context);			
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

	public static ArticleSetFactory getArticleSetFactory(Context context) {
		if (articleSetFactory == null) {
			articleSetFactory = new ArticleSetFactory(context);
		}
		return articleSetFactory;
	}
	
	public static ContentResultsHandler getContentResultsHandler(Context context) {
		return new ContentResultsHandler(context, new HtmlCleaner());
	}
	
	public static NetworkStatusService getNetworkStatusService(Context context) {
		if (networkStatusService == null) {
			networkStatusService =new NetworkStatusService(context);
		}
		return networkStatusService;		
	}

	public static DownProgressAnnouncer getDownloadProgressAnnouncer(Context context) {
		if (downloadProgressAnnouncer == null) {
			downloadProgressAnnouncer = new DownProgressAnnouncer(context);
		}
		return downloadProgressAnnouncer;
	}

	public static TagListPopulatingService getTagListPopulator(Context context) {
		if (tagListPopulatingService == null) {
			tagListPopulatingService = new TagListPopulatingService(context);
		}
		return tagListPopulatingService;
	}
	
}
