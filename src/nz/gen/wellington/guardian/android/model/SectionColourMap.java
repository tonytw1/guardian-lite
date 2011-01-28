package nz.gen.wellington.guardian.android.model;

import java.util.HashMap;
import java.util.Map;

public class SectionColourMap {

	private static final String TRAVEL_BLUE = "#65C5FB";
	private static final String SPORT_GREEN = "#008000";
	private static final String MONEY_PURPLE = "#8F1AB6";
	private static final String LIFE_AND_STYLE_YELLOW = "#FFC202";
	private static final String FOOTBALL_GREEN = "#006000";
	private static final String CULTURE_PINK = "#D1008B";
	private static final String ENVIRONMENT_GREEN = "#7BBB00";
	private static final String GUARDIAN_RED = "#801100";
	private static final String CIF_BLUE = "#0061A6";
	private static final String BUSINESS_BLUE = "#4A64D9";
	private static final String DEFAULT_SECTION_COLOUR = "#D61D00";
	
	private static Map<String, String> sectionColours = buildSectionColourMap();

	public static String getColourForSection(String sectionId) {	
		if (sectionColours.containsKey(sectionId)) {
			return sectionColours.get(sectionId);
		}
		return DEFAULT_SECTION_COLOUR;
	}

	private static Map<String, String> buildSectionColourMap() {
		Map<String, String> sectionColours = new HashMap<String, String>();
		sectionColours.put("business", BUSINESS_BLUE);
		sectionColours.put("commentisfree", CIF_BLUE);
		sectionColours.put("culture", CULTURE_PINK);
		sectionColours.put("education", GUARDIAN_RED);
		sectionColours.put("environment", ENVIRONMENT_GREEN);
		sectionColours.put("football", FOOTBALL_GREEN);
		sectionColours.put("lifeandstyle", LIFE_AND_STYLE_YELLOW);
		sectionColours.put("media", GUARDIAN_RED);
		sectionColours.put("money", MONEY_PURPLE);
		sectionColours.put("politics", GUARDIAN_RED);
		sectionColours.put("science", GUARDIAN_RED);
		sectionColours.put("society", GUARDIAN_RED);
		sectionColours.put("sport", SPORT_GREEN);
		sectionColours.put("travel", TRAVEL_BLUE);
		return sectionColours;
	}

}
