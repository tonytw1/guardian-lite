package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

public class ArticleBundle implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	List<Article> articles;
	List<Tag> refinements;
	
	
	public ArticleBundle(List<Article> articles, List<Tag> refinements) {
		this.articles = articles;
		this.refinements = refinements;
	}


	public List<Article> getArticles() {
		return articles;
	}


	public List<Tag> getRefinements() {
		return refinements;
	}
	
	
	
}
