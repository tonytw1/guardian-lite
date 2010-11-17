package nz.gen.wellington.guardian.android.model;

import java.util.Arrays;
import java.util.List;

public class TagArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 1L;
	private Tag tag;
	
	protected String[] permittedRefinements = {"keyword"};

	public TagArticleSet(Tag tag) {
		this.tag = tag;
	}

	@Override
	public String getApiUrl() {
		return tag.getId();
	}
	
	@Override
	public String getName() {
		return tag.getName();
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}

	public Tag getTag() {
		return tag;
	}

}
