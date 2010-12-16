package nz.gen.wellington.guardian.android.usersettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesDAO {
	
	private static final String GUARDIAN_LITE_PROXY_API_PREFIX = "http://guardian-lite.appspot.com";
	private static final String CONTENT_API_URL = "http://content.guardianapis.com";

	private SharedPreferences prefs;

	public PreferencesDAO(Context context) {
		prefs =  PreferenceManager.getDefaultSharedPreferences(context);
	}

	public int getPageSizePreference() {
		final String pageSizeString = prefs.getString("pageSize", "10");
		return Integer.parseInt(pageSizeString);
	}

	public String getLargePicturesPreference() {
		return (String) prefs.getString("largeImagesOption", "WIFI_ONLY");
	}
	
	public String getSyncPreference() {
		return (String) prefs.getString("syncType", "NEVER");
	}

	public int getBaseFontSize() {
		final String baseSizeString = prefs.getString("baseFontSize", "7");
		return Integer.parseInt(baseSizeString);
	}
	
	public boolean useContentApi() {
		return prefs.getBoolean("useContentApi", false);		
	}
	
	public String getPreferedApiHost() {
		if (useContentApi()) {
			return CONTENT_API_URL;
		}
		return GUARDIAN_LITE_PROXY_API_PREFIX;
	}

	public String getApiKey() {
		return (String) prefs.getString("contentApiKey", null);
	}
	
}
