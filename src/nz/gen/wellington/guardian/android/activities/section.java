package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import android.os.Bundle;

public class section extends ArticleListActivity {
	
	private Section section;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        section = (Section) this.getIntent().getExtras().get("section");
    	setHeading(section.getName());
    	setHeadingColour(section.getColour());
    	updateArticlesHandler = new UpdateArticlesHandler(this);
	}


	@Override
	protected List<Article> loadArticles() {
		return articleDAO.getSectionItems(section);
	}
	
}