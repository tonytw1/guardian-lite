package nz.gen.wellington.guardian.android.activities.widgets;

import java.util.List;

import nz.gen.wellington.guardian.android.activities.favourites;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.app.Activity;

public class favouritewidget extends WidgetClickthroughActivity {
	
	@Override
	protected ArticleSet getArticleSet() {	
		FavouriteSectionsAndTagsDAO favouriteSectionAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());		
		List<Section> favouriteSections = favouriteSectionAndTagsDAO.getFavouriteSections();
		List<Tag> favouriteTags = favouriteSectionAndTagsDAO.getFavouriteTags();		
		return articleSetFactory.getFavouritesArticleSetFor(favouriteSections, favouriteTags);		
	}

	@Override
	protected Class<? extends Activity> getDefaultActivity() {
		return favourites.class;
	}

}
