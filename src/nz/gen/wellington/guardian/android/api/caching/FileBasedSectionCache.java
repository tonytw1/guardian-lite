package nz.gen.wellington.guardian.android.api.caching;

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
	
	private static final String TAG = "FileBasedSectionCache";
	private static final String SECTIONS_FILE = "sections";
	
	private Context context;
	
	public FileBasedSectionCache(Context context) {
		this.context = context;
	}
	
	public void putSections(List<Section> sections) {
		try {
			FileOutputStream fos = FileService.getFileOutputStream(context, SECTIONS_FILE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(sections);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Section> getSections() {
		if (!FileService.existsLocally(context, SECTIONS_FILE)) {
			return null;
		}
		Log.i(TAG, "Reading from disk: " + SECTIONS_FILE);
		try {
			FileInputStream fis = FileService.getFileInputStream(context, SECTIONS_FILE);
			ObjectInputStream in = new ObjectInputStream(fis);
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
		if (FileService.existsLocally(context, SECTIONS_FILE)) {			
			FileService.clear(context, SECTIONS_FILE);			
		} else {
			Log.i(TAG, "No local copy to clear:" + SECTIONS_FILE);
		}
	}
		
}
