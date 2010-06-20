package nz.gen.wellington.guardian.android.api.caching;

import nz.gen.wellington.guardian.android.api.openplatfrom.OpenPlatformJSONApi;
import android.content.Context;
import android.util.Log;

public class FileBasedSectionCache {
	
	private static final String TAG = "FileBasedSectionCache";

	private Context context;
	
	public FileBasedSectionCache(Context context) {
		this.context = context;
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
