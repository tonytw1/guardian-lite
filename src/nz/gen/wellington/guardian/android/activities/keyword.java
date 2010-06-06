package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Tag;
import android.os.Bundle;
import android.widget.Toast;

public class keyword extends ArticleListActivity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		final Tag keyword = (Tag) this.getIntent().getExtras().get("keyword");
		
		if (keyword.getSection() != null) {
			setHeading(keyword.getSection().getName() + " - " + keyword.getName());
			setHeadingColour(keyword.getSection().getColour());
		} else {
			setHeading(keyword.getName());
		}
    	List<Article> articles = ArticleDAOFactory.getDao(this).getKeywordItems(keyword);
    	if (articles != null) {
    		populateNewsitemList(articles);
    	} else {
    		Toast.makeText(this, "Could not load articles", Toast.LENGTH_SHORT).show();
    	}
	}
	

}
