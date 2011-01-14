package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class AboutArticleSet extends AbstractArticleSet implements Serializable, ArticleSet {
	
	private static final long serialVersionUID = 1L;
		
	public AboutArticleSet(int pageSize) {
		super(pageSize);
	}
	
	@Override
	public String getName() {
		return "About";
	}
	
	@Override
	public boolean isFeatureTrailAllowed() {
		return false;
	}
	
}
