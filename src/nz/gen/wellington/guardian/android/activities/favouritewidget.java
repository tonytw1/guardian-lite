package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.FavouriteStoriesArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.Activity;

public class favouritewidget extends Activity {
	
	protected ArticleSet getArticleSet() {	
		FavouriteSectionsAndTagsDAO favouriteSectionAndTagsDAO = ArticleDAOFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());		
		List<Section> favouriteSections = favouriteSectionAndTagsDAO.getFavouriteSections();
		List<Tag> favouriteTags = favouriteSectionAndTagsDAO.getFavouriteTags();
		
		if (!favouriteSections.isEmpty() || !favouriteTags.isEmpty()) {
			return new FavouriteStoriesArticleSet(favouriteSections, favouriteTags);			
		}
		return null;
	}

}
