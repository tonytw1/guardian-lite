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
		menu.add(0, 1, 0, "Home");
		menu.add(0, 2, 0, "Favourites");
		menu.add(0, 3, 0, "Sections");
		DataHelper dh = new DataHelper(this);
		if (dh.isFavourite(keyword)) {
			favouriteMenuItem = menu.add(0, 4, 0, "Remove Favourite");
		} else {
			favouriteMenuItem = menu.add(0, 4, 0, "Add to Favourites");
		}
		dh.close();
	    return true;
	}
	
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			switchToMain();
			return true;
		case 2:
			switchToFavourites();
			return true;
		case 3:
			switchToSections();
			return true;
		case 4:
			addToFavourites();
			return true;
		}
		return false;
	}

	
	private void addToFavourites() {
		DataHelper dh = new DataHelper(this);
		if (!dh.isFavourite(keyword)) {
			Log.i(TAG, "Adding current tag to favourites: " + keyword.getName());
			dh.addTag(keyword);
			favouriteMenuItem.setTitle("Remove Favourite");			
	
		} else {
			Log.i(TAG, "Removing current tag from favourites: " + keyword.getName());			
			dh.removeTag(keyword);
			favouriteMenuItem.setTitle("Add to Favourites");
		}
		dh.close();	
	}
	
	private void switchToMain() {
		Intent intent = new Intent(this, main.class);
		this.startActivity(intent);	
	}
	
	private void switchToFavourites() {
		Intent intent = new Intent(this, favourites.class);
		this.startActivity(intent);		
	}
	
	private void switchToSections() {
		Intent intent = new Intent(this, sections.class);
		this.startActivity(intent);		
	}
	
}
