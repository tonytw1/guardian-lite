package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.AuthorArticleSet;
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

	protected ArticleSet getArticleSet() {
		return new AuthorArticleSet(author);
	}
	
		
}
