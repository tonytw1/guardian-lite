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
	private Context context;

	
	public FavouriteSectionsAndTagsDAO(ArticleDAO articleDAO, Context context) {
		this.articleDAO = articleDAO;
		this.context = context;
	}
	
	public List<Section> getFavouriteSections() {
		DataHelper dh = new DataHelper(context);
		List<Section> favouriteSections = new LinkedList<Section>();
		dh.close();
		return favouriteSections;
	}
	
	

	public List<Tag> getFavouriteTags() {
		DataHelper dh = new DataHelper(context);
		List<Tag> tags = dh.selectAll(articleDAO.getSectionsMap());
		dh.close();
		return tags;
	}
	
}
