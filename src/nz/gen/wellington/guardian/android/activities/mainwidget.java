package nz.gen.wellington.guardian.android.activities;

import android.app.Activity;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;

public class mainwidget extends WidgetClickthroughActivity {
	
	@Override
	protected TopStoriesArticleSet getArticleSet() {
		return new TopStoriesArticleSet();
	}
	
	@Override
	protected Class<? extends Activity> getDefaultActivity() {
		return main.class;
	}

}
