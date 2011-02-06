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

package nz.gen.wellington.guardian.android.factories;

import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.ImageDownloadDecisionService;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.api.filtering.HtmlCleaner;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleApi;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentResultsHandler;
import nz.gen.wellington.guardian.android.contentupdate.TaskQueue;
import nz.gen.wellington.guardian.android.network.DownProgressAnnouncer;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
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
	private static ImageDownloadDecisionService imageDownloadDecisionService;
	private static SettingsDAO settingsDAO;
	
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
	
	public static SettingsDAO getSettingsDAO(Context context) {
		if (settingsDAO == null) {
			settingsDAO = new SettingsDAO(context);
		}
		return settingsDAO;
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

	public static ImageDownloadDecisionService getImageDownloadDecisionService(Context context) {
		if (imageDownloadDecisionService == null) {
			imageDownloadDecisionService = new ImageDownloadDecisionService(context);
		}
		return imageDownloadDecisionService;
	}
	
}
