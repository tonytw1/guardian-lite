package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class AboutArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;
	
	protected String[] permittedRefinements = {};
	
	@Override
	public String getName() {
		return "About";
	}

	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
}
