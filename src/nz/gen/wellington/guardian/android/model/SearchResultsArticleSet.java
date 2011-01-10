package nz.gen.wellington.guardian.android.model;

import java.util.Arrays;
import java.util.List;

public class SearchResultsArticleSet extends AbstractArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 1L;
	private String searchTerm;
	
	private String[] permittedRefinements = {"keyword", "date"};

	public SearchResultsArticleSet(String searchterm, int pageSize) {
		super(pageSize);
		this.searchTerm = searchterm;
	}
	
	@Override
	public String getName() {
		return searchTerm + " (search)";
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}

	public String getSearchTerm() {
		return searchTerm;
	}
	
}
