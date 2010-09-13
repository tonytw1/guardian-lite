package nz.gen.wellington.guardian.android.tasks;

import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;

public class ImageFetchTask implements ContentUpdateTaskRunnable {

	private String url;
	private ContentUpdateReport report;
	private String description;	
	private ImageDAO imageDao;
	
	public ImageFetchTask(String thumbnailUrl, String description, ImageDAO imageDAO) {
		this.url = thumbnailUrl;
		this.description = description;
		this.imageDao = imageDAO;
	}
	
	@Override
	public void run() {
		if (!imageDao.isAvailableLocally(url)) {
			imageDao.fetchLiveImage(url);
			report.setImageCount(report.getImageCount()+1);
		}
	}
	
	@Override
	public void stop() {
		// TODO
	}
	
	@Override
	public String getTaskName() {
		if (description != null) {
			return "Fetching image: " + description;
		}
		return "Fetching image: " + url;
	}
	
	@Override
	public void setReport(ContentUpdateReport report) {
		this.report = report;		
	}
	
}
