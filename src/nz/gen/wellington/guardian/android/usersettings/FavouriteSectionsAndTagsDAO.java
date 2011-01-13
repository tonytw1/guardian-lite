package nz.gen.wellington.guardian.android.usersettings;

import java.util.List;

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
	
	public FavouriteSectionsAndTagsDAO(Context context) {
		this.sqlLiteDAO = new SqlLiteFavouritesDAO(context);
		this.articleSetFactory = SingletonFactory.getArticleSetFactory(context);
	}
	
	public List<ArticleSet> getFavouriteArticleSets() {
		// TODO - this implies three sqllite queries in a row - needs to be done in one open open and close if possible.
		List<Section> favouriteSections = getFavouriteSections();
		List<Tag> favouriteTags = getFavouriteTags();
		List<String> favouriteSearchTerms = getFavouriteSearchTerms();
		
		boolean favouritesLoadedCorrectly = (favouriteSections != null && favouriteTags != null && favouriteSearchTerms != null);
		if (!favouritesLoadedCorrectly) {
			return null;
		}
		
		List<ArticleSet> favouriteArticleSets = articleSetFactory.getArticleSetsForSections(favouriteSections);
		favouriteArticleSets.addAll(articleSetFactory.getArticleSetsForTags(favouriteTags));
		favouriteArticleSets.addAll(articleSetFactory.getArticleSetsForSearchTerms(favouriteSearchTerms));
		return favouriteArticleSets;
	}
		
	public List<Section> getFavouriteSections() {
		return sqlLiteDAO.getFavouriteSections();
	}
		
	public List<Tag> getFavouriteTags() {
		return sqlLiteDAO.getFavouriteTags();
	}
	
	public List<String> getFavouriteSearchTerms() {
		return sqlLiteDAO.getFavouriteSearchTerms();
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
		
}
