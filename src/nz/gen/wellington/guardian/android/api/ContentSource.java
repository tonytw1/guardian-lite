package nz.gen.wellington.guardian.android.api;

import java.util.List;

import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;

/*
 * This interface is here to remind you that you can source content from the Content API,
 * the RSS feeds or elsewhere. You could add implementations for entirely different publications.
 */

public interface ContentSource {

	public List<Section> getSections();
	List<Article> getArticles(ArticleSet articleSet, List<Section> sections);

}
