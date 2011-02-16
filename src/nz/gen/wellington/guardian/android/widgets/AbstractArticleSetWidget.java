/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.widgets.mainwidget;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ContentFetchType;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import nz.gen.wellington.guardian.model.Article;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

public abstract class AbstractArticleSetWidget extends AppWidgetProvider {
			
	private ArticleViews firstArticleViews = new ArticleViews(
			R.id.WidgetFirstItem, R.id.WidgetHeadline, R.id.WidgetStandfirst,
			R.id.WidgetImage);
	
	private ArticleViews secondArticleViews = new ArticleViews(
			R.id.WidgetSecondItem, R.id.WidgetSecondHeadline,
			R.id.WidgetSecondStandfirst, R.id.WidgetSecondImage);

	protected ArticleSetFactory articleSetFactory;
	private ImageDAO imageDAO;
	private SettingsDAO settingsDAO;
	private ArticleDAO articleDAO;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	
		// TODO is there a better place todo this - ie. a constructor method?
		articleSetFactory = SingletonFactory.getArticleSetFactory(context);
		imageDAO = SingletonFactory.getImageDao(context);
		settingsDAO = SingletonFactory.getSettingsDAO(context);
		articleDAO = SingletonFactory.getArticleDao(context);
		
		refresh(context, appWidgetIds);
	}
	
	abstract protected ArticleSet getArticleSet(int pageSize, Context context);

	abstract protected String getNoArticlesExplainationText();
	
	private void refresh(Context context, int[] appWidgetIds) {
		ArticleBundle stories = getArticles(context);
		
		RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget);
		if (stories != null && stories.getArticles() != null) {
			
			List<Article> randomArticles = selectTwoRandomArticleWithTrailImages(stories.getArticles());
			if (randomArticles.size() > 0) {
				populateArticle(widgetView, imageDAO, randomArticles.get(0), context, firstArticleViews);
				if (randomArticles.size() > 1) {
					populateArticle(widgetView, imageDAO, randomArticles.get(1), context, secondArticleViews);
				} else {
					hideSecondArticle(widgetView);
				}
				
			} else {
				showNoArticlesMessage(context, imageDAO, widgetView);				
			}
						
		} else {			
			showNoArticlesMessage(context, imageDAO, widgetView);
		}
		
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		if (manager != null) {
			manager.updateAppWidget(appWidgetIds, widgetView);
		}
	}

	
	private void showNoArticlesMessage(Context context, ImageDAO imageDAO, RemoteViews widgetView) {
		Article errorMessage = new Article();
		errorMessage.setHeadline("No articles available");
		errorMessage.setStandfirst(getNoArticlesExplainationText());
		populateArticle(widgetView, imageDAO, errorMessage, context, firstArticleViews);
		
		hideSecondArticle(widgetView);
	}

	private void hideSecondArticle(RemoteViews widgetView) {
		widgetView.setViewVisibility(R.id.WidgetSecondItem, View.GONE);
	}

	
	private ArticleBundle getArticles(Context context) {
		ArticleSet articleSet = getArticleSet(settingsDAO.getPageSizePreference(), context);
		if (articleSet.isEmpty()) {
			return null;
		}
		return articleDAO.getArticleSetArticles(articleSet, ContentFetchType.LOCAL_ONLY);
	}
	
	
	private List<Article> selectTwoRandomArticleWithTrailImages(List<Article> articles) {
		List<Article> randomArticles = new ArrayList<Article>();
		
		List<Article> articleWithTrailImages = selectArticlesWithTrailImages(articles);
		while (randomArticles.size() < 2 && !articleWithTrailImages.isEmpty()) {
			int articleIndex = new Random(new Date().getTime()).nextInt(articleWithTrailImages.size()-1);
			Article article = articleWithTrailImages.get(articleIndex);			
			randomArticles.add(article);
			articleWithTrailImages.remove(article);			
		}		
		return randomArticles;
	}

	private List<Article> selectArticlesWithTrailImages(List<Article> articles) {
		List<Article> articleWithTrailImages = new ArrayList<Article>();
		for (Article article : articles) {
			if (article.getThumbnailUrl() != null) {
				articleWithTrailImages.add(article);
			}
		}
		return articleWithTrailImages;
	}
	
	private void populateArticle(RemoteViews widgetView, ImageDAO imageDAO, Article article, Context context, ArticleViews articleViews) {
		widgetView.setTextViewText(articleViews.headline, article.getHeadline());
		widgetView.setTextViewText(articleViews.standfirst, article.getStandfirst());
		
		if (article.getThumbnailUrl() != null && imageDAO.isAvailableLocally(article.getThumbnailUrl())) {				
			Bitmap trailImage = imageDAO.getImage(article.getThumbnailUrl());
			widgetView.setViewVisibility(articleViews.image, View.VISIBLE);
			widgetView.setImageViewBitmap(articleViews.image, trailImage);
		} else {
			widgetView.setViewVisibility(articleViews.image, View.GONE);
		}
		
		PendingIntent pendingIntent = createShowArticleIntent(context, article);
		widgetView.setOnClickPendingIntent(articleViews.view, pendingIntent);		
		widgetView.setViewVisibility(articleViews.view, View.VISIBLE);
	}

	protected PendingIntent createShowArticleIntent(Context context, Article article) {
		Intent intent = new Intent(context, mainwidget.class);		
		intent.setData(Uri.withAppendedPath(Uri.parse("content://article/id"), String.valueOf(article.getId())));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		return pendingIntent;
	}
	
	private  class ArticleViews {
		int view;
		int headline;
		int standfirst;
		int image;

		public ArticleViews(int view, int headline, int standfirst, int image) {
			this.view = view;
			this.headline = headline;
			this.standfirst = standfirst;
			this.image = image;
		}		
	}
	
}
