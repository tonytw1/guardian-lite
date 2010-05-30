package nz.gen.wellington.guardian.android.api.caching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;

public class InMemoryArticleCache {
	
	Map<String, List<Article>> articleSetArticles;
	
	public InMemoryArticleCache() {
		this.articleSetArticles = new HashMap<String,  List<Article>>();
	}
		
	public List<Article> getArticleSetArticles(ArticleSet articleSet) {
		return articleSetArticles.get(articleSet.getApiUrl());
	}
	
	public void putArticleSetArticles(ArticleSet articleSet, List<Article> articles) {
		articleSetArticles.put(articleSet.getApiUrl(), articles);		
	}
	
	public void evictArticleSet(ArticleSet articleSet) {
		articleSetArticles.remove(articleSet.getApiUrl());		
	}
	
	public void clear() {
		articleSetArticles.clear();
	}

}
