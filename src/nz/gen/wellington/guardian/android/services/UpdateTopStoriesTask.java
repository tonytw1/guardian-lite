package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
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
	public void run() {
		articleDAO.evictArticleSet(new TopStoriesArticleSet());
		Log.i(TAG, "Fetching latest articles");
		
		List<Article> items = articleDAO.getTopStories();
		processArticles(items);
	}
	
	@Override
	public String getTaskName() {
		return "Fetching top stories";
	}
		
}
