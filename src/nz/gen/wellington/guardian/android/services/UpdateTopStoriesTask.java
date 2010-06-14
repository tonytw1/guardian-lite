package nz.gen.wellington.guardian.android.services;

import java.security.acl.LastOwnerException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;

import org.joda.time.DateTime;

import android.content.Context;
import android.util.Log;

public class UpdateTopStoriesTask implements ContentUpdateTaskRunnable {

	private static final String TAG = "UpdateTopStoriesTask";
	private ArticleDAO articleDAO;

	public UpdateTopStoriesTask(ArticleDAO articleDAO, Context context) {
		this.articleDAO = articleDAO;
	}

	@Override
	public void run() {
		Log.i(TAG, "Updating top stories from favourite sections");
		SortedMap<DateTime, Article> topStories = new TreeMap<DateTime, Article>();
		
		List<Section> sections = new FavouriteSectionsAndTagsDAO(articleDAO).getFavouriteSections();
		if (sections != null) {
			for (Section section : sections) {
				List<Article> sectionItems = articleDAO.getSectionItems(section);
				putLatestThreeStoriesFromSectionOntoList(topStories, sectionItems);
			}
		}
		
		// List now contains 3 items from each section in date order.
		LinkedList<Article> results = new LinkedList<Article>();
		while (!topStories.isEmpty()) {
			Article latest = topStories.get(topStories.firstKey());
			Log.d(TAG, "Latest article has section: " + latest.getSection().getName());
			results.add(latest);
			
			addArticlesForSection(topStories, results, latest.getSection());
			
		}
				
		Log.i(TAG, "Saving " + topStories.size() + " top stories");
		Collections.reverse(results);		
		articleDAO.saveTopStories(results);
		Log.i(TAG, "Done");
	}

	
	
	private void addArticlesForSection(SortedMap<DateTime, Article> topStories,
			LinkedList<Article> results, Section section) {
		
		for (Article article : new LinkedList<Article>(topStories.values())) {
			if (article.getSection().getId().equals(section.getId())) {
				results.add(article);
				topStories.remove(article.getPubDate());
			}
		}

		// TODO Auto-generated method stub
		
	}

	private void putLatestThreeStoriesFromSectionOntoList(SortedMap<DateTime, Article> topStories, List<Article> sectionItems) {
		if (sectionItems == null) {
			return;
		}
		final int numberToAdd = (sectionItems.size() < 3) ? sectionItems.size() : 3; 
		for (int i = 0; i < numberToAdd; i++) {
			Article article = sectionItems.get(i);
			if (article.getSection() != null) {
				topStories.put(article.getPubDate(), article);
			}
		}
	}

	
	@Override
	public void setReport(ContentUpdateReport report) {			
	}
		
}
