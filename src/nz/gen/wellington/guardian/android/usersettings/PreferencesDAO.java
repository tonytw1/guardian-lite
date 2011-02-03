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
	
	public String getPreference(String key, String defaultValue) {
		Log.d(TAG, "Looking up preference: " + key);		
		if (key.equals("useContentApi")) {			
			return ((Boolean) prefs.getBoolean(key, false)).toString();
		}
		if (key.equals("showDateDefinements")) {			
			return ((Boolean) prefs.getBoolean(key, false)).toString();
		}
		return (String) prefs.getString(key, defaultValue);		
	}
	
}
