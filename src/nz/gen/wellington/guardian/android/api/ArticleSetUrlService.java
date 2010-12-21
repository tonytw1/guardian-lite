package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.about.AboutArticlesDAO;
import nz.gen.wellington.guardian.android.about.SavedArticlesDAO;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiUrlService;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;

public class ArticleSetUrlService {
	
	private PreferencesDAO preferencesDAO;
	private AboutArticlesDAO aboutArticlesDAO;
	private SavedArticlesDAO savedArticlesDAO;
	
	public ArticleSetUrlService(Context context) {
		preferencesDAO = SingletonFactory.getPreferencesDAO(context);
		aboutArticlesDAO = new AboutArticlesDAO(context);
		savedArticlesDAO = new SavedArticlesDAO(context);
	}
	
	public String getUrlForArticleSet(ArticleSet articleSet) {
		ContentApiUrlService contentApiUrlService = new ContentApiUrlService(preferencesDAO.getPreferedApiHost(), preferencesDAO.getApiKey());
		if (articleSet instanceof AboutArticleSet) {
			return aboutArticlesDAO.getArticleSetUrl();
		}
		if (articleSet instanceof SavedArticlesArticleSet) {
			return savedArticlesDAO.getArticleSetUrl(((SavedArticlesArticleSet) articleSet));
		}
		return contentApiUrlService.getContentApiUrlForArticleSet(articleSet);
	}
	
}
