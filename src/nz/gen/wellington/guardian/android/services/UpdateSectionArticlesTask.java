package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import android.content.Context;
import android.util.Log;

public class UpdateSectionArticlesTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateSectionArticlesTask";
	private Section section;
	private Context context;
	private ContentUpdateReport report;
	
	public UpdateSectionArticlesTask(Section section, Context context) {
		this.section = section;
		this.context = context;
	}

	@Override
	public void run() {
		ArticleDAO articleDAO = ArticleDAOFactory.getDao(context);
		articleDAO.evictArticleSet(new SectionArticleSet(section));
		Log.i(TAG, "Fetching section articles: " + section.getName());
		List<Article> sectionItems = articleDAO.getSectionItems(section);
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
	public String getTaskName() {
		return "Fetching articles for section: " + section.getName();
	}

	
	@Override
	public void setReport(ContentUpdateReport report) {
		this.report = report;
	}
	
	private void queueImageDownloadIsNotAvailableLocally(String imageUrl) {
		if (imageUrl != null) {
			if (!ArticleDAOFactory.getImageDao(context).isAvailableLocally(imageUrl)) {
				Log.d(TAG, "Queuing file for fetching: " + imageUrl);
				ArticleDAOFactory.getTaskQueue().addImageTask(new ImageFetchTask(imageUrl, context));
			}
		}
	}
		
}
