package nz.gen.wellington.guardian.android.services;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.model.Section;
import android.content.Context;
import android.util.Log;

public class UpdateSectionsTask implements ContentUpdateTaskRunnable {
	
	private static final String TAG = "UpdateSectionsTask";
	private Context context;

	public UpdateSectionsTask(Context context) {
		this.context = context;
	}
	
	@Override
	// TODO refactor to be able to retrieve before commiting to the evict
	public void run() {
		ArticleDAO articleDAO = ArticleDAOFactory.getDao(context);
		articleDAO.evictSections();
		
		List<Section> sections = articleDAO.getSections();
		if (sections != null) {
			for (Section section : sections) {
				Log.i(TAG, "Injecting section into update queue: " + section.getName());
				ArticleDAOFactory.getTaskQueue().addArticleTask(new UpdateSectionArticlesTask(articleDAO, section, context));
			}
		}
	}
	
	@Override
	public void setReport(ContentUpdateReport report) {		
	}

}
