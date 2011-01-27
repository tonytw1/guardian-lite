package nz.gen.wellington.guardian.android.content;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;

public interface ArticleSource {

	ArticleBundle getArticles(ArticleSet articleSet, ArticleCallback articleCallback);

}
