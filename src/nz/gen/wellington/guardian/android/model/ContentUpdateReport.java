package nz.gen.wellington.guardian.android.model;

import nz.gen.wellington.guardian.android.dates.DateTimeHelper;

import org.joda.time.DateTime;


public class ContentUpdateReport {
	
	int sectionCount;
	int articleCount;
	int imageCount;
	DateTime startTime;
	
	
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


	public DateTime getStartTime() {
		return startTime;
	}
	
}
