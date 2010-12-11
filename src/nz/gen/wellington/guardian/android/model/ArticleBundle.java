package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ArticleBundle implements Serializable {
	
	private static final long serialVersionUID = 5L;
	
	private List<Article> articles;
	private Map<String, List<ArticleSet>> refinements;
	private String checksum;
	private String description;
	
	public ArticleBundle(List<Article> articles, Map<String, List<ArticleSet>> refinements, String checksum, String description) {
		this.articles = articles;
		this.refinements = refinements;
		this.checksum = checksum;
		this.description = description;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public Map<String, List<ArticleSet>> getRefinements() {
		return refinements;
	}

	public String getChecksum() {
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public String getDescription() {
		return description;
	}
	
}
