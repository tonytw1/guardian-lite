package nz.gen.wellington.guardian.android.usersettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferencesDAO {
	
	private static final String TAG = "PreferencesDAO";
	
	private SharedPreferences prefs;
	
	public PreferencesDAO(Context context) {
		prefs =  PreferenceManager.getDefaultSharedPreferences(context);
	}

	public String getPageSizePreference() {
		Log.d(TAG, "Looking up pageSize preference");
		return prefs.getString("pageSize", "10");
	}
	
	public String getTrailPicturesPreference() {
		Log.d(TAG, "Looking up trailImagesOption preference");
		return (String) prefs.getString("trailImagesOption", "ALWAYS");
	}

	public String getLargePicturesPreference() {
		Log.d(TAG, "Looking up largeImagesOption preference");
		return (String) prefs.getString("largeImagesOption", "WIFI_ONLY");
	}
	
	public String getSyncPreference() {
		Log.d(TAG, "Looking up syncType preference");
		return (String) prefs.getString("syncType", "NEVER");
	}

	public String getBaseFontSize() {
		Log.d(TAG, "Looking up baseFontSize preference");
		return prefs.getString("baseFontSize", "7");
	}
	
	public boolean useContentApi() {
		Log.d(TAG, "Looking up useContentApi preference");
		return prefs.getBoolean("useContentApi", false);		
	}
	
	public boolean showDateRefinements() {
		Log.d(TAG, "Looking up showDateDefinements preference");
		return prefs.getBoolean("showDateDefinements", false);
	}
	
	public String getApiKey() {
		Log.d(TAG, "Looking up contentApiKey preference");
		return (String) prefs.getString("contentApiKey", null);
	}
	
	public String getColourScheme() {
		Log.d(TAG, "Looking up colourScheme preference");
		return (String) prefs.getString("colourScheme", "WHITE_ON_BLACK");		
	}
	
}
