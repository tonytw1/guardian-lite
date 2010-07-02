package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class sections extends Activity {
	
	private static final String TAG = "sections";
	
	ListAdapter adapter;
	private ArticleDAO articleDAO;
	
	
	protected BroadcastReceiver downloadProgressReceiver;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onStart();
		articleDAO = ArticleDAOFactory.getDao(this.getApplicationContext());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sections);
		
		setHeading("Sections");
		setHeadingColour("#0061A6");
		
		downloadProgressReceiver = new DownloadProgressReceiver();
		
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(downloadProgressReceiver, new IntentFilter(HttpFetcher.DOWNLOAD_PROGRESS));
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);
		mainPane.removeAllViews();
		populateSections();        
	}

		
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(downloadProgressReceiver);
	}
	
		
	private void populateSections() {
		List<Section> sections = articleDAO.getSections();		
		if (sections != null) {
			LayoutInflater inflater = LayoutInflater.from(this);		
			LinearLayout authorList = (LinearLayout) findViewById(R.id.MainPane);
			
			boolean connectionIsAvailable = new NetworkStatusService(this.getApplicationContext()).isConnectionAvailable();
			TagListPopulatingService.populateSections(inflater, connectionIsAvailable, authorList, sections, this.getApplicationContext());

		} else {
        	Toast.makeText(this, "Could not load sections", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home");
		menu.add(0, 2, 0, "Favourites");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			switchToMain();
			return true;
		case 2:
			switchToFavourites();
			return true;
		}
		return false;
	}
	
	
	private void switchToMain() {
		Intent intent = new Intent(this, main.class);
		this.startActivity(intent);	
	}
	
	private void switchToFavourites() {
		Intent intent = new Intent(this, favourites.class);
		this.startActivity(intent);		
	}
	
	
	// TODO duplication
	protected void setHeading(String headingText) {
		TextView heading = (TextView) findViewById(R.id.Heading);
		heading.setText(headingText);		
	}
	// TODO duplication
	protected void setHeadingColour(String colour) {
		LinearLayout heading = (LinearLayout) findViewById(R.id.HeadingLayout);
		heading.setBackgroundColor(Color.parseColor(colour));
	}
	
	
	// TODO All duplicated with main and sync.
	class DownloadProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Received: " + intent.toString());
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
	
	
	private void updateDownloadProgress(int received, long  expected) {
		final String statusMessage =  received + " / " +  Long.toString(expected);
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	private void showDownloadStart(String url) {
		final String statusMessage =  "Downloading: " + url;
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	private void showDownloadFailed(String url) {
		Log.i(TAG, "Got download failed message: " + url);
		final String statusMessage =  "Download failed: " + url;
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setText(statusMessage);
		status.setVisibility(View.VISIBLE);
	}
	
	private void hideDownloadProgress() {
		TextView status = (TextView) findViewById(R.id.DownloadProgress);
		status.setVisibility(View.GONE);
	}
	
}