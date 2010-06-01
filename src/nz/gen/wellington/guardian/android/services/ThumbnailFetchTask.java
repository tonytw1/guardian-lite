package nz.gen.wellington.guardian.android.services;

import android.content.Context;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;

public class ThumbnailFetchTask implements Runnable {

	private String url;
	private Context context;

	public ThumbnailFetchTask(String thumbnailUrl, Context context) {
		this.url = thumbnailUrl;
		this.context = context;
	}

	@Override
	public void run() {
		ArticleDAOFactory.getImageDao(context).fetchLiveImage(url);
	}

}
