package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;


public class AuthorArticleSet implements ArticleSet, Serializable {

	private static final long serialVersionUID = 1L;
	private Tag author;
		
	public AuthorArticleSet(Tag author) {
		this.author = author;
	}

	@Override
	public String getName() {
		return author.getName();
	}
	
	@Override
	public String getApiUrl() {
		return author.getId();
	}

}
