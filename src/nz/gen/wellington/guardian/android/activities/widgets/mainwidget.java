package nz.gen.wellington.guardian.android.activities.widgets;

import nz.gen.wellington.guardian.android.activities.main;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.app.Activity;

public class mainwidget extends WidgetClickthroughActivity {
	
	@Override
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getTopStoriesArticleSet();
	}
	
	@Override
	protected Class<? extends Activity> getDefaultActivity() {
		return main.class;
	}

}
