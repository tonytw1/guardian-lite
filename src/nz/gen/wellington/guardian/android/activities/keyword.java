package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.sqllite.DataHelper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;

public class keyword extends ArticleListActivity {

	private static final String TAG = "keyword";

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
		return articleDAO.getKeywordItems(keyword);
	}
	
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Add to Favourites");
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	    case 1: 	    	
	    	addToFavourites();
	    	return true;	 
	    }
	    return false;
	}	

	private void addToFavourites() {	
		Log.i(TAG, "Adding current tag to favourites: " + keyword.getName());
		DataHelper dh = new DataHelper(this);
		dh.insert("keyword", keyword.getId(), keyword.getName(), keyword.getSection().getId());		
	}
	
}
