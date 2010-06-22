package nz.gen.wellington.guardian.android.services;

import java.util.List;

import android.content.Context;
import android.util.Log;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;

public abstract class ArticleUpdateTask {

	private static final String TAG = "ArticleUpdateTask";
	
	protected Context context;
	protected ContentUpdateReport report;
	protected ArticleDAO articleDAO;

	final public void setReport(ContentUpdateReport report) {
		this.report = report;
	}
	

	final public void stop() {
		Log.i(TAG, "Stopping");
		articleDAO.stopLoading();
	}
	
	
	final protected void processArticles(List<Article> articles) {
		if (articles != null) {
			for (Article article : articles) {
				queueImageDownloadIsNotAvailableLocally(article.getThumbnailUrl());
				queueImageDownloadIsNotAvailableLocally(article.getMainImageUrl());				
				report.setArticleCount(report.getArticleCount()+1);
			}
		}
	}
	
	
	final protected void queueImageDownloadIsNotAvailableLocally(String imageUrl) {
		if (imageUrl != null) {
			if (!ArticleDAOFactory.getImageDao(context).isAvailableLocally(imageUrl)) {
				Log.d(TAG, "Queuing file for fetching: " + imageUrl);
				ArticleDAOFactory.getTaskQueue(context).addImageTask(new ImageFetchTask(imageUrl, context));
			}
		}
	}
	
}
