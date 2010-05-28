package nz.gen.wellington.guardian.android.services;

import java.util.LinkedList;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.sync;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/* See http://google-ukdev.blogspot.com/2009/01/crimes-against-code-and-using-threads.html */
public class TaskQueue {
	
	  private static final String TAG = "TaskQueue";
	
	  
	  private Context baseContext;
	  private NotificationManager notificationManager;

	    
	  private LinkedList<Runnable> tasks;
	   private Thread thread;
	   private boolean running;
	   private boolean inBatch;
	   private Runnable internalRunnable;


	   
	   private class InternalRunnable implements Runnable {
	     public void run() {
	       internalRun();
	     }
	   }
	  
	   public TaskQueue(Context baseContext) {
		   this.baseContext = baseContext;
		   tasks = new LinkedList<Runnable>();
		   internalRunnable = new InternalRunnable();
	   }
	   
	      
	   public void start() {
		   inBatch = false;
		   if (!running) {
			   thread = new Thread(internalRunnable);
			   thread.setDaemon(true);
			   running = true;
			   thread.start();
		   }
	   }
	  
	   public void stop() {
	     running = false;
	   }
	 
	  public void addTask(Runnable task) {
	     synchronized(tasks) {
	         tasks.addFirst(task);
	         tasks.notify();
	     }
	   }
	  
	   private Runnable getNextTask() {
		   Log.i(TAG, "Getting next task");
		   if (getSize() > 0 && !inBatch) {
			   inBatch = true;
			   Log.i(TAG, "Starting an update batch");
		   }
		   synchronized(tasks) {
	       if (tasks.isEmpty()) {
	         try {
	           tasks.wait();
	         } catch (InterruptedException e) {
	           stop();
	         }
	       }
	       return tasks.removeLast();
	     }
	   }
	  
	   
	   public int getSize() {
		   return tasks.size();	
	   }
	   
	   private void internalRun() {
	     while(running) {
	       Runnable task = getNextTask();
	       try {
	         task.run();
	         if (this.getSize() == 0) {
	  		   Log.i(TAG, "Finished batch");
	  		   sendNotification();
	         }
	       } catch (Throwable t) {
	       }
	     }
	   }


	public synchronized boolean isRunning() {
		return running;
	}
	
	
	private void sendNotification() {
		Log.i(TAG, "Sending notification");
		int icon = R.drawable.notification_icon;	// TODO resize icon
		CharSequence tickerText = "Content update complete";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		
		CharSequence contentTitle = "Content update complete";
		CharSequence contentText = "Completed";
		Intent notificationIntent = new Intent(baseContext, sync.class);
		PendingIntent contentIntent = PendingIntent.getActivity(baseContext, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(baseContext, contentTitle, contentText, contentIntent);
		notificationManager.notify(1, notification);	// TODO silently fails
		Log.i(TAG, "Notification sent");
	}
		   
}
