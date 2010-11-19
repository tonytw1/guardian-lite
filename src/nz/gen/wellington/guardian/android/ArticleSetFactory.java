package nz.gen.wellington.guardian.android;

import java.util.List;

import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;

public class ArticleSetFactory {

	public static ArticleSet getAboutArticleSet() {
		return new AboutArticleSet(20);
	}

	public static ArticleSet getArticleSetForSection(Section section) {
		return new SectionArticleSet(section, 20);
	}

	public static ArticleSet getFavouritesArticleSetFor(List<Section> favouriteSections, List<Tag> favouriteTags) {
		return new FavouriteStoriesArticleSet(favouriteSections, favouriteTags, 20);
	}

	public static ArticleSet getTopStoriesArticleSet() {
		return new TopStoriesArticleSet(20);
	}

	public static ArticleSet getArticleSetForTag(Tag tag) {
		return new TagArticleSet(tag, 20);
	}
	
	

}
