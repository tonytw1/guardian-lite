package nz.gen.wellington.guardian.android.sqllite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.keyword;
import nz.gen.wellington.guardian.android.activities.sections;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataHelper {

	private static final String TAG = "DataHelper";
	
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
		Cursor cursor = this.db.query(TAG_TABLE, new String[] { "apiid" }, " apiid = ? ", new String[] { tag.getId() }, null, null, "name desc");
		return cursor.getCount() > 0;		
	}
	
	
	
	public void removeTag(Tag keyword) {
		this.db.delete(TAG_TABLE, " apiid = ? ", new String[] { keyword.getId() });		
	}
	
	
	public List<Tag> selectAll(Map<String, Section> map) {
		Cursor cursor = this.db.query(TAG_TABLE, new String[] { "type", "apiid", "name","sectionid" }, null, null, null, null, "name desc");
		
		List<Tag> favouriteTags = new ArrayList<Tag>();
		if (cursor.moveToFirst()) {
			do {
				final String type = cursor.getString(0);
				final String id = cursor.getString(1);
				final String name = cursor.getString(2);
				final String sectionId = cursor.getString(3);
				Log.i(TAG, type + ", " + name + ", " + id);
				if (type.equals("keyword")) {
					favouriteTags.add(new Tag(name, id, map.get(sectionId)));
				}
				
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return favouriteTags;
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
			Log.w("Example", "Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE);
			onCreate(db);
		}
	}



	public void close() {
		db.close();		
	}


	public boolean isFavourite(Section section) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
