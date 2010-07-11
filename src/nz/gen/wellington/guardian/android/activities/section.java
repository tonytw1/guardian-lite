package nz.gen.wellington.guardian.android.activities;

import java.util.Arrays;
import java.util.List;

import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.sqllite.DataHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class section extends ArticleListActivity {
	
	private static final String TAG = "section";	
	private Section section;
	private MenuItem favouriteMenuItem;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        section = (Section) this.getIntent().getExtras().get("section");
    	setHeading(section.getName());
    	setHeadingColour(section.getColour());
    	updateArticlesHandler = new UpdateArticlesHandler(this, getArticleSet());
	}

	
	protected ArticleSet getArticleSet() {
		return new SectionArticleSet(section);
	}
	
	protected List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
		
	@Override
	protected String getRefinementDescription(String refinementType) {
		if (refinementType.equals("blog")) {
			return "These blog tags have been used recently in the " + getArticleSet().getName() + " section:";
		} else if (refinementType.equals("contributor")) { 
			return "These contributors have appeared recently in the " + getArticleSet().getName() + " section:";
		}	
		return "These keywords have been used recently within the " + getArticleSet().getName() + " section:";
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Home");
		menu.add(0, 5, 0, "Refresh");
		
		DataHelper dh = new DataHelper(this);
		if (dh.isFavourite(section)) {
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
		case 5:
			refresh(true);
			return true;
		}
		return false;
	}
	
	
	private void addToFavourites() {
		DataHelper dh = new DataHelper(this);
		if (!dh.isFavourite(section)) {
			
			boolean haveRoom = dh.haveRoom();
			if (haveRoom) {
				Log.i(TAG, "Adding current section to favourites: " + section.getName());
				dh.addSection(section);
				favouriteMenuItem.setTitle("Remove Favourite");
			} else {
	        	Toast.makeText(this, "Favourites list is full", Toast.LENGTH_LONG).show();
			}
	
		} else {
			Log.i(TAG, "Removing current section from favourites: " + section.getName());			
			dh.removeSection(section);
			favouriteMenuItem.setTitle("Add to Favourites");
		}
		dh.close();	
	}
	
}