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
		StringBuilder name = new StringBuilder();
		if (tag.getSection() != null) {
			name.append(tag.getSection().getTag().getName() + " - ");
		}
		name.append(tag.getName());
		if (fromDate != null) {
			name.append(" (" + dateDisplayName + ")");
		}
		return name.toString();
	}
	
	@Override
	public String getShortName() {
		return tag.getName();
	}

	@Override
	public Section getSection() {
		return tag.getSection();
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		if (isDateRefinedArticleSet()) {
			return Arrays.asList("date");
		}
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
