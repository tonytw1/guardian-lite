package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.caching.InMemoryArticleCache;
import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;

public class CacheFactory {
	
	private static InMemoryArticleCache articleCache;
	private static InMemorySectionCache sectionCache;
	

		
	public static InMemoryArticleCache getArticleCache() {
		if (articleCache == null) {
			articleCache = new InMemoryArticleCache();
		}
		return articleCache;		
	}
	
	public static InMemorySectionCache getSectionCache() {
		if (sectionCache == null) {
			sectionCache = new InMemorySectionCache();
		}
		return sectionCache;		
	}
	
}
