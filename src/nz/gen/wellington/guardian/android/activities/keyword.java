package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Tag;
import android.os.Bundle;
import android.widget.ListAdapter;

public class keyword extends ArticleListActivity {

	ListAdapter adapter;
	Tag keyword;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		keyword = (Tag) this.getIntent().getExtras().get("keyword");		
		if (keyword.getSection() != null) {
			setHeading(keyword.getSection().getName() + " - " + keyword.getName());
			setHeadingColour(keyword.getSection().getColour());
		} else {
			setHeading(keyword.getName());
		}	
		updateArticlesHandler = new UpdateArticlesHandler(this);
	}
	
	@Override
	protected List<Article> loadArticles() {
		return ArticleDAOFactory.getDao(this.getApplicationContext()).getKeywordItems(keyword);
	}
}
