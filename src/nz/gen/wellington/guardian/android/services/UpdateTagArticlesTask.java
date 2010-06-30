package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;
import android.util.Log;

public class UpdateTagArticlesTask extends ArticleUpdateTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTagArticlesTask";
	private Tag tag;
		
	public UpdateTagArticlesTask(Tag tag, Context context) {
		this.tag = tag;
		this.context = context;
		this.articleDAO = ArticleDAOFactory.getDao(context);
	}

	
	@Override
	public void run() {
		Log.i(TAG, "Fetching tags articles");
		ArticleSet articleSet = new KeywordArticleSet(tag);
				
		ContentSource api = ArticleDAOFactory.getOpenPlatformApi(context);
		List<Section> sections = ArticleDAOFactory.getDao(context).getSections();
		
		ArticleBundle bundle = api.getArticles(articleSet, sections, null);
		if (bundle != null) {
			FileBasedArticleCache fileBasedArticleCache = new FileBasedArticleCache(context);
			fileBasedArticleCache.putArticleSetArticles(articleSet, bundle);
			processArticles(bundle.getArticles());
		}
	}
	
	@Override
	public String getTaskName() {
		return "Fetching articles for tag: " + tag.getName();
	}
			
}
