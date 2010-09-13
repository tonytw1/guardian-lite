package nz.gen.wellington.guardian.android.contentupdate.tasks;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import android.content.Context;

public class PurgeExpiredContentTask implements ContentUpdateTaskRunnable {

	private Context context;

	public PurgeExpiredContentTask(Context context) {
		this.context = context;
	}

	
	@Override
	public void run() {
		ArticleDAO articleDAO = ArticleDAOFactory.getDao(context);
		articleDAO.clearExpiredCacheFiles(context);
	}
	
	@Override
	public void stop() {
		// TODO
	}
	
	@Override
	public String getTaskName() {
		return "Clearing expired article sets";
	}


	@Override
	public void setReport(ContentUpdateReport report) {
	}
	
}
