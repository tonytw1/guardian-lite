package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.Tag;
import android.os.Bundle;
import android.widget.ListAdapter;

public class author extends ArticleListActivity {

	ListAdapter adapter;
	Tag author;
	
	public author() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
						
		author = (Tag) this.getIntent().getExtras().get("author");
    	setHeading(author.getName());		
	}

	@Override
	protected ArticleBundle loadArticles() {
		return articleDAO.getAuthorItems(author);
	}
	
		
}
