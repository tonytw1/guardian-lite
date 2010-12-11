package nz.gen.wellington.guardian.android.model;

import java.util.Date;

import nz.gen.wellington.guardian.android.utils.DateTimeHelper;


public class ContentUpdateReport {
	
	private int sectionCount;
	private int articleCount;
	private int imageCount;
	private Date startTime;
	
	public ContentUpdateReport() {
		sectionCount = 0;
		articleCount = 0;
		imageCount = 0;
		startTime = DateTimeHelper.now();
	}


	public int getSectionCount() {
		return sectionCount;
	}


	public void setSectionCount(int sectionCount) {
		this.sectionCount = sectionCount;
	}


	public int getArticleCount() {
		return articleCount;
	}


	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}


	public int getImageCount() {
		return imageCount;
	}


	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}


	public Date getStartTime() {
		return startTime;
	}
	
}
