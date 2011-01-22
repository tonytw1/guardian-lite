package nz.gen.wellington.guardian.android.model;

import java.util.Arrays;
import java.util.List;

public class TagArticleSet extends AbstractArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 2L;
	private Tag tag;
	private String fromDate;
	private String toDate;
	private String dateDisplayName;
	
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
	
	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public String getFromDate() {
		return fromDate;
	}
	
	public String getToDate() {
		return toDate;
	}

	@Override
	public boolean isFeatureTrailAllowed() {
		return !tag.isContributorTag();
	}
	
}
