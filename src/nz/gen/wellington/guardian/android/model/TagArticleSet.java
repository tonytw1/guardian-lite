package nz.gen.wellington.guardian.android.model;

import java.util.Arrays;
import java.util.List;

public class TagArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 1L;
	private Tag keyword;
	
	protected String[] permittedRefinements = {"keyword"};

	public TagArticleSet(Tag keyword) {
		this.keyword = keyword;
	}

	@Override
	public String getName() {
		return keyword.getName();
	}
	
	@Override
	public String getApiUrl() {
		return keyword.getId();
	}

	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}

}
