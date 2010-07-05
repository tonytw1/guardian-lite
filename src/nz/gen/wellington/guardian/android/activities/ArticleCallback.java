package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.model.Article;

public interface ArticleCallback {
	
	public void articleReady(Article ready);
	public void descriptionReady(String description);

}
