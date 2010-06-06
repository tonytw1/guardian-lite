package nz.gen.wellington.guardian.android.model;

import java.util.HashMap;
import java.util.Map;

public class SectionColourMap {

	public static String getColourForSection(String sectionId) {
		Map<String, String> sectionColours = new HashMap<String, String>();
		
		sectionColours.put("business", "#8F1AB6");
		sectionColours.put("commentisfree", "#0061A6");
		sectionColours.put("culture", "#D1008B");
		sectionColours.put("environment", "#7BBB00");
		sectionColours.put("lifeandstyle", "#FFC202");
		sectionColours.put("money", "#8F1AB6");	
		sectionColours.put("politics", "#801100");
		sectionColours.put("media", "#801100");
		sectionColours.put("education", "#801100");
		sectionColours.put("society", "#801100");
		sectionColours.put("science", "#801100");
		sectionColours.put("sport", "#008000");
		sectionColours.put("football", "#006000");
		
		if (sectionColours.containsKey(sectionId)) {
			return sectionColours.get(sectionId);
		}
		
		return "#D61D00";
	}

}
