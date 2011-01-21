package nz.gen.wellington.guardian.android.model;

import java.util.Arrays;
import java.util.List;

public class TagArticleSet extends AbstractArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 2L;
	private Tag tag;
	private String date;
	
	private String[] permittedRefinements = {"keyword", "date"};

	public TagArticleSet(Tag tag, int pageSize) {
		super(pageSize);
		this.tag = tag;
	}
	
	public TagArticleSet(Tag tag, int pageSize, String date) {
		super(pageSize);
		this.tag = tag;
		this.date = date;
	}

	@Override
	public String getName() {
		String name = tag.getName();
		if (date != null) {
			name = name + " - " + date;
		}
		return name;
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public String getDate() {
		return date;
	}
	
	@Override
	public boolean isFeatureTrailAllowed() {
		return !tag.isContributorTag();
	}
	
}
