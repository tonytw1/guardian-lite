package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class TopStoriesArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;
	
	protected String[] permittedRefinements = {};
	
	@Override
	public String getName() {
		return "Top stories";
	}

	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
}
