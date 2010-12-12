package nz.gen.wellington.guardian.android.api.caching;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.model.Section;

public class InMemorySectionCache {
	
	private List<Section> sections;
	
	public InMemorySectionCache() {
		this.sections = new ArrayList<Section>();
	}

	public void clear() {
		sections.clear();
	}
		
	public synchronized void addAll(List<Section> sections) {
		this.sections.clear();
		this.sections.addAll(sections);
	}

	public synchronized List<Section> getAll() {
		return new ArrayList<Section>(sections);	// TODO return imputable copy
	}

	public synchronized boolean isEmpty() {
		return sections.isEmpty();
	}

}
