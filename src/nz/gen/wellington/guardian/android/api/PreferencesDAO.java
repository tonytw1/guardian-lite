package nz.gen.wellington.guardian.android.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesDAO {

	private SharedPreferences prefs;

	public PreferencesDAO(Context context) {
		prefs =  PreferenceManager.getDefaultSharedPreferences(context);
	}

	public int getPageSizePreference() {
		final String pageSizeString = prefs.getString("pageSize", "10");
		int pageSize = Integer.parseInt(pageSizeString);
		return pageSize;
	}

	public boolean getLargePicturesPreference() {
		return (Boolean) prefs.getBoolean("largeImages", false);
	}
	
}
