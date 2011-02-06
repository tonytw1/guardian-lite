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

package nz.gen.wellington.guardian.android.contentupdate;

import nz.gen.wellington.guardian.android.contentupdate.tasks.UpdateArticleSetTask;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteTagsArticleSet;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ContentUpdateService extends Service {
	
    public static final String CONTROL = "nz.gen.wellington.guardian.android.services.CONTENT_UPDATE";
	
    public static final String TASK_START = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_START";
    public static final String TASK_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_COMPLETION";
    public static final String BATCH_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_BATCH_COMPLETION";
    
    public static final int RUNNING = 1;
    public static final int CLEANUP = 2;
    public static final int STOPPED = 3;
        
	private static final String TAG = "ContentUpdateService";
    
    public static final int UPDATE_COMPLETE_NOTIFICATION_ID = 1;

    
    private Thread thread;
    private InternalRunnable internalRunnable;
 
   
	private final IBinder mBinder = new ContentUpdateServiceBinder();
	private ArticleSetFactory articleSetFactory;
	private SettingsDAO settingsDAO;
	
	
    @Override
	public void onCreate() {
		super.onCreate();
		this.articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		this.settingsDAO = SingletonFactory.getSettingsDAO(this.getApplicationContext());
	}


	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent != null && intent.getAction() != null && intent.getAction().equals("RUN")) {
			Log.i(TAG, "Got start command");
			int pageSize = settingsDAO.getPageSizePreference();
			this.start(pageSize);
		}
	}

        
    public void start(int pagesize) {
    	Log.i(TAG, "Queuing tasks");
    	queueUpdateTasks(pagesize);
    	internalRunnable = new InternalRunnable(this, (NotificationManager)getSystemService(NOTIFICATION_SERVICE));
    	thread = new Thread(internalRunnable);
    	thread.setDaemon(true);
    	thread.start();
    	internalRunnable.start();
	}

    
	public void stop() {
		internalRunnable.stop();
		//running = false;
	}
	
	
	public int getStatus() {
		if (internalRunnable != null) {
			return internalRunnable.getStatus();
		}
		return STOPPED;
	}
	
	
	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
	
	public class ContentUpdateServiceBinder extends Binder {
		public ContentUpdateService getService() {
			return ContentUpdateService.this;
		}
	}
	
	
	private void queueUpdateTasks(int pagesize) {
		TaskQueue taskQueue = SingletonFactory.getTaskQueue(this.getApplicationContext());
		
		taskQueue.addArticleTask(new UpdateArticleSetTask(this.getApplicationContext(), articleSetFactory.getTopStoriesArticleSet()));
		
		ArticleSet favouritesArticleSet = articleSetFactory.getFavouritesArticleSet();
		if (!favouritesArticleSet.isEmpty()) {			
			for (ArticleSet articleSet : ((FavouriteTagsArticleSet) favouritesArticleSet).getArticleSets()) {
				taskQueue.addArticleTask(new UpdateArticleSetTask(this.getApplicationContext(),articleSet));
			}
			taskQueue.addArticleTask(new UpdateArticleSetTask(this.getApplicationContext(), favouritesArticleSet));
		}
	}
	
}
