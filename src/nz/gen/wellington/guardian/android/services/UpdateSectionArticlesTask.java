package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import android.content.Context;
import android.util.Log;

public class UpdateSectionArticlesTask extends ArticleUpdateTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateSectionArticlesTask";
	private Section section;
		
	public UpdateSectionArticlesTask(Section section, Context context) {
		this.section = section;
		this.context = context;
		this.articleDAO = ArticleDAOFactory.getDao(context);
	}

	
	@Override
	public void run() {
		Log.i(TAG, "Fetching section articles");
		ArticleSet articleSet = new SectionArticleSet(section);
				
		ContentSource api = ArticleDAOFactory.getOpenPlatformApi(context);
		List<Section> sections = ArticleDAOFactory.getDao(context).getSections();
		
		List<Article> articles = api.getArticles(articleSet, sections, null);
		if (articles != null) {
			FileBasedArticleCache fileBasedArticleCache = new FileBasedArticleCache(context);
			fileBasedArticleCache.putArticleSetArticles(articleSet, new ArticleBundle(articles, api.getRefinements(), api.getChecksum()));
			processArticles(articles);
		}
	}
	
	
	@Override
	public String getTaskName() {
		return "Fetching articles for section: " + section.getName();
	}
		
}
