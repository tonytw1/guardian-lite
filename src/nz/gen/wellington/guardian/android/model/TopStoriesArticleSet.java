package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class TopStoriesArticleSet extends AbstractArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;
		
	public TopStoriesArticleSet(int pageSize) {
		super(pageSize);
	}

	@Override
	public String getName() {
		return "Top stories";
	}
	
	@Override
	public boolean isFeatureTrailAllowed() {
		return false;
	}
	
}
