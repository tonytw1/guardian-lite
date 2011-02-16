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

package nz.gen.wellington.guardian.android.usersettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.model.Article;
import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

// TODO null safe all db opens.
public class SqlLiteFavouritesDAO {
	
	private static final String TAG = "SqlLiteFavouritesDAO";
	private static final int MAX_FAVOURITE_TAGS = 20;
	private static final int MAX_FAVOURITE_ARTICLES = 15;

	private static final String DATABASE_NAME = "guardian-lite.db";
	private static final int DATABASE_VERSION = 3;
	
	private static final String TAG_TABLE = "favourites";
	private static final String SAVED_ARTICLES_TABLE = "saved_articles";
	
	public static final String SECTIONID = "sectionid";
	public static final String ARTICLEID = "articleid";
	public static final String NAME = "name";
	public static final String APIID = "apiid";
	public static final String TYPE = "type";
	public static final String SEARCHTERM = "searchterm";
	
	private static final String ARTICLEID_DESC = "articleid DESC";
	private static final String NAME_ASC = "name asc";
	
	private static final String INSERT_FAVOURITE_TAG = "insert into " + TAG_TABLE + "(type, apiid, name, sectionid) values (?, ?, ?, ?)";
	private static final String INSERT_FAVOURITE_SEARCH_TERM = "insert into " + TAG_TABLE + "(type, searchTerm) values ('searchterm', ?)";
	private static final String INSERT_SAVED_ARTICLE = "insert into " + SAVED_ARTICLES_TABLE + "(articleid) values (?)";
	
	private OpenHelper openHelper;

	public SqlLiteFavouritesDAO(Context context) {
		openHelper = new OpenHelper(context);
	}
	
	public synchronized boolean hasFavourites() {	// TODO count query rather than select all
		SQLiteDatabase db = openHelper.getReadableDatabase();		
		Cursor cursor = db.query(TAG_TABLE, new String[] { TYPE, APIID, NAME,SECTIONID }, null, null, null, null, NAME_ASC);		
		int total = cursor.getCount();
		closeCursor(cursor);
		db.close();
		return total > 0;	
	}
	
	public synchronized long insertFavouriteTag(String type, String apiid, String name, String sectionid) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		SQLiteStatement insertStmt = db.compileStatement(INSERT_FAVOURITE_TAG);
		insertStmt.bindString(1, type);
		insertStmt.bindString(2, apiid);
		insertStmt.bindString(3, name);
		insertStmt.bindString(4, sectionid);
		long result = insertStmt.executeInsert();
		db.close();
		return result;
	}
	
	
	private synchronized long insertFavouriteSearchTerm(String searchTerm) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		SQLiteStatement insertStmt = db.compileStatement(INSERT_FAVOURITE_SEARCH_TERM);
		insertStmt.bindString(1, searchTerm);		
		long result = insertStmt.executeInsert();
		db.close();
		return result;
	}
	
	
	private synchronized long insertSavedArticle(String articleId) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		SQLiteStatement insertStmt = db.compileStatement(INSERT_SAVED_ARTICLE);
		insertStmt.bindString(1, articleId);		
		long result = insertStmt.executeInsert();
		db.close();
		return result;
		
	}
	
		
	public synchronized void deleteAll() {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.delete(TAG_TABLE, null, null);
		db.close();
	}
	
	
	public synchronized boolean isFavourite(Tag tag) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.query(TAG_TABLE, new String[] { APIID }, " type = 'tag' and apiid = ? ", new String[] { tag.getId() }, null, null, NAME_ASC);
		final boolean isFavourite = cursor.getCount() > 0;
		closeCursor(cursor);
		final boolean result = isFavourite;	
		db.close();
		return result;
	}
	

	public synchronized boolean isFavourite(Section section) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.query(TAG_TABLE, new String[] { APIID }, " type = 'section' and apiid = ? ", new String[] { section.getId() }, null, null, NAME_ASC);
		final boolean isFavourite = cursor.getCount() > 0;
		closeCursor(cursor);		
		db.close();
		return isFavourite;	
	}
	
	public boolean isFavouriteSearchTerm(String searchTerm) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.query(TAG_TABLE, new String[] { APIID }, " searchterm = ? ", new String[] { searchTerm }, null, null, NAME_ASC);
		final boolean isFavourite = cursor.getCount() > 0;
		closeCursor(cursor);
		db.close();
		return isFavourite;
	}
	
	public boolean isSavedArticle(Article article) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.query(SAVED_ARTICLES_TABLE, new String[] { ARTICLEID }, " articleid = ? ", new String[] { article.getId() }, null, null, ARTICLEID_DESC);
		final boolean isSaved = cursor.getCount() > 0;
		closeCursor(cursor);
		db.close();
		return isSaved;
	}
	
	public synchronized void removeSection(Section section) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.delete(TAG_TABLE, " apiid = ? ", new String[] { section.getId() });
		db.close();
	}
	
	public synchronized void removeTag(Tag tag) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.delete(TAG_TABLE, " apiid = ? ", new String[] { tag.getId() });
		db.close();
	}
	
	public synchronized void removeSearchTerm(String searchTerm) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.delete(TAG_TABLE, " searchterm = ? ", new String[] { searchTerm });
		db.close();
	}
	
	public synchronized boolean removeSavedArticle(Article article) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		final int deleted = db.delete(SAVED_ARTICLES_TABLE, " articleid = ? ", new String[] { article.getId() });
		db.close();
		return deleted > 0;
	}
	
	
	public synchronized void removeAllSavedArticles() {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + SAVED_ARTICLES_TABLE);
		db.close();
	}
	
	

	public synchronized List<Map<String, String>> getFavouriteRows() {
		List<Map<String, String>> rows = new ArrayList<Map<String,String>>();

		SQLiteDatabase db = openHelper.getReadableDatabase();
		if (db != null && db.isOpen()) {
			Cursor cursor = db.query(TAG_TABLE, new String[] {TYPE, APIID, NAME, SECTIONID, SEARCHTERM}, null, null, null, null, NAME_ASC);
			if (cursor.moveToFirst()) {
				do {
					Map<String, String> row = new HashMap<String, String>();
					row.put(TYPE,  cursor.getString(0));
					row.put(APIID,  cursor.getString(1));
					row.put(NAME,  cursor.getString(2));
					row.put(SECTIONID,  cursor.getString(3));
					row.put(SEARCHTERM,  cursor.getString(4));
					rows.add(row);
					
				} while (cursor.moveToNext());
			}
			closeCursor(cursor);
			db.close();
		} else {
			Log.i(TAG, "Could not open readable database connection");
		}
		return rows;
	}
	
	
	public List<String> getSavedArticleIds() {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.query(SAVED_ARTICLES_TABLE, new String[] {ARTICLEID}, null, null, null, null, ARTICLEID_DESC);
		
		List<String> savedArticleIds = new ArrayList<String>();
		if (cursor.moveToFirst()) {
			do {
				final String articleId = cursor.getString(0);
				savedArticleIds.add(articleId);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		db.close();
		return savedArticleIds;
	}
	
	
	public synchronized boolean addTag(Tag keyword) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		boolean result = false;
		if (haveRoom(db)) {
			this.insertFavouriteTag("tag", keyword.getId(), keyword.getName(), (keyword.getSection() != null) ? keyword.getSection().getId(): "global");
			result = true;
		}
		db.close();
		return result;		
	}
	
	
	public synchronized boolean addSection(Section section) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		boolean result = false;
		if (this.haveRoom(db)) {
			this.insertFavouriteTag("section", section.getId(), section.getName(), section.getId());
			result = true;
		}
		db.close();
		return result;
	}
	
	public boolean addSearchTerm(String searchTerm) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		boolean result = false;
		if (this.haveRoom(db)) {
			this.insertFavouriteSearchTerm(searchTerm);	// TODO Meh - assumes success
			result = true;
		}
		db.close();
		return result;
	}
	
	public boolean addSavedArticle(Article article) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		boolean result = false;
		if (this.haveRoomForSavedArticle(db)) {
			this.insertSavedArticle(article.getId());
			result = true;
		}
		db.close();
		return result;
	}
	
	private boolean haveRoomForSavedArticle(SQLiteDatabase db) {
		Cursor cursor = db.query(SAVED_ARTICLES_TABLE, new String[] {ARTICLEID}, null, null, null, null, ARTICLEID_DESC);		
		int total = cursor.getCount();
		closeCursor(cursor);
		return total < MAX_FAVOURITE_ARTICLES;	
	}

	private boolean haveRoom(SQLiteDatabase db) {
		Cursor cursor = db.query(TAG_TABLE, new String[] {TYPE, APIID, NAME,SECTIONID}, null, null, null, null, NAME_ASC);		
		int total = cursor.getCount();
		closeCursor(cursor);
		return total < MAX_FAVOURITE_TAGS;	
	}
	
	private void closeCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}	
		
	private static class OpenHelper extends SQLiteOpenHelper {
		
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public synchronized SQLiteDatabase getReadableDatabase() {
			Log.d(TAG, "getReadableDatabase called");
			return super.getReadableDatabase();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TAG_TABLE + "(id INTEGER PRIMARY KEY, type, apiid, name, sectionid TEXT)");
			upgradeFromOneToTwo(db);
			upgradeFromTwoToThree(db);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion == 2 && newVersion == 3) {
				upgradeFromTwoToThree(db);
			}
			
			if (oldVersion == 1 && newVersion == 3) {
				upgradeFromOneToTwo(db);
				upgradeFromTwoToThree(db);
			}			
		}
		
		private void upgradeFromTwoToThree(SQLiteDatabase db) {
			addSearchTerm(db);
		}
				
		private void upgradeFromOneToTwo(SQLiteDatabase db) {
			createSavedArticlesTable(db);
		}
		
		private void createSavedArticlesTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + SAVED_ARTICLES_TABLE + "(id INTEGER PRIMARY KEY, articleid TEXT)");
		}
		
		private void addSearchTerm(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE " + TAG_TABLE + " ADD COLUMN searchterm TEXT");
		}
		
	}
	
}
