package nz.gen.wellington.guardian.android.usersettings;

import java.util.List;

import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.sqllite.DataHelper;
import android.content.Context;

public class FavouriteSectionsAndTagsDAO {
	
	private SectionDAO sectionDAO;
	private DataHelper dh;
	
	public FavouriteSectionsAndTagsDAO(SectionDAO sectionDAO, Context context) {
		this.sectionDAO = sectionDAO;
		this.dh = new DataHelper(context);
	}
		
	public List<Section> getFavouriteSections() {
		List<Section> sections = dh.getFavouriteSections(sectionDAO.getSectionsMap());	// todo dh should get sections itself
		return sections;
	}
		
	public List<Tag> getFavouriteTags() {
		List<Tag> tags = dh.getFavouriteTags(sectionDAO.getSectionsMap());
		return tags;
	}
	
	public boolean hasFavourites() {
		boolean hasFavourites = dh.hasFavourites();
		return hasFavourites;
	}


	public boolean isFavourite(Tag tag) {
		return dh.isFavourite(tag);
	}

	public boolean addTag(Tag tag) {
		return dh.addTag(tag);
	}

	public void removeTag(Tag tag) {
		dh.removeTag(tag);
	}
	
	public boolean isFavourite(Section section) {
		return dh.isFavourite(section);
	}

	public boolean addSection(Section section) {
		return dh.addSection(section);		
	}

	public void removeSection(Section section) {
		dh.removeSection(section);		
	}
	
}
