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
	private ArticleDAO articleDAO;
	private Section section;
	private Context context;
	private ContentUpdateReport report;

	public UpdateSectionArticlesTask(ArticleDAO articleDAO, Section section, Context context) {
		this.articleDAO = articleDAO;
		this.section = section;
		this.context = context;
	}

	@Override
	public void run() {
		articleDAO.evictArticleSet(new SectionArticleSet(section));
		Log.i(TAG, "Fetching section articles: " + section.getName());
		List<Article> sectionItems = articleDAO.getSectionItems(section);
		if (sectionItems != null) {
			for (Article article : sectionItems) {
				if (article.getThumbnailUrl() != null) {					
					ArticleDAOFactory.getTaskQueue().addImageTask(new ImageFetchTask(article.getThumbnailUrl(), context));
				}
				
				if (article.getMainImageUrl() != null) {					
					ArticleDAOFactory.getTaskQueue().addImageTask(new ImageFetchTask(article.getMainImageUrl(), context));
				}
				report.setArticleCount(report.getArticleCount()+1);
			}
		}
		report.setSectionCount(report.getSectionCount()+1);
	}

	@Override
	public void setReport(ContentUpdateReport report) {
		this.report = report;		
	}
		
}
