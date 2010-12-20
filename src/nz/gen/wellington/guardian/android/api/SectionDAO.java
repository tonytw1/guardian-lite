package nz.gen.wellington.guardian.android.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.api.caching.FileBasedSectionCache;
import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Section;
import android.content.Context;
import android.util.Log;

public class SectionDAO {

	private static final String TAG = "SectionDAO";
	
	private InMemorySectionCache inMemorySectionCache;
	private FileBasedSectionCache fileBasedSectionCache;
	private Context context;
	
	public SectionDAO(Context context) {
		this.context = context;
		this.inMemorySectionCache = CacheFactory.getSectionCache();
		this.fileBasedSectionCache = new FileBasedSectionCache(context);
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
		
		ContentSource api = SingletonFactory.getOpenPlatformApi(context);
		sections = api.getSections();
		if (sections != null) {
			inMemorySectionCache.addAll(sections);
			fileBasedSectionCache.putSections(sections);
		}
		return sections;
	}
	
	
	public Section getSectionById(String sectionId) {
		Map<String, Section> sections = getSectionsMap();
		if (sections != null && sections.containsKey(sectionId)) {
			return sections.get(sectionId);
		}
		return null;
	}
	
	
	// TODO could be private? anyone using this is probably doing something shady and might prefer using getById.
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
		Log.i(TAG, "Evicting section caches");
		inMemorySectionCache.clear();
		fileBasedSectionCache.clear();
	}
	
}
