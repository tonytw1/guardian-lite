package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.services.ContentUpdateService;
import nz.gen.wellington.guardian.android.services.TaskQueue;
import nz.gen.wellington.guardian.android.services.UpdateSectionArticlesTask;
import nz.gen.wellington.guardian.android.services.UpdateTagArticlesTask;
import nz.gen.wellington.guardian.android.services.UpdateTopStoriesTask;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class sync extends Activity implements OnClickListener {
	
	private static final String TAG = "sync";

	Button start;
	Button stop;
	

	private NotificationManager notificationManager;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync);
        
    	Log.d(TAG, "Starting content update service service");
		startService(new Intent(this, ContentUpdateService.class));	// TODO should be on app startup
		
        start = (Button) findViewById(R.id.buttonStart);        
        start.setOnClickListener(this);
        stop = (Button) findViewById(R.id.StopDownloadButton);        
        stop.setOnClickListener(this);
        
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	notificationManager.cancel(ContentUpdateService.UPDATE_COMPLETE_NOTIFICATION_ID);	
    	    	    	
    	Log.d(TAG, "Starting content update service service");
		startService(new Intent(this, ContentUpdateService.class));	// TODO should be on app startup		
   	}
	
	
	@Override
	protected void onResume() {
		super.onResume();

		BroadcastReceiver taskStartReceiver = new TaskStartReceiver();
		registerReceiver(taskStartReceiver, new IntentFilter(ContentUpdateService.TASK_START));
		
		BroadcastReceiver queueChangeReceiver = new QueueChangeReceiver();
		registerReceiver(queueChangeReceiver, new IntentFilter(TaskQueue.QUEUE_CHANGED));
		
		BroadcastReceiver downloadProgressReceiver = new DownloadProgressReceiver();
		registerReceiver(downloadProgressReceiver, new IntentFilter(HttpFetcher.DOWNLOAD_PROGRESS));
				
		BroadcastReceiver batchCompletionReceiver = new BatchCompletionReceiver();
		registerReceiver(batchCompletionReceiver, new IntentFilter(ContentUpdateService.BATCH_COMPLETION));
	}

	
	public void onClick(View src) {		
		TaskQueue taskQueue = ArticleDAOFactory.getTaskQueue(this.getApplicationContext());
		switch (src.getId()) {

		case R.id.buttonStart:			
			queueFavouriteTags(taskQueue);
			queueFavouriteSections(taskQueue);
			
			Log.i(TAG, "Injecting update top stories task onto queue");
			ArticleDAO articleDAO = ArticleDAOFactory.getDao(this);
			taskQueue.addArticleTask(new UpdateTopStoriesTask(articleDAO, this));			
			break;
		
		case R.id.StopDownloadButton: 
			taskQueue.clear();
		}
		
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


	private void queueFavouriteSections(TaskQueue taskQueue) {
		List<Section> sections = new FavouriteSectionsAndTagsDAO(ArticleDAOFactory.getDao(this.getApplicationContext())).getFavouriteSections(); // TODO move favourites dao to singleton.
		if (sections != null) {
			for (Section section : sections) {
				Log.i(TAG, "Injecting favourite section into update queue: " + section.getName());
				taskQueue.addArticleTask(new UpdateSectionArticlesTask(section, this.getApplicationContext()));
			}
		}
	}
	
	
	private void updateStatus(int articles, int images) {	
		final String statusMessage =  articles + " article sets and " + images + " images to load.";
		TextView status = (TextView) findViewById(R.id.Status);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	
	private void updateDownloadProgress(int received, long  expected) {
		final String statusMessage =  received + " / " +  Long.toString(expected);
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setText(statusMessage);
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
			updateStatus(articles, images);
		}
	}
	

	class DownloadProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int received = intent.getIntExtra("bytes_received", 0);
			final long expected = intent.getLongExtra("bytes_expected", 0);
			updateDownloadProgress(received, expected);
		}		
	}
	
	class BatchCompletionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			TextView status = (TextView) findViewById(R.id.Status);
			status.setVisibility(View.GONE);
			TextView currentTask = (TextView) findViewById(R.id.CurrentTask);
			currentTask.setVisibility(View.GONE);
			switchToTopStories();
		}
	}
	
}