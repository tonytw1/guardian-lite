package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.sync;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ContentUpdateService extends Service {
	
    public static final String TASK_START = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_START";
    public static final String TASK_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_TASK_COMPLETION";
    public static final String BATCH_COMPLETION = "nz.gen.wellington.guardian.android.event.CONTENT_UPDATE_BATCH_COMPLETION";
    
	private static final String TAG = "ContentLoader";
    
    public static final int UPDATE_COMPLETE_NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    
    private Thread thread;
    
    private boolean running;
    private boolean inBatch;

    private TaskQueue taskQueue;
    private Runnable internalRunnable;
    private ContentUpdateReport report;
   
    
    @Override
    public void onCreate() {
    	notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	Log.i(TAG, "Started content update service");

    	taskQueue = ArticleDAOFactory.getTaskQueue(this.getApplicationContext());
    	internalRunnable = new InternalRunnable();

    	 inBatch = false;    	 
    	 if (!running) {
			   thread = new Thread(internalRunnable);
			   thread.setDaemon(true);
			   running = true;
			   thread.start();
    	 }
    }
    
    private class InternalRunnable implements Runnable {
    	public void run() {
    		internalRun();
    	}
    }
    
    
    private void internalRun() {
    	while(running) {
    		ContentUpdateTaskRunnable task = getNextTask();
    		
    		announceTaskBeginning(task);
    		task.setReport(report);    		
    		task.run();
    		taskQueue.remove(task);
    		
    		announceTaskCompletion(task);    		
    	} 
    }
 
    
	private ContentUpdateTaskRunnable getNextTask() {
    	Log.i(TAG, "Getting next task");    
    	synchronized(taskQueue) {
    		if (taskQueue.isEmpty()) {
	    	   if (inBatch) {
	    		   sendNotification(report);
	    		   inBatch = false;
	    		   announceBatchFinished();
	    	   }
	        
	    	   try {
	    		   Log.i(TAG, "Waiting for next task");
	    		   taskQueue.wait();	           

	    		   inBatch= true;
	    		   report = new ContentUpdateReport();
	           
	    	   } catch (InterruptedException e) {
	    		   stop();
	    	   }	    	   
    		}    		    		
    		return taskQueue.getNext();
    	}
    }
	 
	   


	private void stop() {
		//running = false;
	}


	@Override
	public void onDestroy() {
		Log.i(TAG, "Stopping content update service");
		// TODO wait for thread to stop correctly
		super.onDestroy();
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
		
		Intent notificationIntent = new Intent(this, sync.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(UPDATE_COMPLETE_NOTIFICATION_ID, notification);
	}
	
}
