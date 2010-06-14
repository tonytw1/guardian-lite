package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.services.ContentUpdateService;
import nz.gen.wellington.guardian.android.services.TaskQueue;
import nz.gen.wellington.guardian.android.services.UpdateSectionArticlesTask;
import nz.gen.wellington.guardian.android.services.UpdateTopStoriesTask;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class sync extends Activity implements OnClickListener {
	
	private static final String TAG = "sync";

	Button start;
	Button stop;
	
	StatusUpdateRunner statusUpdateRunner;
	Handler statusUpdateHandler;
	
	private NotificationManager notificationManager;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync);
        
        start = (Button) findViewById(R.id.buttonStart);        
        start.setOnClickListener(this);
        stop = (Button) findViewById(R.id.StopDownloadButton);        
        stop.setOnClickListener(this);
        
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	notificationManager.cancel(ContentUpdateService.UPDATE_COMPLETE_NOTIFICATION_ID);	
    	    	    	
    	statusUpdateHandler = new StatusUpdateHandler();    	
    	statusUpdateRunner = new StatusUpdateRunner();
    	Thread updateThread = new Thread(statusUpdateRunner);
    	updateThread.start();
   	}

	
	public void onClick(View src) {		
		TaskQueue taskQueue = ArticleDAOFactory.getTaskQueue();
		switch (src.getId()) {
		case R.id.buttonStart:
			Log.d(TAG, "Starting content update service service");
			startService(new Intent(this, ContentUpdateService.class));
			
			// TODO move favourites dao to singleton.
			List<Section> sections = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext())).getFavouriteSections();
			if (sections != null) {
				for (Section section : sections) {
					Log.i(TAG, "Injecting favourite section into update queue: " + section.getName());
					taskQueue.addArticleTask(new UpdateSectionArticlesTask(section, this.getApplicationContext()));
				}
			}
			
			Log.i(TAG, "Injecting update top stories task onto queue");
			ArticleDAO articleDAO = ArticleDAOFactory.getDao(this);
			taskQueue.addArticleTask(new UpdateTopStoriesTask(articleDAO, this));			
			break;
		
		case R.id.StopDownloadButton: 
			taskQueue.clear();
		}
		
		updateStatus();
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "Stopping status update runner");
		statusUpdateRunner.stop();
	}

	
	public void updateStatus() {
		TaskQueue taskQueue = ArticleDAOFactory.getTaskQueue();

		String statusMessage = Integer.toString(taskQueue.getArticleSize()) + " article sets and "
				+ Integer.toString(taskQueue.getImageSize()) + " images to load.";

		TextView status = (TextView) findViewById(R.id.Status);
		status.setText(statusMessage);

		boolean canRun = taskQueue.isEmpty();
		start.setEnabled(canRun);
		stop.setEnabled(!canRun);
	}
	
	class StatusUpdateHandler extends Handler {
		
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d(TAG, "Status update handler is updating status");
			updateStatus();
		}
		
	}
	
	
	class StatusUpdateRunner implements Runnable {
		
		private boolean running;
				
		public StatusUpdateRunner() {
			running = true;
		}

		public void stop() {
			this.running = false;			
		}
		
		public void run() {
			while (running && !Thread.currentThread().isInterrupted()) {
				Message m = new Message();
				m.what = 1;
				sync.this.statusUpdateHandler.sendMessage(m);

				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {					
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	
}