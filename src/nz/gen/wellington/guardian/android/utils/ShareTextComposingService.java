package nz.gen.wellington.guardian.android.utils;

import nz.gen.wellington.guardian.android.model.Article;

public class ShareTextComposingService {
	
	private static final String SPACE = " ";
	private static final int MAX_LENGTH = 140;

	public static String composeShareText(Article article) {
		String url = article.getShortUrl();
		if (url == null) {
			url = article.getWebUrl();
		}
		if (article.getTitle() != null && url != null) {
			if (article.getTitle().length() + SPACE.length() + url.length() <= MAX_LENGTH) {
				return article.getTitle() + SPACE + url;
			}
		}
		return null;
	}
	
}
