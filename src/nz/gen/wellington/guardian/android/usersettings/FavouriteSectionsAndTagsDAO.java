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
		addFavouriteTag(favouriteTags, "");
		return favouriteTags;
	}
	
	private void addFavouriteTag(List<Tag> favouriteTags, String tagId) {
		Tag tag = new Tag("Environment", "environment/oil-spills", new Section("Environment", "BP oil spill", "#FFFF00"));
		Tag tag3 = new Tag("lifeandstyle", "lifeandstyle/cycling", new Section("lifeandstyle", "Cycling3", "#FFFF00"));
		Tag tag2 = new Tag("Politics", "politics/vincentcable", new Section("politics", "Politics", "#FFFF00"));

		Tag tag4 = new Tag("Sport", "football/vuvuzelas", new Section("sport", "Sport", "#FFFF00"));

		
		
		if (tag != null) {
			favouriteTags.add(tag);
			favouriteTags.add(tag2);
			favouriteTags.add(tag3);
			favouriteTags.add(tag4);

		}
	}

	private void addFavouriteSection(List<Section> favouriteSections, String sectionId) {
		Section section = articleDAO.getSectionById(sectionId);
		if (section != null) {
			favouriteSections.add(section);
		}
	}

}
