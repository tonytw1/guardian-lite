package nz.gen.wellington.guardian.android.about;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;

public interface ArticleSource {

	ArticleBundle getArticles(ArticleSet articleSet, ArticleCallback articleCallback);

}
