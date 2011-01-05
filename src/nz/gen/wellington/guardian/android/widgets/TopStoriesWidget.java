package nz.gen.wellington.guardian.android.widgets;

import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.content.Context;

public class TopStoriesWidget extends AbstractWidget {

	@Override
	protected ArticleSet getArticleSet(int pagesize, Context context) {
		return articleSetFactory.getTopStoriesArticleSet();
	}
	
	@Override
	protected String getNoArticlesExplainationText() {
		return "You may need to sync this article set";
	}
	
}
