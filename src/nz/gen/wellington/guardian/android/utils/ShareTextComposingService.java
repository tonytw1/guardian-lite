package nz.gen.wellington.guardian.android.utils;

import nz.gen.wellington.guardian.android.model.Article;

public class ShareTextComposingService {
	
	private static final String SPACE = " ";
	private static final int MAX_LENGTH = 140;

	public static String composeShareText(Article article) {
		if (article.getTitle() != null && article.getShortUrl() != null) {
			if (article.getTitle().length() + SPACE.length() + article.getShortUrl().length() <= MAX_LENGTH) {
				return article.getTitle() + SPACE + article.getShortUrl();
			}
		}
		return null;
	}
	
}
