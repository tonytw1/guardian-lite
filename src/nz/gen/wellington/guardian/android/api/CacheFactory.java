package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;

public class CacheFactory {
	
	private static InMemorySectionCache sectionCache;
		
	public static InMemorySectionCache getSectionCache() {
		if (sectionCache == null) {
			sectionCache = new InMemorySectionCache();
		}
		return sectionCache;		
	}
	
}
