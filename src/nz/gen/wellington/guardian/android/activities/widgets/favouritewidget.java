package nz.gen.wellington.guardian.android.activities.widgets;

import nz.gen.wellington.guardian.android.activities.favourites;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.app.Activity;

public class favouritewidget extends WidgetClickthroughActivity {
	
	@Override
	protected ArticleSet getArticleSet() {			
		return articleSetFactory.getFavouritesArticleSet();		
	}

	@Override
	protected Class<? extends Activity> getDefaultActivity() {
		return favourites.class;
	}

}
