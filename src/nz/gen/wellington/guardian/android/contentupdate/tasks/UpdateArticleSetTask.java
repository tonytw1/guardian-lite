/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.contentupdate.tasks;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.ImageDownloadDecisionService;
import nz.gen.wellington.guardian.android.contentupdate.TaskQueue;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.model.Article;
import nz.gen.wellington.guardian.model.MediaElement;
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
				if (article.getThumbnail() != null && imageDownloadDecisionService.isOkToDownloadTrailImages()) {
					String description = article.getHeadline() + " - trail image";
					queueImageDownloadIfNotAvailableLocally(article.getThumbnail(), description);
				}
				if (article.getMainImageUrl() != null && imageDownloadDecisionService.isOkToDownloadMainImages()) {
					String description = article.getHeadline() + " - main image";					
					final MediaElement mainPicture = article.getMainPictureMediaElement();
					if (mainPicture != null && mainPicture.getCaption() != null) {
						description = mainPicture.getCaption();
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
