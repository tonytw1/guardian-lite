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
		List<Section> sections = articleDAO.getSections();
		addFavouriteSection(favouriteSections, "business", sections);
		addFavouriteSection(favouriteSections, "commentisfree", sections);
		addFavouriteSection(favouriteSections, "environment", sections);
		addFavouriteSection(favouriteSections, "uk, sections", sections);
		addFavouriteSection(favouriteSections, "world", sections);
		return favouriteSections;
	}

	public List<Tag> getFavouriteTags() {
		List<Tag> favouriteTags = new LinkedList<Tag>();
		addFavouriteTag(favouriteTags, "");
		return favouriteTags;
	}
	
	private void addFavouriteTag(List<Tag> favouriteTags, String tagId) {
		Tag tag = new Tag("BP oil spill", "environment/bp-oil-spill", new Section("environment", "Environment", "#FF0000"));
		Tag tag2 = new Tag("Vince Cable", "politics/vincentcable", new Section("politics", "Politics", "#FF0000"));
		Tag tag3 = new Tag("Cycling", "lifeandstyle/cycling", new Section("lifeandstyle", "Life and style", "#FF0000"));
		Tag tag4 = new Tag("Vuvuzelas", "football/vuvuzelas", new Section("sport", "Sport", "#FF0000"));
		Tag tag5 = new Tag("Helen Pidd", "profile/helenpidd", new Section("global", "Global", "#FF0000"));
		
		if (tag != null) {
			favouriteTags.add(tag);
			favouriteTags.add(tag2);
			favouriteTags.add(tag3);
			favouriteTags.add(tag4);
			favouriteTags.add(tag5);


		}
	}

	private void addFavouriteSection(List<Section> favouriteSections, String sectionId, List<Section> sections) {
		Section section = getSectionById(sectionId, sections);
		if (section != null) {
			favouriteSections.add(section);
		}
	}

	
	public Section getSectionById(String id, List<Section> sections) {
		if (sections != null) {
			for (Section section : sections) {	// TODO suggests that sections should be a map?
				if (section.getId().equals(id)) {
					return section;
				}
			}
		}
		return null;
	}
}
