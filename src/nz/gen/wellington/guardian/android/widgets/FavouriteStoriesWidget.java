/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.widgets;

import nz.gen.wellington.guardian.android.activities.widgets.favouritewidget;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.model.Article;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FavouriteStoriesWidget extends AbstractArticleSetWidget {

	@Override
	protected ArticleSet getArticleSet(int pagesize, Context context) {
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
