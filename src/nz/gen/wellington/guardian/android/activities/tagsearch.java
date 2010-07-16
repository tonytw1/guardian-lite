package nz.gen.wellington.guardian.android.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class tagsearch extends DownloadProgressAwareActivity implements OnClickListener {
	
	private static final int RESULTS_LOADED = 1;
	private static final int ERROR = 2;
	
	private Button search;
	private NetworkStatusService networkStatusService;
	private List<Tag> searchResults;
	private ContentSource api;
	private Map<String, Section> sections;
	private TagSearchResultsHandler tagSearchResultsHandler;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagsearch);
		
		api = ArticleDAOFactory.getOpenPlatformApi(this.getApplicationContext());
		networkStatusService = new NetworkStatusService(this.getApplicationContext());
		ArticleDAO articleDAO = ArticleDAOFactory.getDao(this.getApplicationContext());
		sections = articleDAO.getSectionsMap();
		
		search = (Button) findViewById(R.id.Search);        
		search.setOnClickListener(this);
		
		searchResults = new ArrayList<Tag>();		
		tagSearchResultsHandler = new TagSearchResultsHandler(this.getApplicationContext());
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		search.setEnabled(networkStatusService.isConnectionAvailable());
		// TODO text warning if no connection is available
		populateSearchResults();
	}


	@Override
	public void onClick(View src) {
		switch (src.getId()) {		
			case R.id.Search:	{
				
				EditText input = (EditText) findViewById(R.id.Input);
				final String searchTerm = input.getText().toString().trim();
				
				if (!(searchTerm.trim().equals(""))) {					
					hideKeyboard(input);
					Thread loader = new Thread(new TagSearchRunner(searchTerm));
					loader.start();
				}
				return;								
			}
		}
		return;
	}

	
	class TagSearchResultsHandler extends Handler {

		private Context context;
		
		public TagSearchResultsHandler(Context context) {
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case RESULTS_LOADED:
				populateSearchResults();
				return;

			case ERROR:
	        	Toast.makeText(context, "Could not load article", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}
	
	
	class TagSearchRunner implements Runnable {
		
		String searchTeam;
		
		public TagSearchRunner(String searchTerm) {
			this.searchTeam = searchTerm;
		}

		@Override
		public void run() {
			List<Tag> results = fetchTagResults(this.searchTeam);
			if (results != null) {
				searchResults = results;				
				Message msg = new Message();
				msg.what = RESULTS_LOADED;
				tagSearchResultsHandler.sendMessage(msg);				
			} else {
				Message msg = new Message();
				msg.what = ERROR;
				tagSearchResultsHandler.sendMessage(msg);
			}
		}
			
		private List<Tag> fetchTagResults(final String searchTerm) {
			List<Tag> results = api.searchTags(searchTerm, sections);
			return results;
		}

	}


	private void hideKeyboard(EditText input) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
	}

	
	private void populateSearchResults() {
		LinearLayout resultsPane = (LinearLayout) findViewById(R.id.TagList);
		resultsPane.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(this);
		TagListPopulatingService.populateTags(inflater, networkStatusService.isConnectionAvailable(), resultsPane, searchResults, this.getApplicationContext());		
	}
	
}
