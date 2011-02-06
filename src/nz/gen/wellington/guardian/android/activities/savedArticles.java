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

import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.usersettings.FavouriteSectionsAndTagsDAO;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class savedArticles extends ArticleListActivity implements FontResizingActivity {
	
	private ArticleSetFactory articleSetFactory;
	private FavouriteSectionsAndTagsDAO favouriteSectionsAndTagsDAO;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		favouriteSectionsAndTagsDAO = SingletonFactory.getFavouriteSectionsAndTagsDAO(this.getApplicationContext());
		
    	setHeading("Saved items");
		setFontSize();
	}
	
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getSavedArticlesArticleSet(favouriteSectionsAndTagsDAO.getSavedArticleIds());
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		setFontSize();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.HOME, 0, "Home");
		MenuItem refreshOption = menu.add(0, MenuedActivity.REFRESH, 0, "Refresh");
		menu.add(0, MenuedActivity.REMOVE_ALL_SAVED, 0, "Remove all");
		enableMenuItemIfConnectionIsAvailable(refreshOption);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!super.onOptionsItemSelected(item)) {
			switch (item.getItemId()) {			
			case MenuedActivity.REMOVE_ALL_SAVED:
				removeAll();
				return true;
			}
		}
		return false;
	}
	
	private void removeAll() {
		favouriteSectionsAndTagsDAO.removeAllSavedArticles();
		refresh();
	}
	
}
