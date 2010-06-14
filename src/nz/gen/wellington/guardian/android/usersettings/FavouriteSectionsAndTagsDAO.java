package nz.gen.wellington.guardian.android.usersettings;

import java.util.LinkedList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.model.Section;

public class FavouriteSectionsAndTagsDAO {
	
	ArticleDAO articleDAO;
	
	public FavouriteSectionsAndTagsDAO(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}

	public List<Section> getFavouriteSections() {		
		List<Section> favouriteSections = new LinkedList<Section>();
		favouriteSections.add(articleDAO.getSectionById("business"));	// TODO null safe this lot.
		favouriteSections.add(articleDAO.getSectionById("commentisfree"));
		favouriteSections.add(articleDAO.getSectionById("environment"));
		favouriteSections.add(articleDAO.getSectionById("uk"));
		favouriteSections.add(articleDAO.getSectionById("world"));
		return favouriteSections;
	}

}
