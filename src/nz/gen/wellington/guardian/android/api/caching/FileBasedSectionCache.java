package nz.gen.wellington.guardian.android.api.caching;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import nz.gen.wellington.guardian.android.api.openplatfrom.OpenPlatformJSONApi;
import nz.gen.wellington.guardian.android.model.Section;
import android.content.Context;
import android.util.Log;

public class FileBasedSectionCache {
	
	private static final String TAG = "FileBasedSectionCache";

	private Context context;
	
	public FileBasedSectionCache(Context context) {
		this.context = context;
	}
	
	
	
	public void putSections(List<Section> sections) {
		try {
			FileOutputStream fos = FileService.getFileOutputStream(context,
					OpenPlatformJSONApi.SECTIONS_API_URL);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(sections);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<Section> getSections() {
		if (!FileService.isLocallyCached(context,
				OpenPlatformJSONApi.SECTIONS_API_URL)) {
			return null;
		}
		Log.i(TAG, "Reading from disk: " + OpenPlatformJSONApi.SECTIONS_API_URL);
		try {
			FileInputStream fis = FileService.getFileInputStream(context, OpenPlatformJSONApi.SECTIONS_API_URL);
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
		Log.i(TAG, "Clearing sections");
		if (FileService.isLocallyCached(context, OpenPlatformJSONApi.SECTIONS_API_URL)) {			
			FileService.clear(context, OpenPlatformJSONApi.SECTIONS_API_URL);
			Log.i(TAG, "Cleared: " + OpenPlatformJSONApi.SECTIONS_API_URL);
		} else {
			Log.i(TAG, "No local copy to clear:" + OpenPlatformJSONApi.SECTIONS_API_URL);
		}
	}
		
}
