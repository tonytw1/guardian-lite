package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class DownloadProgressAwareActivity extends MenuedActivity {
	

	class DownloadProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int type = intent.getIntExtra("type", 0);
			switch (type) {
			
			case HttpFetcher.DOWNLOAD_STARTED:
				showDownloadStart(intent.getStringExtra("url"));
				return;
						
			case HttpFetcher.DOWNLOAD_UPDATE:
				updateDownloadProgress(
						intent.getIntExtra("bytes_received", 0),
						intent.getLongExtra("bytes_expected", 0));
				return;
				
			case HttpFetcher.DOWNLOAD_COMPLETED:
				hideDownloadProgress();
				return;
				
			case HttpFetcher.DOWNLOAD_FAILED:
				showDownloadFailed(intent.getStringExtra("url"));
				return;
			}
		}				
	}
	
	
	final protected void updateDownloadProgress(int received, long  expected) {
		final String statusMessage =  received + " / " +  Long.toString(expected);
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	final protected void showDownloadStart(String url) {
		final String statusMessage =  "Downloading: " + url;
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	final protected void showDownloadFailed(String url) {
		final String statusMessage =  "Download failed (Press Refresh to retry)";
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	final protected void hideDownloadProgress() {
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setVisibility(View.GONE);
	}
	
}
