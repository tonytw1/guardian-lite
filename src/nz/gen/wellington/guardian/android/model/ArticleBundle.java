package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

public class ArticleBundle implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	List<Article> articles;
	List<Tag> refinements;
	String checksum;
	
	public ArticleBundle(List<Article> articles, List<Tag> refinements, String checksum) {
		this.articles = articles;
		this.refinements = refinements;
		this.checksum = checksum;		
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
	
	
	
}
