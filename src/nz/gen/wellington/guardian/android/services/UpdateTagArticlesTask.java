package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;
import android.util.Log;

public class UpdateTagArticlesTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTagArticlesTask";
	private Tag tag;
	private Context context;
	private ContentUpdateReport report;
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
		if (sectionItems != null) {
			for (Article article : sectionItems) {
				queueImageDownloadIsNotAvailableLocally(article.getThumbnailUrl());
				queueImageDownloadIsNotAvailableLocally(article.getMainImageUrl());				
				report.setArticleCount(report.getArticleCount()+1);
			}
		}
		report.setSectionCount(report.getSectionCount()+1);
	}
	
	
	@Override
	public void stop() {
		Log.i(TAG, "Stopping");
		articleDAO.stopLoading();
	}

	
	@Override
	public String getTaskName() {
		return "Fetching articles for tag: " + tag.getName();
	}
	
	
	@Override
	public void setReport(ContentUpdateReport report) {
		this.report = report;
	}
	
	private void queueImageDownloadIsNotAvailableLocally(String imageUrl) {
		if (imageUrl != null) {
			if (!ArticleDAOFactory.getImageDao(context).isAvailableLocally(imageUrl)) {
				Log.d(TAG, "Queuing file for fetching: " + imageUrl);
				ArticleDAOFactory.getTaskQueue(context).addImageTask(new ImageFetchTask(imageUrl, context));
			}
		}
	}

		
}
