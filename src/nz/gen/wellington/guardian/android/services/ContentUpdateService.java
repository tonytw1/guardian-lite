package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
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

	
    @Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(TAG, "Got start command");
		
		TaskQueue taskQueue = ArticleDAOFactory.getTaskQueue(this.getApplicationContext());
		taskQueue.addArticleTask(new UpdateArticleSetTask(this.getApplicationContext(), new TopStoriesArticleSet(), 10));
		
		this.start();
	}


	@Override
    public void onCreate() {
    }
    
    
    public void start() {
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
	
}
