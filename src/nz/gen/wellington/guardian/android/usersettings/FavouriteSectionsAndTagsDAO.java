package nz.gen.wellington.guardian.android.usersettings;

import java.util.List;

import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;
import android.util.Log;

public class FavouriteSectionsAndTagsDAO {
	
	private static final String TAG = "FavouriteSectionsAndTagsDAO";
	
	private SectionDAO sectionDAO;	// Really? this dependenancy should be on the things we're passing it to!
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
	
	public List<String> getSavedArticleIds() {
		return sqlLiteDAO.getSavedArticleIds();
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
	
	public boolean addSavedArticle(Article article) {
		return sqlLiteDAO.addSavedArticle(article);	
	}

	public boolean isSavedArticle(Article article) {
		return sqlLiteDAO.isSavedArticle(article);
	}

	public boolean removeSavedArticle(Article article) {
		return sqlLiteDAO.removeSavedArticle(article);
	}

	public void removeAllSavedArticles() {
		sqlLiteDAO.removeAllSavedArticles();		
	}
		
}
