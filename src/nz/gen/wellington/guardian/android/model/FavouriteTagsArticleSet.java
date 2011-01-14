package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

public class FavouriteTagsArticleSet extends AbstractArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 2L;
	
	private List<ArticleSet> articleSets;
		
	public FavouriteTagsArticleSet(List<ArticleSet> favouriteArticleSets, int pageSizePreference) {
		super(pageSizePreference);
		this.articleSets = favouriteArticleSets;
	}

	@Override
	public String getName() {
		return "Favourites";
	}
	
	
	public List<ArticleSet> getArticleSets() {
		return articleSets;
	}

	@Override
	public boolean isEmpty() {
		return articleSets.isEmpty();
	}
	
	@Override
	public boolean isFeatureTrailAllowed() {
		return false;
	}
	
}
