package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.services.ContentUpdateService;
import nz.gen.wellington.guardian.android.services.TaskQueue;
import nz.gen.wellington.guardian.android.services.UpdateSectionArticlesTask;
import nz.gen.wellington.guardian.android.services.UpdateTagArticlesTask;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class sync extends Activity implements OnClickListener {
	
	private static final String TAG = "sync";

	Button start;
	Button stop;
	TextView statusMessage;
	
	private ContentUpdateService contentUpdateService;
	
	BroadcastReceiver taskStartReceiver;
	BroadcastReceiver queueChangeReceiver;
	BroadcastReceiver downloadProgressReceiver;
	BroadcastReceiver batchCompletionReceiver;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync);
        		
        start = (Button) findViewById(R.id.buttonStart);        
        start.setOnClickListener(this);
        stop = (Button) findViewById(R.id.StopDownloadButton);        
        stop.setOnClickListener(this);
        
        statusMessage = (TextView) findViewById(R.id.StatusMessage);        
        
        taskStartReceiver = new TaskStartReceiver();
        queueChangeReceiver = new QueueChangeReceiver();
        downloadProgressReceiver = new DownloadProgressReceiver();
        batchCompletionReceiver = new BatchCompletionReceiver();
        
        doBindService();
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(taskStartReceiver, new IntentFilter(ContentUpdateService.TASK_START));		
		registerReceiver(queueChangeReceiver, new IntentFilter(TaskQueue.QUEUE_CHANGED));
		registerReceiver(downloadProgressReceiver, new IntentFilter(HttpFetcher.DOWNLOAD_PROGRESS));
		registerReceiver(batchCompletionReceiver, new IntentFilter(ContentUpdateService.BATCH_COMPLETION));
	}

	
	@Override
	protected void onPause() {
		super.onResume();
		unregisterReceiver(taskStartReceiver);
		unregisterReceiver(queueChangeReceiver);
		unregisterReceiver(downloadProgressReceiver);
		unregisterReceiver(batchCompletionReceiver);	
	}


	public void onClick(View src) {		
		TaskQueue taskQueue = ArticleDAOFactory.getTaskQueue(this.getApplicationContext());
		switch (src.getId()) {

		case R.id.buttonStart:			
			queueFavouriteTags(taskQueue);
			queueFavoriteSections(taskQueue);
			//queueAllSections(taskQueue);
						
			contentUpdateService.start();
			stop.setEnabled(true);
			start.setEnabled(false);
			statusMessage.setText("The most recent articles for your favourite tags and sections are been downloaded in the background.\n\n" +
					"You will receive a notification when this download has completed.");
			statusMessage.setVisibility(View.VISIBLE);

			break;
		
		case R.id.StopDownloadButton: 
			contentUpdateService.stop();
			stop.setEnabled(false);
			start.setEnabled(true);
			statusMessage.setText("Your content download has been halted. Preforming post download cleanup tasks.");
			statusMessage.setVisibility(View.VISIBLE);

			break;
		}
		
	}


	private void queueFavoriteSections(TaskQueue taskQueue) {
		List<Section> favouriteSections = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext())).getFavouriteSections();		
		queueSections(taskQueue, favouriteSections);
	}
	
	
	private void queueAllSections(TaskQueue taskQueue) {
		// TODO Auto-generated method stub
		queueSections(taskQueue, ArticleDAOFactory.getDao(this).getSections());
	}
	
	
	private void queueFavouriteTags(TaskQueue taskQueue) {
		List<Tag> tags = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext())).getFavouriteTags(); // TODO move favourites dao to singleton.
		if (tags != null) {
			for (Tag tag : tags) {
				Log.i(TAG, "Injecting favourite tag into update queue: " + tag.getName());
				taskQueue.addArticleTask(new UpdateTagArticlesTask(tag, this.getApplicationContext()));
			}
		}
	}


	private void queueSections(TaskQueue taskQueue, List<Section> sections) {
		if (sections != null) {
			for (Section section : sections) {
				Log.i(TAG, "Injecting favourite section into update queue: " + section.getName());
				taskQueue.addArticleTask(new UpdateSectionArticlesTask(section, this.getApplicationContext()));
			}
		}
	}
	
	
	private void updateQueueStatus(int articles, int images) {	
		TextView status = (TextView) findViewById(R.id.Status);
		if (articles + images == 0) {
			status.setVisibility(View.GONE);
			return;
		}
		final String statusMessage =  articles + " article sets and " + images + " images to load.";
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	

	private void updateDownloadProgress(int received, long  expected) {
		final String statusMessage =  received + " / " +  Long.toString(expected);
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
		
	private void hideDownloadProgress() {
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setVisibility(View.GONE);
	}

	private void updateCurrentTask(String taskName) {
		TextView currentTask = (TextView) findViewById(R.id.CurrentTask);
		currentTask.setText(taskName);
		currentTask.setVisibility(View.VISIBLE);
	}
	
		
	private void switchToTopStories() {
		Intent intent = new Intent(this, main.class);
		this.startActivity(intent);
	}
		
	class TaskStartReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String taskName = intent.getStringExtra("task_name");			
			updateCurrentTask(taskName);
		}
	}
	

	class QueueChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int articles = intent.getIntExtra("article_queue_size", 0);
			final int images = intent.getIntExtra("image_queue_size", 0);
			updateQueueStatus(articles, images);
		}
	}
	

	class DownloadProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int type = intent.getIntExtra("type", 0);
			switch (type) {		
			case HttpFetcher.DOWNLOAD_UPDATE:
				updateDownloadProgress(
						intent.getIntExtra("bytes_received", 0),
						intent.getLongExtra("bytes_expected", 0));
				return;
				
			case HttpFetcher.DOWNLOAD_COMPLETED:
				hideDownloadProgress();
				return;
			}
		}
	}
	
	class BatchCompletionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			TextView status = (TextView) findViewById(R.id.Status);
			status.setVisibility(View.GONE);
			TextView currentTask = (TextView) findViewById(R.id.CurrentTask);
			currentTask.setVisibility(View.GONE);
			statusMessage.setText("");
			statusMessage.setVisibility(View.GONE);
			switchToTopStories();
		}
	}
	
	
	
	
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        contentUpdateService = ((ContentUpdateService.ContentUpdateServiceBinder)service).getService();

	        // Tell the user about this for our demo.
	        Log.d(TAG, "Content update service is bound");
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        contentUpdateService = null;
	        Log.d(TAG, "Content update service is unbound");

	    }
	};

	boolean mIsBound = false;
	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
		
		Intent intent = new Intent(this, ContentUpdateService.class);
	    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	}

	void doUnbindService() {
	    if (mIsBound) {
	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
	
	
	
	
}