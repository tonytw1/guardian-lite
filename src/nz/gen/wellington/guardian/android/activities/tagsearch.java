package nz.gen.wellington.guardian.android.activities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class tagsearch extends DownloadProgressAwareActivity implements OnClickListener, FontResizingActivity {
	
	private static final String NETWORK_CONNECTION_REQUIRED_WARNING = "A network connection is required to be able to search for tags.";
	private static final String NO_MATCHING_TAGS_WARNING = "No matching tags were found.";
	
	private static final int RESULTS_LOADED = 1;
	private static final int ERROR = 2;
	
	private static final List<String> allowedTagSearchTypes = Arrays.asList("keyword", "contributor", "blog", "series");
	
	private Button search;
	private NetworkStatusService networkStatusService;
	
	private List<Tag> searchResults;
	private Map<String, Section> sections;
	private TagSearchResultsHandler tagSearchResultsHandler;
	private SectionDAO sectionDAO;
	private ArticleSetFactory articleSetFactory;
	private TagListPopulatingService tagListPopulatingService;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagsearch);		
		
		networkStatusService = SingletonFactory.getNetworkStatusService(this.getApplicationContext());
		articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		sectionDAO = SingletonFactory.getSectionDAO(this.getApplicationContext());
		tagListPopulatingService = SingletonFactory.getTagListPopulator(this.getApplicationContext());
		tagSearchResultsHandler = new TagSearchResultsHandler();
		
		sections = sectionDAO.getSectionsMap();
		searchResults = null;		
		
		search = (Button) findViewById(R.id.Search);        
		search.setOnClickListener(this);
		
		populateView();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
        populateView();
	}


	private void populateView() {
		setFontSize();        
        final boolean isConnectionAvailable = networkStatusService.isConnectionAvailable();
        search.setEnabled(isConnectionAvailable);
		if (!isConnectionAvailable) {
        	outputErrorWarning(NETWORK_CONNECTION_REQUIRED_WARNING);
        } else {       
        	if (searchResults != null) {
        		populateSearchResults(searchResults);
        	}
        }
	}

	
	@Override
	public void setFontSize() {
		super.setFontSize();	
	}
	

	@Override
	public void onClick(View src) {
		switch (src.getId()) {		
			case R.id.Search:	{
				
				EditText input = (EditText) findViewById(R.id.Input);
				final String searchTerm = input.getText().toString().trim();
				
				if (!(searchTerm.trim().equals(""))) {					
					hideKeyboard(input);
					Thread loader = new Thread(new TagSearchRunner(searchTerm, this.getApplicationContext()));
					loader.start();
				}
				return;								
			}
		}
		return;
	}

	
	class TagSearchResultsHandler extends Handler {
		
		private static final String FAILED_TO_LOAD_SEARCH_RESULTS_WARNING = "Failed to load tag search results.";

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case RESULTS_LOADED:
				populateSearchResults(searchResults);
				return;
				
			case ERROR:
				outputErrorWarning(FAILED_TO_LOAD_SEARCH_RESULTS_WARNING);
				return;
			}
		}
	}
	
	
	class TagSearchRunner implements Runnable {
		String searchTeam;
		private ContentSource api;
		
		public TagSearchRunner(String searchTerm, Context context) {
			this.api = SingletonFactory.getOpenPlatformApi(context);
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
			List<Tag> results = api.searchTags(searchTerm, allowedTagSearchTypes, sections);
			return results;
		}

	}


	private void hideKeyboard(EditText input) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
	}

	
	private void populateSearchResults(List<Tag> results) {
		if (results.isEmpty()) {
			outputErrorWarning(NO_MATCHING_TAGS_WARNING);
		}
		LinearLayout resultsPane = (LinearLayout) findViewById(R.id.TagList);
		resultsPane.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(this);
		tagListPopulatingService.populateTags(inflater, networkStatusService.isConnectionAvailable(), resultsPane, articleSetFactory.getArticleSetsForTags(results), colourScheme, baseFontSize);
	}
	
	
	
	private void outputErrorWarning(String message) {
		LinearLayout resultsScroller = (LinearLayout) findViewById(R.id.TagList);		
		resultsScroller.removeAllViews();
		
		TextView noArticlesMessage = new TextView(this.getApplicationContext());
		noArticlesMessage.setText(message);
		noArticlesMessage.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
		noArticlesMessage.setTextColor(colourScheme.getHeadline());
		noArticlesMessage.setPadding(2, 3, 2, 3);					
		resultsScroller.addView(noArticlesMessage, 0);
	}
	
}
