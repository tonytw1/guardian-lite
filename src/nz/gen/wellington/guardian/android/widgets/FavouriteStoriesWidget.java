package nz.gen.wellington.guardian.android.widgets;

import nz.gen.wellington.guardian.android.activities.widgets.favouritewidget;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FavouriteStoriesWidget extends AbstractWidget {

	@Override
	protected ArticleSet getArticleSet(int pagesize, Context context) {
		ArticleSetFactory articleSetFactory = SingletonFactory.getArticleSetFactory(context);	// TODO should be a field
		return articleSetFactory.getFavouritesArticleSet();
	}
	
	@Override
	protected String getNoArticlesExplainationText() {
		return "You may not have any favourites set or the articles may not have synced yet";
	}

	@Override
	protected PendingIntent createShowArticleIntent(Context context, Article article) {
		Intent intent = new Intent(context, favouritewidget.class);		
		intent.setData(Uri.withAppendedPath(Uri.parse("content://article/id"), String.valueOf(article.getId())));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		return pendingIntent;
	}
	
}
