package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public abstract class AbstractArticleSet implements Serializable {
	
	private static final long serialVersionUID = 2L;
	private int pageSize;
	private String sourceUrl;
	

	public AbstractArticleSet(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	
}
