package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import android.content.Context;
import android.util.Log;

public class UpdateTopStoriesTask extends ArticleUpdateTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTopStoriesTask";
	
	
	public UpdateTopStoriesTask(Context context) {
		this.context = context;
		this.articleDAO = ArticleDAOFactory.getDao(context);
	}

	
	@Override
	public String getTaskName() {
		return "Fetching top stories";
	}
	
	
	@Override
	public void run() {
		Log.i(TAG, "Fetching latest articles");
		TopStoriesArticleSet articleSet = new TopStoriesArticleSet();
				
		ContentSource api = ArticleDAOFactory.getOpenPlatformApi(context);
		List<Section> sections = ArticleDAOFactory.getDao(context).getSections();
		
		ArticleBundle bundle = api.getArticles(articleSet, sections, null);
		if (bundle != null) {
			FileBasedArticleCache fileBasedArticleCache = new FileBasedArticleCache(context);
			fileBasedArticleCache.putArticleSetArticles(articleSet, bundle);
			processArticles(bundle.getArticles());
		}
	}
			
}
