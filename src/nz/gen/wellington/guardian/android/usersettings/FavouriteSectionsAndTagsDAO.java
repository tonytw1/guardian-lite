package nz.gen.wellington.guardian.android.usersettings;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.sqllite.DataHelper;

public class FavouriteSectionsAndTagsDAO {
	
	ArticleDAO articleDAO;
	private DataHelper dh;

	
	public FavouriteSectionsAndTagsDAO(ArticleDAO articleDAO, Context context) {
		this.articleDAO = articleDAO;
		this.dh = new DataHelper(context);
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
		return this.dh.selectAll();
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
