package nz.gen.wellington.guardian.android.services;

import android.content.Context;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ImageDAO;

public class ImageFetchTask implements Runnable {

	private String url;
	private Context context;

	public ImageFetchTask(String thumbnailUrl, Context context) {
		this.url = thumbnailUrl;
		this.context = context;
	}

	@Override
	public void run() {
		ImageDAO imageDao = ArticleDAOFactory.getImageDao(context);
		if (!imageDao.isAvailableLocally(url)) {
			imageDao.fetchLiveImage(url);
		}
	}

}
