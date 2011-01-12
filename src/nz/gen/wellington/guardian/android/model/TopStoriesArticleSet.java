package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class TopStoriesArticleSet extends AbstractArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;
	
	private String[] permittedRefinements = {};
	
	public TopStoriesArticleSet(int pageSize) {
		super(pageSize);
	}

	@Override
	public String getName() {
		return "Top stories";
	}

	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}

	@Override
	public boolean isFeatureTrailAllowed() {
		return false;
	}
	
}
