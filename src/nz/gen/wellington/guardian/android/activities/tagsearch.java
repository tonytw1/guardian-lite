package nz.gen.wellington.guardian.android.activities;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


public class tagsearch extends MenuedActivity implements OnClickListener {

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
					List<Tag> results = api.searchTags(searchTerm);
					if (results != null) {
						searchResults = results;
						populateSearchResults();
						
					} else {
						// TODO Toast
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
