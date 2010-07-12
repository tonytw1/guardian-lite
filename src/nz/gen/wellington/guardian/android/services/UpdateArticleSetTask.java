package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import android.content.Context;
import android.util.Log;

public class UpdateArticleSetTask extends ArticleUpdateTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTopStoriesTask";
	private ArticleSet articleSet;
	private int pageSize;
	
	
	public UpdateArticleSetTask(Context context, ArticleSet articleSet, int pageSize) {
		this.context = context;
		this.articleDAO = ArticleDAOFactory.getDao(context);
		this.articleSet = articleSet;
		this.pageSize = pageSize;
	}

	
	@Override
	public String getTaskName() {
		return "Fetching " + articleSet.getName();
	}
	
	
	@Override
	public void run() {				
		ContentSource api = ArticleDAOFactory.getOpenPlatformApi(context);
		List<Section> sections = ArticleDAOFactory.getDao(context).getSections();
			
		ArticleBundle bundle = api.getArticles(articleSet, sections, null, pageSize);
		if (bundle != null) {
			FileBasedArticleCache fileBasedArticleCache = new FileBasedArticleCache(context);
			fileBasedArticleCache.putArticleSetArticles(articleSet, bundle);
			processArticles(bundle.getArticles());
		}
	}
	
}
