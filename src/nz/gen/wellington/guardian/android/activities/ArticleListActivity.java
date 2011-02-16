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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.activities.ui.ArticleListActivityViewPopulator;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.ImageDownloadDecisionService;
import nz.gen.wellington.guardian.android.api.openplatfrom.Refinement;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.utils.DateTimeHelper;
import nz.gen.wellington.guardian.model.Article;
import nz.gen.wellington.guardian.model.Section;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ArticleListActivity extends DownloadProgressAwareActivity implements FontResizingActivity {
	
	private static final String TAG = "ArticleListActivity";
	
	protected ArticleDAO articleDAO;
	protected ImageDAO imageDAO;
	private NetworkStatusService networkStatusService;
	private ImageDownloadDecisionService imageDownloadDecisionService;
	
	private UpdateArticlesHandler updateArticlesHandler;
	private UpdateArticlesRunner updateArticlesRunner;
	
	private ArticleBundle bundle;
	private Map<String, View> viewsWaitingForTrailImages;

	boolean showSeperators = false;
	
	private Thread loader;
	private Date loaded;
	
	private Integer currentFontSize;
	private ColourScheme currentColourScheme;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.main);
		viewsWaitingForTrailImages = new HashMap<String, View>();
		articleDAO = SingletonFactory.getArticleDao(this.getApplicationContext());
		imageDAO = SingletonFactory.getImageDao(this.getApplicationContext());		
		networkStatusService = SingletonFactory.getNetworkStatusService(this.getApplicationContext());
		imageDownloadDecisionService = SingletonFactory.getImageDownloadDecisionService(this.getApplicationContext());		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);		
		if (shouldRefreshView(mainPane)) {
			setFontSize();
			mainPane.removeAllViews();
			
			final ArticleSet articleSet = getArticleSet();			
			if (articleDAO.isAvailable(articleSet)) {
				populateArticles(ContentFetchType.NORMAL, baseFontSize, articleSet);
			} else {
				outputNoArticlesWarning(baseFontSize);
			}
		}
	}

	
	@Override
	public void setFontSize() {
		super.setFontSize();
		TextView description = (TextView) findViewById(R.id.Description);
		if (description != null) {
			description.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
			description.setTextColor(colourScheme.getBodytext());
		}
	}

	
	protected void refresh() {
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);
		mainPane.removeAllViews();
		populateArticles(ContentFetchType.CHECKSUM, settingsDAO.getBaseFontSize(), getArticleSet());
	}
	
	
	protected int getPageSize() {
		return settingsDAO.getPageSizePreference();
	}
	
	
	private void populateArticles(ContentFetchType fetchType, int baseFontSize, ArticleSet articleSet) {			
		if (loader == null || !loader.isAlive()) {			
			updateArticlesHandler = new UpdateArticlesHandler(this, articleSet, baseFontSize, isLandScrapeOrientation());
			updateArticlesRunner = new UpdateArticlesRunner(articleDAO, imageDAO, imageDownloadDecisionService, fetchType, articleSet);
			updateArticlesHandler.init();
			
			loader = new Thread(updateArticlesRunner);
			loader.start();

			currentColourScheme = colourScheme;
			currentFontSize = settingsDAO.getBaseFontSize();
		}
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if (updateArticlesHandler != null) {
			updateArticlesRunner.stop();
		}
	}
	
	
	private boolean shouldRefreshView(LinearLayout mainPane) {		
		if (currentFontSize == null || currentFontSize != settingsDAO.getBaseFontSize()) {
			return true;
		}
		
		if (currentColourScheme == null) {
			return true;
		}
		if (colourScheme.getHeadline().intValue() != currentColourScheme.getHeadline().intValue()) {
			return true;
		}
				
		if (loaded == null || mainPane.getChildCount() == 0) {
			return true;
		}
		Date modtime = articleDAO.getModificationTime(getArticleSet());
		return modtime != null && modtime.after(loaded);
	}
	
	
	private ArticleBundle loadArticles(ContentFetchType fetchType, ArticleSet articleSet) {
		if (articleSet != null) {
			return articleDAO.getArticleSetArticles(articleSet, fetchType);
		}
		return null;
	}
	
	
	protected void enableMenuItemIfConnectionIsAvailable(MenuItem menuItem) {
		menuItem.setEnabled(networkStatusService.isConnectionAvailable());		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!super.onOptionsItemSelected(item)) {			
			switch (item.getItemId()) {
			case MenuedActivity.REFRESH:
				refresh();
				return true;
			}
		}
	    return false;
	}
		
	protected String getRefinementDescription(String refinementType) {
		return null;
	}

	protected abstract ArticleSet getArticleSet();
	
	
	
	private void outputNoArticlesWarning(float baseFontSize) {
		LinearLayout mainpane;
		mainpane = (LinearLayout) findViewById(R.id.MainPane);
		TextView noArticlesMessage = new TextView(this.getApplicationContext());
		noArticlesMessage.setText("No articles available.");
		
		noArticlesMessage.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
		noArticlesMessage.setTextColor(colourScheme.getHeadline());
		noArticlesMessage.setPadding(2, 3, 2, 3);					
		mainpane.addView(noArticlesMessage, 0);
	}
	
	
	class UpdateArticlesHandler extends Handler {		

		private static final int ARTICLE_READY = 1;
		private static final int TRAIL_IMAGE_IS_AVAILABLE_FOR_ARTICLE = 3;
		private static final int DESCRIPTION_TEXT_READY = 6;
		private static final int DRAW_REFINEMENTS = 4;
		private static final int SHOW_ARTICLE_SET_OUT_OF_DATE_WARNING = 5;
		public static final int NO_ARTICLES = 2;

		
		private Context context;
		boolean first = true;		
		Section currentSection;
		private ArticleSet articleSet;
		private boolean descriptionSet;
		private int baseFontSize;
		private ArticleSetFactory articleSetFactory;
		private ArticleListActivityViewPopulator articleListActivityViewPopulator;
		private boolean isLandscapeOrientation;
		
		public UpdateArticlesHandler(Context context, ArticleSet articleSet, int baseFontSize, boolean isLandscapeOrientation) {
			super();
			this.context = context;
			this.articleSetFactory = SingletonFactory.getArticleSetFactory(context);
			this.articleListActivityViewPopulator = new ArticleListActivityViewPopulator(context);
			this.articleSet = articleSet;
			this.descriptionSet = false;
			this.baseFontSize = baseFontSize;
			this.isLandscapeOrientation = isLandscapeOrientation;
			init();
		}
				
		public void init() {
			first = true;
			currentSection = null;
			descriptionSet = false;
		}
		
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
				
			    case ARTICLE_READY: 		
			    	Article article = (Article) msg.getData().getSerializable("article");				
			    	LayoutInflater mInflater = LayoutInflater.from(context);
			    	LinearLayout mainpane = (LinearLayout) findViewById(R.id.MainPane);
			    	
			    	if (article.getSection() != null) {
			    		if (currentSection == null || !currentSection.getId().equals(article.getSection().getId())) {
			    			if (showSeperators) {						
			    				ArticleSet articleSetForSection = articleSetFactory.getArticleSetForSection(article.getSection());
								articleListActivityViewPopulator.addSeperator(mInflater, mainpane, articleSetForSection, 
			    						articleDAO.isAvailable(articleSet), colourScheme, baseFontSize);
			    				first = true;
			    			}
			    		}
			    	}
			    	currentSection = article.getSection();
			    	
			    	boolean shouldUseFeatureTrail = article.getMainImageUrl() != null && first && !isLandscapeOrientation && articleSet.isFeatureTrailAllowed() && imageDAO.isAvailableLocally(article.getMainImageUrl());
					String trailImageUrl = article.getThumbnailUrl();
					if (shouldUseFeatureTrail) {
						trailImageUrl = article.getMainImageUrl();
					}					
					
					boolean isTrailImageAvailableLocally = trailImageUrl != null && imageDAO.isAvailableLocally(trailImageUrl);
					View articleTrailView = articleListActivityViewPopulator.populateArticleListView(article, colourScheme, baseFontSize, trailImageUrl, shouldUseFeatureTrail, first, mInflater, isTrailImageAvailableLocally);
					if (!isTrailImageAvailableLocally){
						viewsWaitingForTrailImages.put(article.getTrailImageCallBackLabelForArticle(), articleTrailView);
					}					
					mainpane.addView(articleTrailView);
					first = false;
					return;
					
					
			    case TRAIL_IMAGE_IS_AVAILABLE_FOR_ARTICLE:
			    	Bundle data = msg.getData();
			    	if (data.containsKey("id")) {
			    		final String id = data.getString("id");
			    		final String url = data.getString("url");
			    		if( viewsWaitingForTrailImages.containsKey(id)) {
			    			View view = viewsWaitingForTrailImages.get(id);
			    			articleListActivityViewPopulator.populateTrailImage(url, view);
			    			viewsWaitingForTrailImages.remove(id);
			    		}
			    	}			    
			    	return;
			    	
			    				    
			    case DESCRIPTION_TEXT_READY:
			    	mainpane = (LinearLayout) findViewById(R.id.MainPane);
			    	Bundle descriptionData = msg.getData();
			    	String descripton = descriptionData.getString("description");
			    	if (descripton != null && !descriptionSet) {	    	
			    		descriptionSet = articleListActivityViewPopulator.populateTagDescription(mainpane, descripton, baseFontSize, currentColourScheme);
			    	}
			    	return;
			    	
			    	
			    case DRAW_REFINEMENTS:			    	
			    	mainpane = (LinearLayout) findViewById(R.id.MainPane);
			    	Map<String, List<Refinement>> refinements = bundle.getRefinements();
			    				    	
			    	if (refinements != null && !refinements.isEmpty()) {
			    		LayoutInflater inflater = LayoutInflater.from(context);
			    		
			    		for (String refinementType : articleSet.getPermittedRefinements()) {
			    			Log.d(TAG, "Processing refinement type: " + refinementType);
			    			if (articleSet.getPermittedRefinements().contains(refinementType) && refinements.keySet().contains(refinementType)) {
			    				List<ArticleSet> refinementArticleSets = getRefinementArticleSets(refinements, refinementType, articleSet);
			    				if (!refinementArticleSets.isEmpty()) {
			    					articleListActivityViewPopulator.populateRefinementType(
			    							mainpane, inflater,
											getRefinementDescription(refinementType),
											refinementArticleSets,
											currentColourScheme, baseFontSize);
			    				}
			    			}
						}
			    		
			    	}
			    	return;
			    			
			    	
			    case SHOW_ARTICLE_SET_OUT_OF_DATE_WARNING: 
					mainpane = (LinearLayout) findViewById(R.id.MainPane);
					TextView message = new TextView(context);					
					if (networkStatusService.isConnectionAvailable()) {
						message.setText("This article set was last downloaded more than 2 hours ago. Refresh to check for updates.");
					} else {
						message.setText("This article set was last downloaded more than 2 hours ago and may be out of date.");
					}
					
					message.setTextColor(colourScheme.getStatus());
					message.setPadding(2, 3, 2, 3);
					mainpane.addView(message, 0);
					return;
					
			    case NO_ARTICLES:
			    	Log.i(TAG, "Displaying no articles available message");			    	
			    	outputNoArticlesWarning(baseFontSize);
			    	return;
			}
		}

		private List<ArticleSet> getRefinementArticleSets(Map<String, List<Refinement>> refinements, String refinementType, ArticleSet articleSet) {

			// TODO this is abit of a mess - could be method on refinement?
			List<ArticleSet> refinementArticleSets = new ArrayList<ArticleSet>();
			for (Refinement refinement : refinements.get(refinementType)) {
				
				if (refinementType.equals("type") && articleSet instanceof SectionArticleSet) {
					if (refinement.getTag().isGalleryTag()) {
						ArticleSet articleSetForRefinement = articleSetFactory.getArticleSetForTagCombiner(((SectionArticleSet) articleSet).getSection().getTag(), refinement.getTag());
						if (articleSetForRefinement != null) {
							refinementArticleSets.add(articleSetForRefinement);
						}
					}					
				} else {			
					ArticleSet articleSetForRefinement = articleSetFactory.getArticleSetForRefinement(articleSet, refinement);
					if (articleSetForRefinement != null) {
						refinementArticleSets.add(articleSetForRefinement);
					}
				}
			}
			return refinementArticleSets;
		}
		
	}
	
	
	private void sendArticleReadyMessage(Article article) {
		Message m = new Message();			
		m.what = UpdateArticlesHandler.ARTICLE_READY;
		Bundle bundle = new Bundle();
		bundle.putSerializable("article", article);			
		m.setData(bundle);		
		updateArticlesHandler.sendMessage(m);
	}
	
	
	
	private void sendDescriptionReadyMessage(String description) {
		Message m = new Message();			
		m.what = UpdateArticlesHandler.DESCRIPTION_TEXT_READY;
		Bundle bundle = new Bundle();
		bundle.putString("description", description);			
		m.setData(bundle);		
		updateArticlesHandler.sendMessage(m);
	}
	
	
	class UpdateArticlesRunner implements Runnable, ArticleCallback {		
		private boolean running;
		private ArticleDAO articleDAO;
		private ImageDAO imageDAO;
		private ContentFetchType fetchType;
		private ArticleSet articleSet;
		private ImageDownloadDecisionService imageDownloadDecisionService;
		
		public UpdateArticlesRunner(ArticleDAO articleDAO, ImageDAO imageDAO, ImageDownloadDecisionService imageDownloadDecisionService, ContentFetchType fetchType, ArticleSet articleSet) {
			this.articleDAO = articleDAO;
			this.imageDAO = imageDAO;
			this.imageDownloadDecisionService = imageDownloadDecisionService;
			this.running = true;
			articleDAO.setArticleReadyCallback(this);
			this.fetchType = fetchType;
			this.articleSet = articleSet;
		}
		
		public void run() {

			if (running) {
				bundle = loadArticles(fetchType, articleSet);
			}
			
			if (bundle == null) {
				Log.i(TAG, "Article bundle was null");
				Message m = new Message();
				m.what = UpdateArticlesHandler.NO_ARTICLES;
				updateArticlesHandler.sendMessage(m);
				return;
			}
			
			if (bundle.getDescription() != null) {
				sendDescriptionReadyMessage(bundle.getDescription());
			}
			
			Message m = new Message();
			m.what = UpdateArticlesHandler.DRAW_REFINEMENTS;
			updateArticlesHandler.sendMessage(m);
					
			final boolean isOkToDownloadTrailImages = imageDownloadDecisionService.isOkToDownloadTrailImages();
			List<Article> articlesToDownloadTrailImagesFor = new LinkedList<Article>();
			boolean first = true;
			for (Article article : bundle.getArticles()) {
				
				String imageUrl;
				boolean mainImageIsAvailableLocally = article.getMainImageUrl() != null && imageDAO.isAvailableLocally(article.getMainImageUrl());
				if (first && mainImageIsAvailableLocally) {						
					imageUrl = article.getMainImageUrl();
				} else {
					imageUrl = article.getThumbnailUrl();
				}
				
				if (imageUrl != null) {
					if (imageDAO.isAvailableLocally(imageUrl)) {
						m = new Message();
						m.what = UpdateArticlesHandler.TRAIL_IMAGE_IS_AVAILABLE_FOR_ARTICLE;						
						Bundle bundle = new Bundle();
						bundle.putString("id", article.getId());
						bundle.putString("url", imageUrl);
						
						m.setData(bundle);
						updateArticlesHandler.sendMessage(m);
						
					} else {
						if (isOkToDownloadTrailImages) {
							articlesToDownloadTrailImagesFor.add(article);
						}
					}					
				}
				
				first = false;
			}
			
			
			if (running) {
				for (Article article : articlesToDownloadTrailImagesFor) {
					imageDAO.getImage(article.getThumbnailUrl());
					
					m = new Message();
					m.what = UpdateArticlesHandler.TRAIL_IMAGE_IS_AVAILABLE_FOR_ARTICLE;
					Bundle bundle = new Bundle();
					bundle.putString("id", article.getTrailImageCallBackLabelForArticle());
					bundle.putString("url", article.getThumbnailUrl());
					
					m.setData(bundle);
					updateArticlesHandler.sendMessage(m);																				
				}		
			}
			
			if (bundle != null) {
				Date modificationTime = articleDAO.getModificationTime(articleSet);
				if (modificationTime != null && DateTimeHelper.isMoreThanHoursOld(modificationTime, 2)) {
					m = new Message();
					m.what = UpdateArticlesHandler.SHOW_ARTICLE_SET_OUT_OF_DATE_WARNING;
					Bundle bundle = new Bundle();
					bundle.putString("modtime", modificationTime.toString());					
					m.setData(bundle);
					updateArticlesHandler.sendMessage(m);
				}
				
			}
			
			loaded = DateTimeHelper.now();
			return;				
		}
		
		public void stop() {
			this.running = false;
			articleDAO.stopLoading();
		}

		@Override
		public void articleReady(Article article) {
			sendArticleReadyMessage(article);
		}

		@Override
		public void descriptionReady(String description) {
			sendDescriptionReadyMessage(description);			
		}
			
	}
	
}
