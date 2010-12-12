package nz.gen.wellington.guardian.android.factories;

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

	public static ArticleSet getAboutArticleSet(int pagesize) {
		return new AboutArticleSet(pagesize);
	}
	
	public static ArticleSet getArticleSetForSection(Section section, int pagesize) {
		return new SectionArticleSet(section, pagesize);
	}

	public static ArticleSet getFavouritesArticleSetFor(List<Section> favouriteSections, List<Tag> favouriteTags, int pagesize) {
		return new FavouriteStoriesArticleSet(favouriteSections, favouriteTags, pagesize);
	}

	public static ArticleSet getTopStoriesArticleSet(int pageSize) {
		return new TopStoriesArticleSet(pageSize);
	}

	public static ArticleSet getArticleSetForTag(Tag tag, int pageSize) {
		return new TagArticleSet(tag, pageSize);
	}
	
	public static List<ArticleSet> getArticleSetsForSections(List<Section> favouriteSections, int pagesize) {
		List<ArticleSet> favouriteSectionsArticleSets = new ArrayList<ArticleSet>();			
		for (Section section : favouriteSections) {
			favouriteSectionsArticleSets.add(ArticleSetFactory.getArticleSetForSection(section, pagesize));
		}
		return favouriteSectionsArticleSets;
	}

	public static List<ArticleSet> getArticleSetsForTags(List<Tag> favouriteTags, int pagesize) {
		List<ArticleSet> favouriteTagsArticleSets = new ArrayList<ArticleSet>();
		for (Tag tag : favouriteTags) {
			favouriteTagsArticleSets.add(ArticleSetFactory.getArticleSetForTag(tag, pagesize));
		}
		return favouriteTagsArticleSets;
	}
	
}
