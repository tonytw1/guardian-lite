package nz.gen.wellington.guardian.android.usersettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;

public class FavouriteSectionsAndTagsDAO {
	
	private SqlLiteFavouritesDAO sqlLiteDAO;
	private ArticleSetFactory articleSetFactory;
	private Context context;
	private SectionDAO sectionDAO;
	
	public FavouriteSectionsAndTagsDAO(Context context) {
		this.sqlLiteDAO = new SqlLiteFavouritesDAO(context);
		this.sectionDAO = SingletonFactory.getSectionDAO(context);
		this.context = context;
	}
	
	public List<ArticleSet> getFavouriteArticleSets() {
		// TODO hack to get around circular reference.
		this.articleSetFactory = SingletonFactory.getArticleSetFactory(context);
	
		List<ArticleSet> favouriteArticleSets = new ArrayList<ArticleSet>();
		
		List<Map<String, String>> favouriteRows = sqlLiteDAO.getFavouriteRows();
		for (Map<String, String> row : favouriteRows) {
			ArticleSet articleSet = rowToArticleSet(row);
			if (articleSet != null) {
				favouriteArticleSets.add(articleSet);
			}
		}
		return favouriteArticleSets;
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

	public boolean isFavouriteSearchTerm(String searchTerm) {
		return sqlLiteDAO.isFavouriteSearchTerm(searchTerm);
	}

	public boolean addSearchTerm(String searchTerm) {
		return sqlLiteDAO.addSearchTerm(searchTerm);
	}

	public void removeSearchTerm(String searchTerm) {
		sqlLiteDAO.removeSearchTerm(searchTerm);
	}
	
	private ArticleSet rowToArticleSet(Map<String, String> row) {
		if(row.get(SqlLiteFavouritesDAO.TYPE).equals("tag")) {				
			Section section = sectionDAO.getSectionById(row.get(SqlLiteFavouritesDAO.SECTIONID));
			Tag tag = new Tag(row.get(SqlLiteFavouritesDAO.NAME), row.get(SqlLiteFavouritesDAO.APIID), section);
			return articleSetFactory.getArticleSetForTag(tag);
			
		} else if (row.get(SqlLiteFavouritesDAO.TYPE).equals("section")) {				
			Section section = sectionDAO.getSectionById(row.get(SqlLiteFavouritesDAO.SECTIONID));
			if (section != null) {
				return articleSetFactory.getArticleSetForSection(section);
			}
			
		} else if (row.get(SqlLiteFavouritesDAO.TYPE).equals("searchterm")) {
			return articleSetFactory.getArticleSetForSearchTerm(row.get(SqlLiteFavouritesDAO.SEARCHTERM));
		}
		return null;
	}
	
}
