package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;

public class mainwidget extends WidgetClickthroughActivity {
	
	protected TopStoriesArticleSet getArticleSet() {
		return new TopStoriesArticleSet();
	}

}
