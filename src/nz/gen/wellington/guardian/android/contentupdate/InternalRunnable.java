package nz.gen.wellington.guardian.android.contentupdate;

import java.util.Date;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.notification;
import nz.gen.wellington.guardian.android.contentupdate.tasks.ContentUpdateTaskRunnable;
import nz.gen.wellington.guardian.android.contentupdate.tasks.PurgeExpiredContentTask;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.utils.DateTimeHelper;
import nz.gen.wellington.guardian.android.utils.Plurals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class InternalRunnable implements Runnable {

	private static final String TAG = "InternalRunnable";

	private int status;
	private Context context;
	private boolean running;
		
    private ContentUpdateReport report;
	private ContentUpdateTaskRunnable currentTask;
    private TaskQueue taskQueue;
    
    private NotificationManager notificationManager;
	private NetworkStatusService networkStatusService;

	private PowerManager.WakeLock wl;
	
	public InternalRunnable(Context context, NotificationManager notificationManager) {
		this.context = context;
		this.notificationManager = notificationManager;
		this.networkStatusService = new NetworkStatusService(context.getApplicationContext());
		this.status = ContentUpdateService.STOPPED;
		
		report = new ContentUpdateReport();
		running = true;
		
		taskQueue = SingletonFactory.getTaskQueue(context);
	}

	public void run() {
		internalRun();    		
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public void start() {
		running = true;		
		createWakeLock();
	}
	
	public void stop() {
		taskQueue.clear();
		if (currentTask != null) {
			currentTask.stop();
		}
		// running = false;
	}
	
	 private void internalRun() {
	    	
    	while(running) {
    		this.status = ContentUpdateService.RUNNING;

    		ContentUpdateTaskRunnable task = getNextTask();
    		if (task != null) {
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
    	
    	Log.i(TAG, "Content update has completed - releasing wake lock");
    	releaseWakeLock();	    	
    }

	 
	 private ContentUpdateTaskRunnable getNextTask() {
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
		int icon = R.drawable.notification_icon;
		CharSequence tickerText = "Content update complete";			
		Date now = DateTimeHelper.now();
		
		Notification notification = new Notification(icon, tickerText, now.getTime());
		
		CharSequence contentTitle = "Content update complete";
		CharSequence contentText = "Fetched " + report.getArticleCount() + " articles" + 
			" in " + DateTimeHelper.calculateTimeTaken(report.getStartTime(), now);
		
		final String fullReport = "Fetched " + report.getArticleCount() + " articles" + 
			" and " +  report.getImageCount() + " " + Plurals.getPrural("image", report.getImageCount()) +
			" in " + DateTimeHelper.calculateTimeTaken(report.getStartTime(), now);
		
		Intent notificationIntent = new Intent(context, notification.class);
		notificationIntent.putExtra("report", fullReport);		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(ContentUpdateService.UPDATE_COMPLETE_NOTIFICATION_ID, notification);
	}
		
		
	private void createWakeLock() {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wl.acquire();
	}
	
	private void releaseWakeLock() {
		if (wl != null) {
			wl.release();
		}
	}

}