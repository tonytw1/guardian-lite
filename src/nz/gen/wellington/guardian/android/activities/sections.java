package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class sections extends DownloadProgressAwareActivity {
		
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
		
}