package nz.gen.wellington.guardian.android;

import java.util.ArrayList;
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
		return new AboutArticleSet(getPreferedPageSize());
	}
	
	public static ArticleSet getArticleSetForSection(Section section) {
		return new SectionArticleSet(section, getPreferedPageSize());
	}

	public static ArticleSet getFavouritesArticleSetFor(List<Section> favouriteSections, List<Tag> favouriteTags) {
		return new FavouriteStoriesArticleSet(favouriteSections, favouriteTags, getPreferedPageSize());
	}

	public static ArticleSet getTopStoriesArticleSet() {
		return new TopStoriesArticleSet(getPreferedPageSize());
	}

	public static ArticleSet getArticleSetForTag(Tag tag) {
		return new TagArticleSet(tag, getPreferedPageSize());
	}
	
	public static List<ArticleSet> getArticleSetsForSections(List<Section> favouriteSections) {
		List<ArticleSet> favouriteSectionsArticleSets = new ArrayList<ArticleSet>();			
		for (Section section : favouriteSections) {
			favouriteSectionsArticleSets.add(ArticleSetFactory.getArticleSetForSection(section));
		}
		return favouriteSectionsArticleSets;
	}

	public static List<ArticleSet> getArticleSetsForTags(List<Tag> favouriteTags) {
		List<ArticleSet> favouriteTagsArticleSets = new ArrayList<ArticleSet>();
		for (Tag tag : favouriteTags) {
			favouriteTagsArticleSets.add(ArticleSetFactory.getArticleSetForTag(tag));
		}
		return favouriteTagsArticleSets;
	}
	
	private static int getPreferedPageSize() {
		return 15;	// TODO wired into preferences doa.
	}

}
