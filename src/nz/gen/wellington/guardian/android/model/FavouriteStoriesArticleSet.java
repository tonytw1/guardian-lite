package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

public class FavouriteStoriesArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;
	
	private List<Section> sections;
	private List<Tag> tags;
	
	public FavouriteStoriesArticleSet(List<Section> sections, List<Tag> tags) {
		this.sections = sections;
		this.tags = tags;
	}

	@Override
	public String getApiUrl() {
		StringBuilder query = new StringBuilder("");
		query.append("&sections=");
		boolean first = true;
		for (Section section : sections) {
			if (!first) {
				query.append(",");
			}
			query.append(section.getId());
			first = false;
		}
		
		query.append("&tags=");
		first = true;
		for (Tag tag : tags) {
			if (!first) {
				query.append(",");
			}
			query.append(tag.getId());
			first = false;
		}
		return query.toString();
	}

	@Override
	public String getName() {
		return "Favourites";
	}
	
}
