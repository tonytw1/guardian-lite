package nz.gen.wellington.guardian.android.widgets;

import nz.gen.wellington.guardian.android.activities.favourites;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.content.Context;
import android.content.Intent;

public class FavouriteStoriesWidget extends TopStoriesWidget {

	@Override
	protected ArticleBundle getArticleSet(Context context) {	// TODO return article set, not it's articles
		ArticleDAO articleDAO = ArticleDAOFactory.getDao(context);
		FavouriteSectionsAndTagsDAO favouritesDAO = ArticleDAOFactory.getFavouriteSectionsAndTagsDAO(context);
		if (favouritesDAO.hasFavourites()) {
			ArticleSet favouriteArticlesSet = new FavouriteStoriesArticleSet(favouritesDAO.getFavouriteSections(), favouritesDAO.getFavouriteTags());
			return articleDAO.getArticleSetArticles(favouriteArticlesSet, ContentFetchType.LOCAL_ONLY);
		}
		return null;
	}
	
	@Override
	protected Intent getClickIntent(Context context) {
		return new Intent(context, favourites.class);

	}
	
}
