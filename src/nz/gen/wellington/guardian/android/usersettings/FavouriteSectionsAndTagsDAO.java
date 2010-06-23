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
		return favouriteSections;
	}

	public List<Tag> getFavouriteTags() {
		return this.dh.selectAll(articleDAO.getSectionsMap());
	}
	
}
