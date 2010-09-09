package nz.gen.wellington.guardian.android.usersettings;

import java.util.List;

import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.sqllite.DataHelper;
import android.content.Context;

public class FavouriteSectionsAndTagsDAO {
	
	SectionDAO sectionDAO;
	private Context context;

	
	public FavouriteSectionsAndTagsDAO(SectionDAO sectionDAO, Context context) {
		this.sectionDAO = sectionDAO;
		this.context = context;
	}
	
	
	public List<Section> getFavouriteSections() {
		DataHelper dh = new DataHelper(context);	// TODO setup in constructor
		List<Section> sections = dh.getFavouriteSections(sectionDAO.getSectionsMap());	// TODO dh should get sections itself
		dh.close();
		return sections;
	}
	
	
	public List<Tag> getFavouriteTags() {
		DataHelper dh = new DataHelper(context);
		List<Tag> tags = dh.getFavouriteTags(sectionDAO.getSectionsMap());
		dh.close();
		return tags;
	}
	
	
	public boolean hasFavourites() {
		DataHelper dh = new DataHelper(context);
		boolean hasFavourites = dh.hasFavourites();
		dh.close();
		return hasFavourites;
	}
	
}
