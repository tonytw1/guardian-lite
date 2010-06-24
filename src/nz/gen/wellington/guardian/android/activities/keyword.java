package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.sqllite.DataHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;

public class keyword extends ArticleListActivity {

	private static final String TAG = "keyword";

	ListAdapter adapter;
	Tag keyword;
	MenuItem favouriteMenuItem;
		
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
		DataHelper dh = new DataHelper(this);
		if (dh.isFavourite(keyword)) {
			favouriteMenuItem = menu.add(0, 1, 0, "Remove from Favourites");
		} else {
			favouriteMenuItem = menu.add(0, 1, 0, "Add to Favourites");
		}
		dh.close();
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
		DataHelper dh = new DataHelper(this);
		if (!dh.isFavourite(keyword)) {
			Log.i(TAG, "Adding current tag to favourites: " + keyword.getName());
			dh.insert("keyword", keyword.getId(), keyword.getName(), (keyword.getSection() != null) ? keyword.getSection().getId(): "global");
			favouriteMenuItem.setTitle("Remove from Favourites");			
	
		} else {
			Log.i(TAG, "Removing current tag from favourites: " + keyword.getName());			
			dh.removeTag(keyword);
			favouriteMenuItem.setTitle("Add to Favourites");
		}
		dh.close();	
	}
	
}
