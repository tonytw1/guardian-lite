package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class FavouriteStoriesArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;
	
	private List<Section> sections;
	private List<Tag> tags;
	
	protected String[] permittedRefinements = {};

	
	public FavouriteStoriesArticleSet(List<Section> sections, List<Tag> tags) {
		this.sections = sections;
		this.tags = tags;
	}
	
	@Override
	public String getName() {
		return "Favourites";
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}

	public List<Section> getSections() {
		return sections;
	}

	public List<Tag> getTags() {
		return tags;
	}
	
}