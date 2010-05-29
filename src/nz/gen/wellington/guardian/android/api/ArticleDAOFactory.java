package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.services.TaskQueue;
import android.content.Context;

public class ArticleDAOFactory {

	private static ArticleDAO dao;
	private static TaskQueue taskQueue;
	private static ImageDAO imageDAO;
		
	public static ArticleDAO getDao(Context context) {
		if (dao == null) {
			dao = new ArticleDAO(context);
		}
		return dao;		
	}
	
	public static TaskQueue getTaskQueue() {
		if (taskQueue == null) {
			taskQueue = new TaskQueue();
		}
		return taskQueue;		
	}

	public static ImageDAO getImageDao() {
		if (imageDAO == null) {
			imageDAO = new ImageDAO();
		}
		return imageDAO;	
	}
	
}
