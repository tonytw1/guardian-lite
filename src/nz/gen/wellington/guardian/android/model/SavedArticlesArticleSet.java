package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SavedArticlesArticleSet extends AbstractArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;
	
	private List<String> articlesIds;
	
	private String[] permittedRefinements = {};
	
	public SavedArticlesArticleSet(List<String> articleIds) {
		super(0);
		this.articlesIds = articleIds;
	}
	
	@Override
	public String getName() {
		return "Saved articles";
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
	
	@Override
	public boolean isEmpty() {
		return articlesIds.isEmpty();
	}

	public List<String> getArticlesIds() {
		return articlesIds;
	}
	
}
