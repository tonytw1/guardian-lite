package nz.gen.wellington.guardian.android.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;

import org.joda.time.DateTime;

import android.content.Context;
import android.util.Log;

public class UpdateTopStoriesTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTopStoriesTask";
	private ArticleDAO articleDAO;
	private FavouriteSectionsAndTagsDAO favouritesDAO;

	public UpdateTopStoriesTask(ArticleDAO articleDAO, Context context) {
		this.articleDAO = articleDAO;
		this.favouritesDAO = new FavouriteSectionsAndTagsDAO(articleDAO, context);
	}


	@Override
	public void run() {
		Log.i(TAG, "Updating top stories from favourite sections");
		SortedMap<DateTime, Article> topStories = new TreeMap<DateTime, Article>();
		
		LinkedList<Article> results = new LinkedList<Article>();
		List<Tag> favouriteTags = favouritesDAO.getFavouriteTags();
		if (favouriteTags != null) {
			for (Tag tag: favouriteTags) {
				List<Article> tagItems = articleDAO.getKeywordItems(tag);
				putLatestThreeStoriesOntoList(topStories, tagItems);
			}			
		}
		
		List<Section> sections = favouritesDAO.getFavouriteSections();
		if (sections != null) {
			for (Section section : sections) {
				List<Article> sectionItems = articleDAO.getSectionItems(section);
				putLatestThreeStoriesOntoList(topStories, sectionItems);
			}
		}
		
		
		while (!topStories.isEmpty()) {
			Article latest = topStories.get(topStories.lastKey());
			Log.d(TAG, "Latest article has section: " + latest.getSection().getName());			
			addArticlesForSection(topStories, results, latest.getSection());			
		}
		
		Log.i(TAG, "Saving " + topStories.size() + " top stories");
		articleDAO.saveTopStories(results);
		Log.i(TAG, "Done");
	}
	
	@Override
	public void stop() {
		// TODO
	}
	
	@Override
	public String getTaskName() {
		return "Updating top stories";
	}
	
	
	@Override
	public void setReport(ContentUpdateReport report) {			
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

	
	private void putLatestThreeStoriesOntoList(SortedMap<DateTime, Article> topStories, List<Article> articles) {
		if (articles == null) {
			return;
		}
		final int numberToAdd = (articles.size() < 3) ? articles.size() : 3; 
		for (int i = 0; i < numberToAdd; i++) {
			Article article = articles.get(i);
			if (article.getSection() != null) {
				topStories.put(article.getPubDate(), article);
			}
		}
	}
	
}
