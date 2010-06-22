package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;
import android.util.Log;

public class UpdateTagArticlesTask extends ArticleUpdateTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTagArticlesTask";
	private Tag tag;
	
	private ArticleDAO articleDAO;
	
	public UpdateTagArticlesTask(Tag tag, Context context) {
		this.tag = tag;
		this.context = context;
		this.articleDAO = ArticleDAOFactory.getDao(context);
	}

	@Override
	public void run() {
		articleDAO.evictArticleSet(new KeywordArticleSet(tag));
		Log.i(TAG, "Fetching tag articles: " + tag.getName());
		List<Article> sectionItems = articleDAO.getKeywordItems(tag);
		processArticles(sectionItems);
		report.setSectionCount(report.getSectionCount()+1);
	}
	
	@Override
	public String getTaskName() {
		return "Fetching articles for tag: " + tag.getName();
	}
			
}
