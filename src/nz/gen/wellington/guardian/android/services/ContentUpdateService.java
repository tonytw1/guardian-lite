package nz.gen.wellington.guardian.android.services;

import java.util.LinkedList;
import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.sync;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ContentUpdateService extends Service {
	
    private static final String TAG = "ContentLoader";
    
    public static final int UPDATE_COMPLETE_NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;
    
    private LinkedList<Runnable> tasks;
    private Thread thread;
    private boolean running;
    private Runnable internalRunnable;
    
    
    private class InternalRunnable implements Runnable {
    	public void run() {
	    	 internalRun();
	     }
    }
    
    @Override
    public void onCreate() {
    	notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	Log.i(TAG, "Started content update service");

    	tasks = new LinkedList<Runnable>();
    	internalRunnable = new InternalRunnable();
    	
    	if (!running) {
   	       thread = new Thread(internalRunnable);
   	       thread.setDaemon(true);
   	       running = true;
   	       thread.start();
   	   	}   	
    }
    
    
    private void internalRun() {
    	while(running) {
	       Runnable task = getNextTask();
	       try {
	         task.run();
	       } catch (Throwable t) {
	       }
    	}
    }
    
	
    
    public void stop() {
	     running = false;
	   }
	 
	  public void addTask(Runnable task) {
	     synchronized(tasks) {
	         tasks.addFirst(task);
	         tasks.notify(); // notify any waiting threads
	     }
	   }
	  
	   private Runnable getNextTask() {
		   Log.i(TAG, "Getting next task");
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
	 
	   
	@Override
	public void onDestroy() {
		Log.i(TAG, "Stopping content update service");
		// TODO wait for thread to stop correctly
		super.onDestroy();
	}

		
	// TODO needs to garbage collect author and tags pages.
	private void reloadAll() {
		while (true) {
			ArticleDAO articleDAO = ArticleDAOFactory.getDao(this);
			Log.i(TAG, "Refetching Top Stories");
			articleDAO.evictSections();
			List<Section> sections = articleDAO.getSections();
			
			int sectionCount = 0;
			for (Section section : sections) {
				articleDAO.evictArticleSet(new SectionArticleSet(section));
				Log.i(TAG, "Fetching section articles: " + section.getName());
				sectionCount = sectionCount + 1;
				articleDAO.getSectionItems(section);
			}		
			Log.i(TAG, "Done");
			sendNotification(sectionCount);
			
			sleepForMinutes(10);	// TODO push to a preferences.
		}
	}


	private void sleepForMinutes(int minutes) {
		try {
			Thread.sleep(1000 * 60 * minutes);
		} catch (InterruptedException e) {
			Log.w(TAG, "Interrupted sleep");
		}
	}


	private void sendNotification(int sectionCount) {		
		int icon = R.drawable.notification_icon;	// TODO resize icon
		CharSequence tickerText = "Content update complete";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "Content update complete";
		CharSequence contentText = "Updated " + sectionCount + " sections";
		Intent notificationIntent = new Intent(this, sync.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(UPDATE_COMPLETE_NOTIFICATION_ID, notification);
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
