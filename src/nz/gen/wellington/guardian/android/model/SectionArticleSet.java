package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SectionArticleSet extends AbstractArticleSet implements ArticleSet, Serializable {
	
	protected String[] permittedRefinements = {"blog", "keyword", "contributor"};
	
	private static final long serialVersionUID = 1L;
	private Section section;

	public SectionArticleSet(Section section, int pageSize) {
		super(pageSize);
		this.section = section;
	}

	@Override
	public String getName() {
		return section.getName();
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
	
	public Section getSection() {
		return section;
	}

}