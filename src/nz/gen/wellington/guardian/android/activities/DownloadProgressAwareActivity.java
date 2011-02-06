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
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DownloadProgressAwareActivity extends MenuedActivity {
		
	public static final String TAG = "DownloadProgressAwareActivity";
	private DownloadProgressReceiver downloadProgressReceiver;
	private TextView status;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
		downloadProgressReceiver = new DownloadProgressReceiver();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		status = (TextView) findViewById(R.id.DownloadProgress);
		registerReceiver(downloadProgressReceiver, new IntentFilter(HttpFetcher.DOWNLOAD_PROGRESS));
	}

		
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(downloadProgressReceiver);
		if (status == null) {
			return;
		}
		hideDownloadProgress(status);
	}
	
	
	class DownloadProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final int type = intent.getIntExtra("type", 0);
			if (status != null) {				
				switch (type) {
				
				case HttpFetcher.DOWNLOAD_STARTED:
					showDownloadStart(intent.getStringExtra("url"), status);
					return;
							
				case HttpFetcher.DOWNLOAD_UPDATE:
					updateDownloadProgress(
							intent.getIntExtra("bytes_received", 0),
							intent.getLongExtra("bytes_expected", 0), status);
					return;
					
				case HttpFetcher.DOWNLOAD_COMPLETED:
					hideDownloadProgress(status);
					return;
					
				case HttpFetcher.DOWNLOAD_FAILED:
					showDownloadFailed(intent.getStringExtra("url"), status);
					return;
				}					
			}
		}	
	}
	
	
	final protected void updateDownloadProgress(int received, long  expected, TextView status) {
		final String statusMessage =  received + " / " +  Long.toString(expected);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	final protected void showDownloadStart(String url, TextView status) {		
		final String statusMessage =  "Downloading: " + url;
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	final protected void showDownloadFailed(String url, TextView status) {
		final String statusMessage =  "Download failed (Press Refresh to retry)";
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	final protected void hideDownloadProgress(View status) {
		status.setVisibility(View.GONE);
	}
	
}
