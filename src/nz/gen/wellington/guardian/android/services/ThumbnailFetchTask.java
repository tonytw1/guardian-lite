package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;

public class ThumbnailFetchTask implements Runnable {

	private String url;

	public ThumbnailFetchTask(String thumbnailUrl) {
		this.url = thumbnailUrl;
	}

	@Override
	public void run() {
		ArticleDAOFactory.getImageDao().fetchLiveImage(url);
	}

}
