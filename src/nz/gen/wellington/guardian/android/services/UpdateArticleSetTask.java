package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.content.Context;

public class UpdateArticleSetTask extends ArticleUpdateTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTopStoriesTask";
	private ArticleSet articleSet;
		
	public UpdateArticleSetTask(Context context, ArticleSet articleSet) {
		this.context = context;
		this.articleDAO = ArticleDAOFactory.getDao(context);
		this.articleSet = articleSet;
	}

	
	@Override
	public String getTaskName() {
		return "Fetching " + articleSet.getName();
	}
	
	
	@Override
	public void run() {
		ArticleBundle bundle = articleDAO.getArticleSetArticles(articleSet, ContentFetchType.CHECKSUM);
		if (bundle != null) {
			processArticles(bundle.getArticles());
		}
	}
	
}
