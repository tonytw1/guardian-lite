package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class TopStoriesArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;

	@Override
	public String getApiUrl() {
		return "topstories";
	}

	@Override
	public String getName() {
		return "Top stories";
	}

	
}
