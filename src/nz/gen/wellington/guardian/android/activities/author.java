package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Tag;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.Toast;

public class author extends ArticleListActivity {

	ListAdapter adapter;
	
	public author() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
						
		final Tag author = (Tag) this.getIntent().getExtras().get("author");
    	setHeading(author.getName());

		List<Article> articles = ArticleDAOFactory.getDao(this).getAuthorItems(author);
    	if (articles != null) {
    		populateNewsitemList(articles);
    	} else {
    		Toast.makeText(this, "Could not load articles", Toast.LENGTH_SHORT).show();
    	}
	}
	
}
