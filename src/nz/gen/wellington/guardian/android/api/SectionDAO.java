package nz.gen.wellington.guardian.android.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import nz.gen.wellington.guardian.android.api.caching.FileBasedSectionCache;
import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;
import nz.gen.wellington.guardian.android.model.Section;

public class SectionDAO {

	InMemorySectionCache sectionCache;
	FileBasedSectionCache fileBasedSectionCache;
	ContentSource api;
	
	public SectionDAO(Context context) {
		this.sectionCache = CacheFactory.getSectionCache();
		this.fileBasedSectionCache = new FileBasedSectionCache(context);
		api = ArticleDAOFactory.getOpenPlatformApi(context);
	}

	
	public List<Section> getSections() {
		 List<Section> sections = fileBasedSectionCache.getSections();
		 if (sections != null) {
			 return sections;
		 }
		 
		 sections = api.getSections();
		 if (sections != null) {
			sectionCache.addAll(sections);
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
		sectionCache.clear();
		fileBasedSectionCache.clear();
	}
	
}
