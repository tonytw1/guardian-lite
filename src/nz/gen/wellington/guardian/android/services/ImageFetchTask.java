package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import android.content.Context;

public class ImageFetchTask implements ContentUpdateTaskRunnable {

	private String url;
	private Context context;
	private ContentUpdateReport report;

	public ImageFetchTask(String thumbnailUrl, Context context) {
		this.url = thumbnailUrl;
		this.context = context;
	}

	
	@Override
	public void run() {
		ImageDAO imageDao = ArticleDAOFactory.getImageDao(context);
		if (!imageDao.isAvailableLocally(url)) {
			imageDao.fetchLiveImage(url);
			report.setImageCount(report.getImageCount()+1);
		}
	}

	
	@Override
	public String getTaskName() {
		return "Fetching image: " + url;
	}


	@Override
	public void setReport(ContentUpdateReport report) {
		this.report = report;		
	}
	
}
