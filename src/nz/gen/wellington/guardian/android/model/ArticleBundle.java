package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

public class ArticleBundle implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	List<Article> articles;
	List<Tag> refinements;
	String checksum;
	DateTime timestamp;
	private String description;
	
	public ArticleBundle(List<Article> articles, List<Tag> refinements, String checksum, DateTime timestamp, String description) {
		this.articles = articles;
		this.refinements = refinements;
		this.checksum = checksum;
		this.timestamp = timestamp;
		this.description = description;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public List<Tag> getRefinements() {
		return refinements;
	}

	public String getChecksum() {
		return checksum;
	}
	
	public String getDescription() {
		return description;
	}

	public DateTime getTimestamp() {
		return timestamp;
	}
	
}
