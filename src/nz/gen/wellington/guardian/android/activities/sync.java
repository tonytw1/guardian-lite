/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.contentupdate.ContentUpdateService;
import nz.gen.wellington.guardian.android.contentupdate.TaskQueue;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.utils.Plurals;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class sync extends DownloadProgressAwareActivity implements OnClickListener, FontResizingActivity {
	
	private NetworkStatusService networkStatusService;
	
	private Button start;
	private Button stop;
	private TextView statusMessage;
	
	private ContentUpdateService contentUpdateService;
	
	private BroadcastReceiver taskStartReceiver;
	private BroadcastReceiver queueChangeReceiver;
	private BroadcastReceiver batchCompletionReceiver;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		networkStatusService = SingletonFactory.getNetworkStatusService(this.getApplicationContext());
		
        startService(new Intent(this, ContentUpdateService.class));
        
        setContentView(R.layout.sync);
        
        setFontSize();
        		
        start = (Button) findViewById(R.id.buttonStart);        
        start.setOnClickListener(this);
        stop = (Button) findViewById(R.id.StopDownloadButton);        
        stop.setOnClickListener(this);
        
        statusMessage = (TextView) findViewById(R.id.StatusMessage);        
        
        taskStartReceiver = new TaskStartReceiver();
        queueChangeReceiver = new QueueChangeReceiver();
        batchCompletionReceiver = new BatchCompletionReceiver();
        
        doBindService();        
	}
	
	
	@Override
	public void setFontSize() {
		super.setFontSize();		
		TextView statusMessage = (TextView) findViewById(R.id.StatusMessage);
		statusMessage.setTextColor(colourScheme.getBodytext());
		statusMessage.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
	}


	@Override
	protected void onResume() {
		super.onResume();

        setFontSize();
        
		registerReceiver(taskStartReceiver, new IntentFilter(ContentUpdateService.TASK_START));		
		registerReceiver(queueChangeReceiver, new IntentFilter(TaskQueue.QUEUE_CHANGED));
		registerReceiver(batchCompletionReceiver, new IntentFilter(ContentUpdateService.BATCH_COMPLETION));
		updateStatus();
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(taskStartReceiver);
		unregisterReceiver(queueChangeReceiver);
		unregisterReceiver(batchCompletionReceiver);	
	}


	public void onClick(View src) {
		switch (src.getId()) {

		case R.id.buttonStart:
			Intent serviceIntent = new Intent(this.getApplicationContext(), ContentUpdateService.class);
			serviceIntent.setAction("RUN");
			contentUpdateService.startService(serviceIntent);
			updateStatus();
			break;
		
		case R.id.StopDownloadButton: 
			contentUpdateService.stop();
			updateStatus();
			break;
		}
	}

	
	private void updateStatus() {
		if (contentUpdateService == null) {
			start.setEnabled(false);
			stop.setEnabled(false);
			return;
		}
		
		switch (contentUpdateService.getStatus()) {
		case ContentUpdateService.STOPPED:			
			if (networkStatusService.isConnectionAvailable()) {
				start.setEnabled(true);
				statusMessage.setText("Download the latest articles from your favourite tags and sections for offline viewing.");
				statusMessage.setVisibility(View.VISIBLE);	

			} else {
				start.setEnabled(false);
				statusMessage.setText("An active connection is required before articles can be downloaded.");
				statusMessage.setVisibility(View.VISIBLE);	
			}
			
			stop.setEnabled(false);
			TextView status = (TextView) findViewById(R.id.Status);
			status.setVisibility(View.GONE);
			TextView currentTask = (TextView) findViewById(R.id.CurrentTask);
			currentTask.setVisibility(View.GONE);
			break;

		case ContentUpdateService.RUNNING:
			stop.setEnabled(true);
			start.setEnabled(false);
			statusMessage.setText("Articles are been downloaded in the background.\n\n" +
				"You may exit this screen and continue browsing in the meantime.");
			statusMessage.setVisibility(View.VISIBLE);
			break;
		
		case ContentUpdateService.CLEANUP:
			stop.setEnabled(false);
			start.setEnabled(false);
			statusMessage.setText("Preforming post download cleanup tasks.");
			statusMessage.setVisibility(View.VISIBLE);
			break;
		}
	}

	
	private void updateQueueStatus(int articles, int images) {	
		TextView status = (TextView) findViewById(R.id.Status);
		if (articles + images == 0) {
			status.setVisibility(View.GONE);
			return;
		}
		final String statusMessage =  articles + " article " + Plurals.getPrural("set", articles) + " and " 
		+ images + " " + Plurals.getPrural("image", images) + " to load.";
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	
	private void updateCurrentTask(String taskName) {
		TextView currentTask = (TextView) findViewById(R.id.CurrentTask);
		currentTask.setText(taskName);
		currentTask.setVisibility(View.VISIBLE);
		updateStatus();
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
	
	
	class BatchCompletionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateStatus();			
			switchToTopStories();
		}
	}
	
	
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        contentUpdateService = ((ContentUpdateService.ContentUpdateServiceBinder)service).getService();
	        updateStatus();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        contentUpdateService = null;
	    }
	};

	
	boolean mIsBound = false;
	void doBindService() {
		Intent intent = new Intent(this, ContentUpdateService.class);
	    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	}

	void doUnbindService() {
	    if (mIsBound) {	       
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