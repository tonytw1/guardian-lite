package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.PreferencesDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.content.Context;

@Deprecated // TODO only one extender
public abstract class ArticleUpdateTask {
	
	private PreferencesDAO preferencesDAO;
	private NetworkStatusService networkStatusService;
	private ImageDAO imageDAO;
	private TaskQueue taskQueue;
	
	protected ContentUpdateReport report;
	protected ArticleDAO articleDAO;
	protected boolean running = true;	
	
	
	final public void setReport(ContentUpdateReport report) {
		this.report = report;
	}
	
	
	public ArticleUpdateTask(Context context) {
		preferencesDAO = ArticleDAOFactory.getPreferencesDAO(context);
		imageDAO = ArticleDAOFactory.getImageDao(context);
		networkStatusService = new NetworkStatusService(context);
		taskQueue = ArticleDAOFactory.getTaskQueue(context);
	}

	
	final public void stop() {
		articleDAO.stopLoading();
		running = false;
	}
	
	
	final protected void processArticles(List<Article> articles) {
		final boolean queueMainImagesForDownload = preferencesDAO.getLargePicturesPreference() || networkStatusService.isWifiConnection();		
		if (articles != null) {
			for (Article article : articles) {
				if (article.getThumbnailUrl() != null) {
					String description = article.getTitle() + " - trail image";					
					queueImageDownloadIfNotAvailableLocally(article.getThumbnailUrl(), description);
				}
				if (queueMainImagesForDownload && article.getMainImageUrl() != null) {
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
	
	
	final protected void queueImageDownloadIfNotAvailableLocally(String imageUrl, String description) {
		if (imageUrl != null && running) {
			if (!imageDAO.isAvailableLocally(imageUrl)) {
				taskQueue.addImageTask(new ImageFetchTask(imageUrl, description, imageDAO));
			}
		}
	}
	
}
