package nz.gen.wellington.guardian.android.usersettings;

import java.util.LinkedList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;

public class FavouriteSectionsAndTagsDAO {
	
	ArticleDAO articleDAO;
	
	public FavouriteSectionsAndTagsDAO(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}
	
	public List<Section> getFavouriteSections() {		
		List<Section> favouriteSections = new LinkedList<Section>();
		addFavouriteSection(favouriteSections, "business");
		addFavouriteSection(favouriteSections, "commentisfree");
		addFavouriteSection(favouriteSections, "environment");
		addFavouriteSection(favouriteSections, "uk");
		addFavouriteSection(favouriteSections, "world");
		return favouriteSections;
	}

	public List<Tag> getFavouriteTags() {
		List<Tag> favouriteTags = new LinkedList<Tag>();
		addFavouriteTag(favouriteTags, "science/evolution");
		return favouriteTags;
	}
	
	private void addFavouriteTag(List<Tag> favouriteTags, String string) {
		// TODO Auto-generated method stub
		
	}

	private void addFavouriteSection(List<Section> favouriteSections, String sectionId) {
		Section section = articleDAO.getSectionById(sectionId);
		if (section != null) {
			favouriteSections.add(section);
		}
	}


}
