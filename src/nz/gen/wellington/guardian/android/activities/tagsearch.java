package nz.gen.wellington.guardian.android.activities;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class tagsearch extends DownloadProgressAwareActivity implements OnClickListener {

	private static final String TAG = "tagsearch";
	
	private Button search;
	private NetworkStatusService networkStatusService;
	private List<Tag> searchResults;
	private ContentSource api;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onStart();
		setContentView(R.layout.tagsearch);
		
		api = ArticleDAOFactory.getOpenPlatformApi(this.getApplicationContext());
		networkStatusService = new NetworkStatusService(this.getApplicationContext());
		
		search = (Button) findViewById(R.id.Search);        
		search.setOnClickListener(this);
		
		searchResults = new ArrayList<Tag>();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		search.setEnabled(networkStatusService.isConnectionAvailable());
		populateSearchResults();
	}


	@Override
	public void onClick(View src) {
		switch (src.getId()) {		
			case R.id.Search:	{
				
				EditText input = (EditText) findViewById(R.id.Input);
				final String searchTerm = input.getText().toString();
				
				if (!(searchTerm.trim().equals(""))) {
					
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
					
					List<Tag> results = api.searchTags(searchTerm);
					if (results != null) {
						Log.i(TAG, "Found tags: " + results);
						searchResults = results;
						populateSearchResults();
						
					} else {
			        	Toast.makeText(this, "Tag lookup failed", Toast.LENGTH_SHORT).show();
					}
				}
				return;								
			}
		}
		return;
	}

	
	private void populateSearchResults() {		
		LinearLayout resultsPane = (LinearLayout) findViewById(R.id.TagList);
		resultsPane.removeAllViews();
		
		LayoutInflater inflater = LayoutInflater.from(this);
		TagListPopulatingService.populateTags(inflater, networkStatusService.isConnectionAvailable(), resultsPane, searchResults, this.getApplicationContext());		
	}
	
}
