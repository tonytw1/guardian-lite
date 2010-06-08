package nz.gen.wellington.guardian.android.services;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import nz.gen.wellington.guardian.android.model.Section;
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
		List<Article> topStories = new ArrayList<Article>();
		List<Section> sections = articleDAO.getSections();
		if (sections != null) {
			for (Section section : sections) {
				List<Article> sectionItems = articleDAO.getSectionItems(section);
				if (sectionItems != null) {
					Log.i(TAG, "Adding story: " + sectionItems.get(0));
					topStories.add(sectionItems.get(0));
				}
			}
		}
		
		// TODO sort by date and limit to 10 say
		if (topStories.size() > 10) {
			topStories = topStories.subList(0, 10);
		}
		
		Log.i(TAG, "Saving " + topStories.size() + " top stories");
		articleDAO.saveTopStories(new ArrayList<Article>(topStories));
		Log.i(TAG, "Done");
	}

	@Override
	public void setReport(ContentUpdateReport report) {			
	}
		
}
