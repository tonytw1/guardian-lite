package nz.gen.wellington.guardian.android.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.main;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class TopStoriesWidget extends AppWidgetProvider {
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		refresh(context, appWidgetIds);
	}
		
	protected Intent getClickIntent(Context context) {
		return new Intent(context, main.class);
	}
	
	protected ArticleSet getArticleSet(Context context) {
		return new TopStoriesArticleSet();
	}
	
	private void refresh(Context context, int[] appWidgetIds) {
		ArticleBundle stories = getArticles(context);
		
		ImageDAO imageDAO = ArticleDAOFactory.getImageDao(context);
		RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);
		if (stories != null && stories.getArticles() != null && stories.getArticles().size() > 2) {
			
			List<Article> randomArticles = selectTwoRandomArticleWithTrailImages(stories.getArticles());
			if (randomArticles.size() > 0) {
				populateArticle(widgetView, imageDAO, randomArticles.get(0), context);				
			}
			if (randomArticles.size() > 1) {
				populateSecondArticle(widgetView, imageDAO, randomArticles.get(1), context);								
			}
			
		} else {			
			Article errorMessage = new Article();
			errorMessage.setTitle("No articles available");
			errorMessage.setStandfirst("You may need to sync this article set");
			populateArticle(widgetView, imageDAO, errorMessage, context);
		}
		
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(appWidgetIds, widgetView);
	}
	
	
	private ArticleBundle getArticles(Context context) {
		ArticleSet articleSet = getArticleSet(context);
		if (articleSet == null) {
			return null;
		}
		ArticleDAO articleDAO = ArticleDAOFactory.getDao(context);
		return articleDAO.getArticleSetArticles(articleSet, ContentFetchType.LOCAL_ONLY);
	}
	
	
	private List<Article> selectTwoRandomArticleWithTrailImages(List<Article> articles) {
		List<Article> randomArticles = new ArrayList<Article>();
		int attempts = 0;
		
		while (randomArticles.size() < 2 && attempts < 50) {
			int articleIndex = new Random().nextInt(articles.size()-1);
			Article article = articles.get(articleIndex);			
			if (article.getThumbnailUrl() != null && !randomArticles.contains(article)) {
				randomArticles.add(article);
			}
			attempts = attempts + 1 ;
		}
		
		Log.i("TAG", randomArticles+"'");
		return randomArticles;
	}
	
	private void populateArticle(RemoteViews widgetView, ImageDAO imageDAO, Article article, Context context) {		
		widgetView.setTextViewText(R.id.WidgetHeadline, article.getTitle());
		widgetView.setTextViewText(R.id.WidgetStandfirst, article.getStandfirst());
		
		if (article.getThumbnailUrl() != null && imageDAO.isAvailableLocally(article.getThumbnailUrl())) {				
			Bitmap trailImage = imageDAO.getImage(article.getThumbnailUrl());
			widgetView.setViewVisibility(R.id.WidgetImage, View.VISIBLE);
			widgetView.setImageViewBitmap(R.id.WidgetImage, trailImage);
		} else {
			widgetView.setViewVisibility(R.id.WidgetImage, View.GONE);
		}
		
		Intent intent = getClickIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		widgetView.setOnClickPendingIntent(R.id.WidgetFirstItem, pendingIntent);
		
		widgetView.setViewVisibility(R.id.WidgetFirstItem, View.VISIBLE);
	}
		
	// This is abit messy - 1.5 api does not allow nested views, hence this duplication
	private void populateSecondArticle(RemoteViews widgetView, ImageDAO imageDAO, Article article, Context context) {
		widgetView.setTextViewText(R.id.WidgetSecondHeadline, article.getTitle());
		widgetView.setTextViewText(R.id.WidgetSecondStandfirst, article.getStandfirst());
		
		if (article.getThumbnailUrl() != null && imageDAO.isAvailableLocally(article.getThumbnailUrl())) {				
			Bitmap trailImage = imageDAO.getImage(article.getThumbnailUrl());
			widgetView.setViewVisibility(R.id.WidgetSecondImage, View.VISIBLE);
			widgetView.setImageViewBitmap(R.id.WidgetSecondImage, trailImage);
		} else {
			widgetView.setViewVisibility(R.id.WidgetSecondImage, View.GONE);
		}
		
		Intent intent = getClickIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		widgetView.setOnClickPendingIntent(R.id.WidgetSecondItem, pendingIntent);
		
		widgetView.setViewVisibility(R.id.WidgetSecondItem, View.VISIBLE);
	}

}
