package nz.gen.wellington.guardian.android.sqllite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DataHelper {
	
	private Context context;
	private SQLiteDatabase db;

	private static final String DATABASE_NAME = "guardian-lite.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TAG_TABLE = "favourites";

	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + TAG_TABLE + "(type, apiid, name, sectionid) values (?, ?, ?, ?)";
	

	public DataHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);
	}
		
		
	public long insert(String type, String apiid, String name, String sectionid) {
		this.insertStmt.bindString(1, type);
		this.insertStmt.bindString(2, apiid);
		this.insertStmt.bindString(3, name);
		this.insertStmt.bindString(4, sectionid);
		return this.insertStmt.executeInsert();		
	}
	
		
	public void deleteAll() {
		this.db.delete(TAG_TABLE, null, null);
	}
	
	
	public boolean isFavourite(Tag tag) {
		Cursor cursor = this.db.query(TAG_TABLE, new String[] { "apiid" }, " type = 'tag' and apiid = ? ", new String[] { tag.getId() }, null, null, "name asc");
		final boolean isFavourite = cursor.getCount() > 0;
		closeCursor(cursor);
		return isFavourite;	
	}
	

	public boolean isFavourite(Section section) {
		Cursor cursor = this.db.query(TAG_TABLE, new String[] { "apiid" }, " type = 'section' and apiid = ? ", new String[] { section.getId() }, null, null, "name asc");
		final boolean isFavourite = cursor.getCount() > 0;
		closeCursor(cursor);
		return isFavourite;	
	}
	
	public void removeSection(Section section) {
		this.db.delete(TAG_TABLE, " apiid = ? ", new String[] { section.getId() });
	}
	
	public void removeTag(Tag tag) {
		this.db.delete(TAG_TABLE, " apiid = ? ", new String[] { tag.getId() });		
	}
	
	
	public List<Tag> getFavouriteTags(Map<String, Section> sectionsMap) {
		Cursor cursor = this.db.query(TAG_TABLE, new String[] { "type", "apiid", "name","sectionid" }, null, null, null, null, "name asc");
		
		List<Tag> favouriteTags = new ArrayList<Tag>();
		if (cursor.moveToFirst()) {
			do {
				final String type = cursor.getString(0);
				final String id = cursor.getString(1);
				final String name = cursor.getString(2);
				final String sectionId = cursor.getString(3);
				if (type.equals("tag")) {
					favouriteTags.add(new Tag(name, id, sectionsMap.get(sectionId)));
				}
				
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return favouriteTags;
	}


	public List<Section> getFavouriteSections(Map<String, Section> sectionsMap) {
		Cursor cursor = this.db.query(TAG_TABLE, new String[] { "type", "apiid", "name","sectionid" }, null, null, null, null, "name asc");
		
		List<Section> favouriteSections = new ArrayList<Section>();
		if (cursor.moveToFirst()) {
			do {
				final String type = cursor.getString(0);
				final String sectionId = cursor.getString(3);
				if (type.equals("section")) {
					Section section = sectionsMap.get(sectionId);
					if (section != null) {
						favouriteSections.add(section);
					}
				}
				
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return favouriteSections;
	}
	
	
	public void addTag(Tag keyword) {
		this.insert("tag", keyword.getId(), keyword.getName(), (keyword.getSection() != null) ? keyword.getSection().getId(): "global");
	}
	
	
	public void addSection(Section section) {
		this.insert("section", section.getId(), section.getName(), section.getId());		
	}
	
	
	public void close() {
		db.close();		
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
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TAG_TABLE + "(id INTEGER PRIMARY KEY, type, apiid, name, sectionid TEXT)");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE);
			onCreate(db);
		}
	}
	
}
