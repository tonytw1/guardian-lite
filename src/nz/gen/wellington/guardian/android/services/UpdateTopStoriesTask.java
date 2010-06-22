package nz.gen.wellington.guardian.android.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.joda.time.DateTime;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import android.content.Context;
import android.util.Log;

public class UpdateTopStoriesTask extends ArticleUpdateTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTopStoriesTask";
	
	
	public UpdateTopStoriesTask(Context context) {
		this.context = context;
		this.articleDAO = ArticleDAOFactory.getDao(context);
	}

	
	@Override
	public String getTaskName() {
		return "Fetching top stories";
	}
	
	
	@Override
	public void run() {
		Log.i(TAG, "Fetching latest articles");
		TopStoriesArticleSet articleSet = new TopStoriesArticleSet();
				
		ContentSource api = ArticleDAOFactory.getOpenPlatformApi(context);
		List<Section> sections = ArticleDAOFactory.getDao(context).getSections();
		
		List<Article> articles = api.getArticles(articleSet, sections, null);
		if (articles != null) {
			FileBasedArticleCache fileBasedArticleCache = new FileBasedArticleCache(context);
			fileBasedArticleCache.putArticleSetArticles(articleSet, sortByLatestSection(articles));
			processArticles(articles);
		}
	}
	
	
	private List<Article> sortByLatestSection(List<Article> articles) {
		SortedMap<DateTime, Article> sorted = new TreeMap<DateTime, Article>();
		for (Article article : articles) {
			sorted.put(article.getPubDate(), article);
		}
		
		LinkedList<Article> results = new LinkedList<Article>();
		
		while (!sorted.isEmpty()) {
			Article latest = sorted.get(sorted.lastKey());
			Log.d(TAG, "Latest article has section: " + latest.getSection().getName());			
			addArticlesForSection(sorted, results, latest.getSection());			
		}
		
		return results;
	}

	
	private void addArticlesForSection(SortedMap<DateTime, Article> topStories, LinkedList<Article> results, Section section) {		
		List<Article> sectionArticles = new ArrayList<Article>();
		for (Article article : new LinkedList<Article>(topStories.values())) {
			if (article.getSection().getId().equals(section.getId())) {
				Log.i(TAG, "Adding article: " + article.getPubDateString() + ", " + article.getPubDateString());
				sectionArticles.add(article);
				topStories.remove(article.getPubDate());
			}
		}
		Collections.reverse(sectionArticles);
		Log.i(TAG, "Added: " + sectionArticles);
		results.addAll(sectionArticles);
	}
		
}
