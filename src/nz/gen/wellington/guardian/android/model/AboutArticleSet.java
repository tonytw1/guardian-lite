package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class AboutArticleSet extends AbstractArticleSet implements Serializable, ArticleSet {
	
	private static final long serialVersionUID = 1L;
	
	private String[] permittedRefinements = {};
	
	public AboutArticleSet(int pageSize) {
		super(pageSize);
	}
	
	@Override
	public String getName() {
		return "About";
	}

	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
	
	
}
