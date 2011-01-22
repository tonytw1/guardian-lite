package nz.gen.wellington.guardian.android.model;

import java.util.Arrays;
import java.util.List;

public class TagArticleSet extends AbstractArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 2L;
	private Tag tag;
	
	private String[] permittedRefinements = {"keyword", "date"};

	public TagArticleSet(Tag tag, int pageSize) {
		super(pageSize);
		this.tag = tag;
	}
	
	public TagArticleSet(Tag tag, int pageSize, String dateDisplayName, String fromDate, String toDate) {
		super(pageSize);
		this.tag = tag;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.dateDisplayName = dateDisplayName;
	}

	@Override
	public String getName() {
		String name = tag.getName();
		if (fromDate != null) {
			name = name + " (" + dateDisplayName + ")";
		}
		return name;
	}
	
	public String getHeadingColour() {
		if (tag.getSection() != null) {
			return tag.getSection().getColour();
		}
		return null;
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
	
	public Tag getTag() {
		return tag;
	}
	
	@Override
	public boolean isFeatureTrailAllowed() {
		return !tag.isContributorTag();
	}
	
}
