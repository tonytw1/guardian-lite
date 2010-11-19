package nz.gen.wellington.guardian.android.model;

import java.util.Arrays;
import java.util.List;

public class TagArticleSet extends AbstractArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 1L;
	private Tag tag;
	
	private String[] permittedRefinements = {"keyword", "date"};

	public TagArticleSet(Tag tag, int pageSize) {
		super(pageSize);
		this.tag = tag;
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
