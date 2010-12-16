package nz.gen.wellington.guardian.android.activities.widgets;

import nz.gen.wellington.guardian.android.activities.main;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.app.Activity;

public class mainwidget extends WidgetClickthroughActivity {
	
	@Override
	protected ArticleSet getArticleSet() {
		ArticleSetFactory articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		return articleSetFactory.getTopStoriesArticleSet();
	}
	
	@Override
	protected Class<? extends Activity> getDefaultActivity() {
		return main.class;
	}

}
