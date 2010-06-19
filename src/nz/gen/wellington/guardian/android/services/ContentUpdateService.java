package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.main;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ContentUpdateService extends Service {
	
    public static final String CONTROL = "nz.gen.wellington.guardian.android.services.CONTENT_UPDATE";
	
    public static final String TASK_START = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_START";
    public static final String TASK_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_COMPLETION";
    public static final String BATCH_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_BATCH_COMPLETION";
    
	private static final String TAG = "ContentLoader";
    
    public static final int UPDATE_COMPLETE_NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    
    private Thread thread;
    
    private boolean running;

    private TaskQueue taskQueue;
    private Runnable internalRunnable;
    private ContentUpdateReport report;
	private ContentUpdateTaskRunnable currentTask;
   
    
    @Override
    public void onCreate() {
    	notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	Log.i(TAG, "Started content update service");

    	taskQueue = ArticleDAOFactory.getTaskQueue(this.getApplicationContext());
    	internalRunnable = new InternalRunnable();
    	running = false;
    	
    	BroadcastReceiver contentUpdateControlReceiver = new ContentUpdateControlReceiver();
		registerReceiver(contentUpdateControlReceiver, new IntentFilter(ContentUpdateService.CONTROL));    	
    }
    

    private class InternalRunnable implements Runnable {
    	public void run() {
    		internalRun();
    	}
    }
    
    
    private void internalRun() {
    	
    	while(running) {    		
    		ContentUpdateTaskRunnable task = getNextTask();
    		if (NetworkStatusService.isConnectionAvailable(this)) {
    			announceTaskBeginning(task);
    			task.setReport(report);
    			currentTask = task;
    			task.run();
    			taskQueue.remove(task);    		
    			announceTaskCompletion(task);
    			
    		} else {
    			Log.i(TAG, "Not running update task as network is not available");
    		}
    		
    		if (taskQueue.isEmpty()) {
    			ArticleDAO articleDAO = ArticleDAOFactory.getDao(this);
    			ContentUpdateTaskRunnable updateTopStories = new UpdateTopStoriesTask(articleDAO, this);    			
    			ContentUpdateTaskRunnable purgeExpired = new PurgeExpiredContentTask(this);
    			
    			announceTaskBeginning(updateTopStories);
    			updateTopStories.run();
    			announceTaskCompletion(updateTopStories);
    			
    			announceTaskBeginning(purgeExpired);
    			purgeExpired.run();
    			announceTaskCompletion(purgeExpired);
    			
    			announceBatchFinished();
    			sendNotification(report);
    			running = false;
 			}
    	}
    	
    }
 
    
	private ContentUpdateTaskRunnable getNextTask() {
    	Log.i(TAG, "Getting next task");    
    	synchronized(taskQueue) {    	
    		return taskQueue.getNext();    		   		    		
    	}
    }
	 
	   
	private void start() {
		Log.i(TAG, "Starting run");

		report = new ContentUpdateReport();
		running = true;

    	thread = new Thread(internalRunnable);
    	thread.setDaemon(true);
    	thread.start();
	}


	private void stop() {
		Log.i(TAG, "Starting stop");
		running = false;
		if (currentTask != null) {
			currentTask.stop();
		}
		taskQueue.clear();
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
		
	
	private void announceTaskBeginning(ContentUpdateTaskRunnable task) {
		Intent intent = new Intent(TASK_START);
		intent.putExtra("task_name", task.getTaskName());
		intent.putExtra("article_queue_size", taskQueue.getArticleSize());
		intent.putExtra("image_queue_size", taskQueue.getImageSize());
		sendBroadcast(intent);
	}
	
	
	private void announceTaskCompletion(ContentUpdateTaskRunnable task) {
		Intent intent = new Intent(TASK_COMPLETION);
		sendBroadcast(intent);
	}
	
	private void announceBatchFinished() {
		Intent intent = new Intent(BATCH_COMPLETION);
		sendBroadcast(intent);
	}
	
	private void sendNotification(ContentUpdateReport report) {		
		int icon = R.drawable.notification_icon;	// TODO resize icon
		CharSequence tickerText = "Content update complete";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "Content update complete";
		CharSequence contentText = "Fetched " + report.getArticleCount() + " articles and " + report.getImageCount() + " images.";
		
		Intent notificationIntent = new Intent(this, main.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(UPDATE_COMPLETE_NOTIFICATION_ID, notification);
	}
	
	
	class ContentUpdateControlReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, intent.toString());
			final String command = intent.getStringExtra("command");
			if (command != null && command.equals("start")) {
				start();
				
			} else if (command != null && command.equals("stop")) {
				stop();				
			}			
		}
	}
		
}
