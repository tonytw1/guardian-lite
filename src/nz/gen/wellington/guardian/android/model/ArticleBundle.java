package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ArticleBundle implements Serializable {
	
	private static final long serialVersionUID = 3L;
	
	List<Article> articles;
	Map<String, List<Tag>> refinements;
	String checksum;
	Date timestamp;
	private String description;
	
	public ArticleBundle(List<Article> articles, Map<String, List<Tag>> refinements, String checksum, Date timestamp, String description) {
		this.articles = articles;
		this.refinements = refinements;
		this.checksum = checksum;
		this.timestamp = timestamp;
		this.description = description;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public Map<String, List<Tag>> getRefinements() {
		return refinements;
	}

	public String getChecksum() {
		return checksum;
	}
	
	public String getDescription() {
		return description;
	}

	public Date getTimestamp() {
		return timestamp;
	}
	
}
