package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class SectionArticleSet implements ArticleSet, Serializable {
	
	private static final long serialVersionUID = 1L;
	private Section section;

	public SectionArticleSet(Section section) {
		this.section = section;
	}

	@Override
	public String getName() {
		return section.getName();
	}
	
	@Override
	public String getApiUrl() {
		return section.getApiUrl();
	}
}
