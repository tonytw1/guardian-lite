package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SectionArticleSet extends AbstractArticleSet implements ArticleSet, Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String[] permittedRefinements = {"blog", "keyword", "contributor", "date"};
	private Section section;

	public SectionArticleSet(Section section, int pageSize) {
		super(pageSize);
		this.section = section;
	}

	public SectionArticleSet(Section section, int pageSize, String dateDisplayName, String fromDate, String toDate) {
		super(pageSize);
		this.section = section;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.dateDisplayName = dateDisplayName;
	}

	@Override
	public String getName() {
		String name = section.getName();
		if (fromDate != null) {
			name = name + " (" + dateDisplayName + ")";
		}
		return name;
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		if (isDateRefinedArticleSet()) {
			return Arrays.asList("date");
		}
		return Arrays.asList(permittedRefinements);
	}
	
	public Section getSection() {
		return section;
	}

	public String getHeadingColour() {
		return section.getColour();
	}

}