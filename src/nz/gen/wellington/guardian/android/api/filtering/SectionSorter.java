package nz.gen.wellington.guardian.android.api.filtering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nz.gen.wellington.guardian.android.model.Section;

public class SectionSorter {
	
	public static List<Section> sortByName(List<Section> sections) {
		Map<String, Section> sortedSections = new TreeMap<String, Section>();						
		for (Section section : sections) {
			sortedSections.put(section.getName(), section);
		}
		return new ArrayList<Section>(sortedSections.values());
	}

}
