package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiUrlService;
import nz.gen.wellington.guardian.android.content.AboutArticlesDAO;
import nz.gen.wellington.guardian.android.content.SavedArticlesDAO;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.AboutArticleSet;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.content.Context;

public class ArticleSetUrlService {
	
	private SettingsDAO settingsDAO;
	private AboutArticlesDAO aboutArticlesDAO;
	private SavedArticlesDAO savedArticlesDAO;
	
	public ArticleSetUrlService(Context context) {
		settingsDAO = SingletonFactory.getSettingsDAO(context);
		aboutArticlesDAO = new AboutArticlesDAO(context);
		savedArticlesDAO = new SavedArticlesDAO(context);
	}
	
	public String getUrlForArticleSet(ArticleSet articleSet) {
		ContentApiUrlService contentApiUrlService = new ContentApiUrlService(settingsDAO.getPreferedApiHost(), settingsDAO.getApiKey(), settingsDAO.getSupportedContentTypes());
		if (articleSet instanceof AboutArticleSet) {
			return aboutArticlesDAO.getArticleSetUrl();
		}
		if (articleSet instanceof SavedArticlesArticleSet) {
			return savedArticlesDAO.getArticleSetUrl(((SavedArticlesArticleSet) articleSet));
		}
		return contentApiUrlService.getContentApiUrlForArticleSet(articleSet);
	}
	
}
