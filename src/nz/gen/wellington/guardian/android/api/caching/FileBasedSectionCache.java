package nz.gen.wellington.guardian.android.api.caching;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import nz.gen.wellington.guardian.android.model.Section;
import android.content.Context;
import android.util.Log;

public class FileBasedSectionCache {
	
	private static final String SECTIONS_JSON = "sections.json";
	private static final String TAG = "FileBasedSectionCache";

	private Context context;
	
	public FileBasedSectionCache(Context context) {
		this.context = context;
	}

	public void putSections(List<Section> sections) {		
		final String filepath = SECTIONS_JSON;		
		Log.i(TAG, "Writing to disk: " + filepath);
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = context.openFileOutput(filepath, Context.MODE_PRIVATE);
			out = new ObjectOutputStream(fos);
			out.writeObject(sections);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
		
	@SuppressWarnings("unchecked")
	public List<Section> getSections() {
		if (!isLocallyCached(SECTIONS_JSON)) {
			return null;
		}
		
		final String filepath = SECTIONS_JSON;
		Log.i(TAG, "Reading from disk: " + filepath);

		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = context.openFileInput(filepath);
			in = new ObjectInputStream(fis);
			List<Section> loaded = (List<Section>) in.readObject();
			in.close();
			return loaded;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	public void clear() {
		Log.i(TAG, "Clearing sections");
		if (isLocallyCached(SECTIONS_JSON)) {
			File localFile = context.getFileStreamPath(SECTIONS_JSON);			
			localFile.delete();
			Log.i(TAG, "Cleared: " + SECTIONS_JSON);
		} else {
			Log.i(TAG, "No local copy to clear:" + SECTIONS_JSON);
		}
	}
	
	private boolean isLocallyCached(String filepath) {
		File localFile = context.getFileStreamPath(filepath);
		return localFile.exists() && localFile.canRead();
	}
	
}
