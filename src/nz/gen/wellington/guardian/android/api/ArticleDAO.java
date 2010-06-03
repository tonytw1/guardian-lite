package nz.gen.wellington.guardian.android.api;

import java.util.List;

import nz.gen.wellington.guardian.android.api.caching.FileBasedArticleCache;
import nz.gen.wellington.guardian.android.api.caching.FileBasedSectionCache;
import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.AuthorArticleSet;
import nz.gen.wellington.guardian.android.model.KeywordArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;
import android.util.Log;

public class ArticleDAO {
		
	private static final String TAG = "ArticleDAO";
	
	ContentSource openPlatformApi;

	InMemorySectionCache sectionCache;
	FileBasedArticleCache fileBasedArticleCache;
	FileBasedSectionCache fileBasedSectionCache;
	
	public ArticleDAO(Context context) {
		this.openPlatformApi = ApiFactory.getOpenPlatformApi(context);

		this.sectionCache = CacheFactory.getSectionCache();
		this.fileBasedArticleCache = new FileBasedArticleCache(context);
		this.fileBasedSectionCache = new FileBasedSectionCache(context);
	}
	
	
	public List<Article> getSectionItems(Section section) {
		ArticleSet sectionArticleSet = new SectionArticleSet(section);		
		return getArticleSetArticles(sectionArticleSet);
	}
	
	
	public List<Article> getAuthorItems(Tag author) {
		ArticleSet authorArticleSet = new AuthorArticleSet(author);
		return getArticleSetArticles(authorArticleSet);
	}
	
	public List<Article> getKeywordItems(Tag keyword) {
		ArticleSet keywordArticleSet = new KeywordArticleSet(keyword);
		return getArticleSetArticles(keywordArticleSet);
	}
	
	
	public List<Section> getSections() {
		if (sectionCache != null && !sectionCache.isEmpty()) {
			return sectionCache.getAll();
		}
		
		List<Section> sections = fileBasedSectionCache.getSections();
		if (sections != null) {
			return sections;			
		}
		
		sections = openPlatformApi.getSections();
		if (sections != null) {
			Log.i(TAG, "Found " + sections.size() + " sections");
			sectionCache.addAll(sections);
			fileBasedSectionCache.putSections(sections);
		}		
		return sections;
	}
	
	
	private List<Article> getArticleSetArticles(ArticleSet articleSet) {
		Log.i(TAG, "Retrieving articles for article set: " + articleSet.getName());
		
		List<Article> articles = fileBasedArticleCache.getArticleSetArticles(articleSet);
		if (articles != null) {
			Log.i(TAG, "Got file cache hit for article set: " + articleSet.getName());
			return articles;
		}
				
		articles = openPlatformApi.getArticles(articleSet);		
		if (articles != null) {
			Log.i(TAG, "Got " + articles.size() + " articles from api call");
			//articleCache.putArticleSetArticles(articleSet, articles);
			fileBasedArticleCache.putArticleSetArticles(articleSet, articles);
		} else {
			Log.w(TAG, "Article api call failed");
		}
		return articles;
	}


	public void evictSections() {
		sectionCache.clear();
		fileBasedSectionCache.clear();
	}
	
	public void evictAll() {
		fileBasedArticleCache.clear();
	}
	
	
	public void evictArticleSet(ArticleSet articleSet) {
		fileBasedArticleCache.clear(articleSet);
	}

	
}
