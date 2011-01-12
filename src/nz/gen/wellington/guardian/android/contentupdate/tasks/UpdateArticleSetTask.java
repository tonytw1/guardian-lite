package nz.gen.wellington.guardian.android.contentupdate.tasks;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.ImageDownloadDecisionService;
import nz.gen.wellington.guardian.android.contentupdate.TaskQueue;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import android.content.Context;

public class UpdateArticleSetTask implements ContentUpdateTaskRunnable {
	
	private ImageDAO imageDAO;
	private TaskQueue taskQueue;
	
	protected ContentUpdateReport report;
	protected ArticleDAO articleDAO;
	protected boolean running = true;
	private ArticleSet articleSet;
	private ImageDownloadDecisionService imageDownloadDecisionService;
		
	public UpdateArticleSetTask(Context context, ArticleSet articleSet) {
		articleDAO = SingletonFactory.getArticleDao(context);
		imageDAO = SingletonFactory.getImageDao(context);
		taskQueue = SingletonFactory.getTaskQueue(context);
		imageDownloadDecisionService = SingletonFactory.getImageDownloadDecisionService(context);	
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

	@Override
	final public void setReport(ContentUpdateReport report) {
		this.report = report;
	}

	@Override
	public void stop() {
		articleDAO.stopLoading();
		running = false;
	}
	
		
	private void processArticles(List<Article> articles) {
		if (articles != null) {
			for (Article article : articles) {
				if (article.getThumbnailUrl() != null && imageDownloadDecisionService.isOkToDownloadTrailImages()) {
					String description = article.getTitle() + " - trail image";
					queueImageDownloadIfNotAvailableLocally(article.getThumbnailUrl(), description);
				}
				if (article.getMainImageUrl() != null && imageDownloadDecisionService.isOkToDownloadMainImages()) {
					String description = article.getTitle() + " - main image";
					if (article.getCaption() != null) {
						description = article.getCaption();
					}
					queueImageDownloadIfNotAvailableLocally(article.getMainImageUrl(), description);
				}
				report.setArticleCount(report.getArticleCount()+1);
			}
		}		
	}
	
	
	private void queueImageDownloadIfNotAvailableLocally(String imageUrl, String description) {
		if (imageUrl != null && running) {
			if (!imageDAO.isAvailableLocally(imageUrl)) {
				taskQueue.addImageTask(new ImageFetchTask(imageUrl, description, imageDAO));
			}
		}
	}
	
}
