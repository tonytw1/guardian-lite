package nz.gen.wellington.guardian.android.usersettings;

import java.util.List;

import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;

public class FavouriteSectionsAndTagsDAO {
	
	private SectionDAO sectionDAO;
	private SqlLiteFavouritesDAO sqlLiteDAO;
	
	public FavouriteSectionsAndTagsDAO(SectionDAO sectionDAO, Context context) {
		this.sectionDAO = sectionDAO;
		this.sqlLiteDAO = new SqlLiteFavouritesDAO(context);
	}
		
	public List<Section> getFavouriteSections() {
		List<Section> sections = sqlLiteDAO.getFavouriteSections(sectionDAO.getSectionsMap());	// todo dh should get sections itself
		return sections;
	}
		
	public List<Tag> getFavouriteTags() {
		List<Tag> tags = sqlLiteDAO.getFavouriteTags(sectionDAO.getSectionsMap());
		return tags;
	}
	
	@Deprecated // TODO remove for potential performance reasons
	public boolean hasFavourites() {
		boolean hasFavourites = sqlLiteDAO.hasFavourites();
		return hasFavourites;
	}


	public boolean isFavourite(Tag tag) {
		return sqlLiteDAO.isFavourite(tag);
	}

	public boolean addTag(Tag tag) {
		return sqlLiteDAO.addTag(tag);
	}

	public void removeTag(Tag tag) {
		sqlLiteDAO.removeTag(tag);
	}
	
	public boolean isFavourite(Section section) {
		return sqlLiteDAO.isFavourite(section);
	}

	public boolean addSection(Section section) {
		return sqlLiteDAO.addSection(section);		
	}

	public void removeSection(Section section) {
		sqlLiteDAO.removeSection(section);		
	}
	
}
