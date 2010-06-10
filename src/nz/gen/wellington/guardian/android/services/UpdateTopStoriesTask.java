package nz.gen.wellington.guardian.android.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.model.Section;

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
		Log.i(TAG, "Updating top stories");
		SortedMap<DateTime, Article> topStories = new TreeMap<DateTime, Article>();
		List<Section> sections = articleDAO.getSections();
		if (sections != null) {
			for (Section section : sections) {
				List<Article> sectionItems = articleDAO.getSectionItems(section);
				if (sectionItems != null) {
					Log.i(TAG, "Adding story: " + sectionItems.get(0));
					topStories.put(sectionItems.get(0).getPubDate(), sectionItems.get(0));
				}
			}
		}
		
		Log.i(TAG, "Saving " + topStories.size() + " top stories");
		ArrayList<Article> results = new ArrayList<Article>(topStories.values());
		Collections.reverse(results);
		articleDAO.saveTopStories(results);
		Log.i(TAG, "Done");		
	}

	@Override
	public void setReport(ContentUpdateReport report) {			
	}
		
}
