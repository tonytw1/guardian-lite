package nz.gen.wellington.guardian.android.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import android.os.Bundle;
import android.widget.Toast;

public class section extends ArticleListActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	final Section section = (Section) this.getIntent().getExtras().get("section");
    	setHeading(section.getName());
    	setHeadingColour(getSectionColour(section));
				
    	List<Article> articles = ArticleDAOFactory.getDao(this).getSectionItems(section);
    	if (articles != null) {
    		populateNewsitemList(articles);
    	} else {
    		Toast.makeText(this, "Could not load section articles", Toast.LENGTH_SHORT).show();   		
    	}
	}

	private String getSectionColour(Section section) {
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
		
		if (sectionColours.containsKey(section.getId())) {
			return sectionColours.get(section.getId());
		}
		
		return "#D61D00";
	}

}