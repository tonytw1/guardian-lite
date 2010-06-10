package nz.gen.wellington.guardian.android.activities.ui;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ImageDecoratedArticle;
import android.graphics.Bitmap;

public class ArticleImageDecorator {

	public static List<ImageDecoratedArticle> decorateNewsitemsWithThumbnails(List<Article> newsitems, ImageDAO imageDAO) {
		List<ImageDecoratedArticle> decorated = new ArrayList<ImageDecoratedArticle>();
		for (Article article : newsitems) {
			decorated.add(applyThumbnailIfAvailableLocally(imageDAO, article));
		}
		return decorated;
	}

	
	private static ImageDecoratedArticle applyThumbnailIfAvailableLocally(ImageDAO imageDAO, Article article) {
		Bitmap image = null;		
		if (article.getThumbnailUrl() != null && imageDAO.isAvailableLocally(article.getThumbnailUrl())) {
			image = imageDAO.getImage(article.getThumbnailUrl());
		} else if (article.getMainImageUrl() != null && imageDAO.isAvailableLocally(article.getMainImageUrl())) {
			image = imageDAO.getImage(article.getMainImageUrl());
		}
		return new ImageDecoratedArticle(article, image);		
	}
	
}
