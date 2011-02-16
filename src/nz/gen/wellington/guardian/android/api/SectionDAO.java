/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.api.caching.FileBasedSectionCache;
import nz.gen.wellington.guardian.android.api.caching.InMemorySectionCache;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.model.Section;
import android.content.Context;
import android.util.Log;

public class SectionDAO {

	private static final String TAG = "SectionDAO";
	
	private InMemorySectionCache inMemorySectionCache;
	private FileBasedSectionCache fileBasedSectionCache;
	private NetworkStatusService networkStatusService;
	private ContentSource api;
	
	public SectionDAO(Context context) {
		this.inMemorySectionCache = CacheFactory.getSectionCache();
		this.fileBasedSectionCache = new FileBasedSectionCache(context);
		this.networkStatusService = SingletonFactory.getNetworkStatusService(context);
		this.api = SingletonFactory.getOpenPlatformApi(context);
	}
	
	
	public boolean areSectionsAvailable() {
		return !inMemorySectionCache.isEmpty() || fileBasedSectionCache.getSections() != null || networkStatusService.isConnectionAvailable();
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
