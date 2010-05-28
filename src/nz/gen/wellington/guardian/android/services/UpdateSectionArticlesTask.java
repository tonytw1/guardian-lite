package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import android.util.Log;

public class UpdateSectionArticlesTask implements Runnable {

	private static final String TAG = "UpdateSectionArticlesTask";
	private ArticleDAO articleDAO;
	private Section section;

	public UpdateSectionArticlesTask(ArticleDAO articleDAO, Section section) {
		this.articleDAO = articleDAO;
		this.section = section;
	}

	@Override
	public void run() {
		articleDAO.evictArticleSet(new SectionArticleSet(section));
		Log.i(TAG, "Fetching section articles: " + section.getName());
		articleDAO.getSectionItems(section);
	}
	
}
