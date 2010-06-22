package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import android.content.Context;
import android.util.Log;

public class UpdateSectionArticlesTask extends ArticleUpdateTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateSectionArticlesTask";
	private Section section;
		
	public UpdateSectionArticlesTask(Section section, Context context) {
		this.section = section;
		this.context = context;
		this.articleDAO = ArticleDAOFactory.getDao(context);
	}

	@Override
	public void run() {
		articleDAO.evictArticleSet(new SectionArticleSet(section));
		Log.i(TAG, "Fetching section articles: " + section.getName());
		List<Article> sectionItems = articleDAO.getSectionItems(section);
		processArticles(sectionItems);
		report.setSectionCount(report.getSectionCount()+1);
	}
	
	@Override
	public String getTaskName() {
		return "Fetching articles for section: " + section.getName();
	}
		
}
