/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class search extends ArticleListActivity {

	private static final String TAG = "search";

	private String searchTerm = "sausages";
	private MenuItem favouriteMenuItem;
	private FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
    private ArticleSetFactory articleSetFactory;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		this.favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());
		
		setContentView(R.layout.main);
		String heading = "Search results";
		if (searchTerm != null) {
			heading = heading + " - " + searchTerm;
		}
		setHeading(heading);
		
	}
	
	protected String getRefinementDescription(String refinementType) {
		if (refinementType.equals("keyword")) {
			return "Articles matching " + searchTerm + " have also been tagged with these keywords:";
		}
		return null;
	}
	
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getArticleSetForSearchTerm(searchTerm);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.HOME, 0, "Home");	
		MenuItem refreshOption = menu.add(0, MenuedActivity.REFRESH, 0, "Refresh");
		enableMenuItemIfConnectionIsAvailable(refreshOption);
		
		if (favouriteSectionsAndTagsDAO.isFavouriteSearchTerm(searchTerm)) {
			favouriteMenuItem = menu.add(0, MenuedActivity.ADD_REMOVE_FAVOURITE, 0, "Remove Favourite");
		} else {
			favouriteMenuItem = menu.add(0, MenuedActivity.ADD_REMOVE_FAVOURITE, 0, "Add to Favourites");
		}
		
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!super.onOptionsItemSelected(item)) {
			switch (item.getItemId()) {
			case MenuedActivity.ADD_REMOVE_FAVOURITE:
				addToFavourites();
				return true;		
			}
		}
		return false;
	}

	
	private void addToFavourites() {
		
		if (!favouriteSectionsAndTagsDAO.isFavouriteSearchTerm(searchTerm)) {
			Log.i(TAG, "Adding search term to favourites: " + searchTerm);
			if (favouriteSectionsAndTagsDAO.addSearchTerm(searchTerm)) {
				favouriteMenuItem.setTitle("Remove Favourite");			
			} else {
	        	Toast.makeText(this, "Favourites list is full", Toast.LENGTH_LONG).show();
			}
		
		} else {
			Log.i(TAG, "Removing search term from favourites: " + searchTerm);			
			favouriteSectionsAndTagsDAO.removeSearchTerm(searchTerm);
			favouriteMenuItem.setTitle("Add to Favourites");
		}		
	}
	
}
