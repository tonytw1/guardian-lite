package nz.gen.wellington.guardian.android.model;

import java.util.ArrayList;
import java.util.List;

public class ContentUpdateReport {
	
	int sectionCount;
	int articleCount;
	int imageCount;
	List<Article> topStories;
	
	
	public ContentUpdateReport() {
		sectionCount = 0;
		articleCount = 0;
		imageCount = 0;
		topStories = new ArrayList<Article>();
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


	public void addTopStories(Article article) {
		this.topStories.add(article);		
	}


	public List<Article> getTopStories() {
		return topStories;
	}
	
}
