package nz.gen.wellington.guardian.android.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import nz.gen.wellington.guardian.android.api.caching.FileBasedSectionCache;
import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;
import nz.gen.wellington.guardian.android.model.Section;

public class SectionDAO {

	private InMemorySectionCache inMemorySectionCache;
	private FileBasedSectionCache fileBasedSectionCache;
	private ContentSource api;
	
	public SectionDAO(Context context) {
		this.inMemorySectionCache = CacheFactory.getSectionCache();
		this.fileBasedSectionCache = new FileBasedSectionCache(context);
		api = ArticleDAOFactory.getOpenPlatformApi(context);
	}

	
	public List<Section> getSections() {
		List<Section> sections = inMemorySectionCache.getAll();
		if (sections != null && !sections.isEmpty()) {
			return sections;
		}
		
		sections = fileBasedSectionCache.getSections();
		if (sections != null) {
			inMemorySectionCache.addAll(sections);
			return sections;
		}
		
		sections = api.getSections();
		if (sections != null) {
			inMemorySectionCache.addAll(sections);
			fileBasedSectionCache.putSections(sections);
		}
		return sections;
	}
	
	public Map<String, Section> getSectionsMap() {
		Map<String, Section> sectionsMap = new HashMap<String, Section>();
		List<Section> sections = this.getSections();
		if (sections != null) {
			for (Section section : sections) {
				sectionsMap.put(section.getId(), section);
			}
		}
		return sectionsMap;
	}
	
	public void evictSections() {
		inMemorySectionCache.clear();
		fileBasedSectionCache.clear();
	}
	
}
