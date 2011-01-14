package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractArticleSet implements ArticleSet, Serializable {
	
	private static final long serialVersionUID = 2L;
	private int pageSize;
	private String sourceUrl;
	
	private String[] permittedRefinements = {};

	public AbstractArticleSet(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public String getSourceUrl() {
		return sourceUrl;
	}

	@Override
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isFeatureTrailAllowed() {
		return true;
	}

	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
	
}
