package nz.gen.wellington.guardian.android.activities;

import java.util.List;

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
		this.setTitle(section.getName());
    	List<Article> articles = ArticleDAOFactory.getDao(this).getSectionItems(section);
    	if (articles != null) {
    		populateNewsitemList(articles);
    	} else {
    		Toast.makeText(this, "Could not load section articles", Toast.LENGTH_SHORT).show();   		
    	}
	}

}