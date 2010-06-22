package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.notification;
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
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InternalRunnable implements Runnable {

	private static final String TAG = "InternalRunnable";

	int status;
	Context context;
	boolean running;
	
	
    private ContentUpdateReport report;
	private ContentUpdateTaskRunnable currentTask;
    private TaskQueue taskQueue;
    
    private NotificationManager notificationManager;
	private NetworkStatusService networkStatusService;

    
	
	public InternalRunnable(Context context, NotificationManager notificationManager) {
		this.context = context;
		this.notificationManager = notificationManager;
		this.networkStatusService = new NetworkStatusService(context.getApplicationContext());
		this.status = ContentUpdateService.STOPPED;
		
		report = new ContentUpdateReport();
		running = true;
		
		taskQueue = ArticleDAOFactory.getTaskQueue(context);
	}

	public void run() {
		internalRun();    		
	}
	
	public int getStatus() {
		Log.i(TAG, "GET Internal status is: " + this.status + this.toString());
		return this.status;
	}
	
	public void start() {
		running = true;
	}
	
	public void stop() {
		Log.i(TAG, "Starting stop");
		taskQueue.clear();
		if (currentTask != null) {
			currentTask.stop();
		}
		// running = false;
	}
	
	 private void internalRun() {
	    	
	    	while(running) {
	    		Log.i(TAG, "Internal status is: " + status);

	    		this.status = ContentUpdateService.RUNNING;

	    		ContentUpdateTaskRunnable task = getNextTask();
	    		if (networkStatusService.isConnectionAvailable()) {

	    			announceTaskBeginning(task);
	    			task.setReport(report);
	    			currentTask = task;
	    			task.run();
	    			taskQueue.remove(task);    		
	    			announceTaskCompletion(task);
	    			currentTask = null;
	    			
	    		} else {
	    			Log.i(TAG, "Not running update task as network is not available");
	    		}
	    		
	    		if (taskQueue.isEmpty()) {
	    			status = ContentUpdateService.CLEANUP;	    			
	    			
	    			ContentUpdateTaskRunnable purgeExpired = new PurgeExpiredContentTask(context);
	    			currentTask = purgeExpired;
	    			announceTaskBeginning(purgeExpired);
	    			purgeExpired.run();
	    			announceTaskCompletion(purgeExpired);
	    			currentTask = null;
	    			
	    			announceBatchFinished();
	    			sendNotification(report);
	    			status = ContentUpdateService.STOPPED;
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
			Intent intent = new Intent(ContentUpdateService.TASK_START);
			intent.putExtra("task_name", task.getTaskName());
			intent.putExtra("article_queue_size", taskQueue.getArticleSize());
			intent.putExtra("image_queue_size", taskQueue.getImageSize());
			context.sendBroadcast(intent);
		}
		
		
		private void announceTaskCompletion(ContentUpdateTaskRunnable task) {
			Intent intent = new Intent(ContentUpdateService.TASK_COMPLETION);
			context.sendBroadcast(intent);
		}
		
		private void announceBatchFinished() {
			Intent intent = new Intent(ContentUpdateService.BATCH_COMPLETION);
			context.sendBroadcast(intent);
		}
	 
		private void sendNotification(ContentUpdateReport report) {		
			int icon = R.drawable.notification_icon;	// TODO resize icon
			CharSequence tickerText = "Content update complete";
			long when = System.currentTimeMillis();
			
			Notification notification = new Notification(icon, tickerText, when);
			
			CharSequence contentTitle = "Content update complete";
			
			CharSequence contentText = "Fetched " + report.getArticleCount() + " articles" + 
				//" and " +  report.getImageCount() + " images" 
				" in " + calculateTimeTaken(report);
			
			final String fullReport = "Fetched " + report.getArticleCount() + " articles" + 
				" and " +  report.getImageCount() + " images" +
				" in " + calculateTimeTaken(report);
			
			Intent notificationIntent = new Intent(context, notification.class);
			notificationIntent.putExtra("report", fullReport);		
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			notificationManager.notify(ContentUpdateService.UPDATE_COMPLETE_NOTIFICATION_ID, notification);
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
		
}