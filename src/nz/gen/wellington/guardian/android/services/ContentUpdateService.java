package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.notification;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ContentUpdateService extends Service {
	
    public static final String CONTROL = "nz.gen.wellington.guardian.android.services.CONTENT_UPDATE";
	
    public static final String TASK_START = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_START";
    public static final String TASK_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_COMPLETION";
    public static final String BATCH_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_BATCH_COMPLETION";
    
	private static final String TAG = "ContentUpdateService";
    
    public static final int UPDATE_COMPLETE_NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    
    private Thread thread;
    
    private boolean running;

    private TaskQueue taskQueue;
    private Runnable internalRunnable;
    private ContentUpdateReport report;
	private ContentUpdateTaskRunnable currentTask;
   
	private final IBinder mBinder = new ContentUpdateServiceBinder();

    
    @Override
    public void onCreate() {
    	notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	Log.i(TAG, "Started content update service");

    	taskQueue = ArticleDAOFactory.getTaskQueue(this.getApplicationContext());
    	internalRunnable = new InternalRunnable();
    	running = false;    	
    }
    
    
    public void start() {
		Log.i(TAG, "Starting run");

		report = new ContentUpdateReport();
		running = true;

    	thread = new Thread(internalRunnable);
    	thread.setDaemon(true);
    	thread.start();
	}


	public void stop() {
		Log.i(TAG, "Starting stop");
		running = false;
		if (currentTask != null) {
			currentTask.stop();
		}
		taskQueue.clear();
	}
	
	
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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
		
		CharSequence contentText = "Fetched " + report.getArticleCount() + " articles" + 
			//" and " +  report.getImageCount() + " images" 
			" in " + calculateTimeTaken(report);
		
		final String fullReport = "Fetched " + report.getArticleCount() + " articles" + 
			" and " +  report.getImageCount() + " images" +
			" in " + calculateTimeTaken(report);
		
		Intent notificationIntent = new Intent(this, notification.class);
		notificationIntent.putExtra("report", fullReport);		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(UPDATE_COMPLETE_NOTIFICATION_ID, notification);
	}


	private String calculateTimeTaken(ContentUpdateReport report) {
		Interval timeTaken = new Interval(report.getStartTime().getMillis(), new DateTime().getMillis());
	    PeriodFormatter pf = new PeriodFormatterBuilder().printZeroRarelyFirst()
        .appendYears().appendSuffix("y ", "y ")
        .appendMonths().appendSuffix("m" , "m ")
        .appendDays().appendSuffix("d ", "d ")
        .appendHours().appendSuffix("h ", "h ")
        .appendMinutes().appendSuffix("m ", "m ")
        .appendSeconds().appendSuffix("s ", "s ")
        .toFormatter();

		String duration = pf.print(timeTaken.toPeriod()).trim();
		return duration;
	}
	

    private class InternalRunnable implements Runnable {
    	public void run() {
    		internalRun();
    	}
    }
    
	
	public class ContentUpdateServiceBinder extends Binder {
		public ContentUpdateService getService() {
			return ContentUpdateService.this;
		}
	}
	
}
