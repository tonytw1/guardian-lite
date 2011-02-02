package nz.gen.wellington.guardian.android.usersettings;

import nz.gen.wellington.guardian.android.model.BlackOnWhiteColourScheme;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.model.WhiteOnBlackColourScheme;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferencesDAO {
	
	private static final String APP_PACKAGE = "nz.gen.wellington.guardian.android";

	private static final String TAG = "PreferencesDAO";
	
	// TODO suggests the need for a settings DAO in front of the preference doa.
	private static final String GUARDIAN_LITE_PROXY_API_PREFIX = "http://2.guardian-lite.appspot.com";
	private static final String CONTENT_API_URL = "http://content.guardianapis.com";

	private SharedPreferences prefs;
	private int clientVersion = 0;
	
	public PreferencesDAO(Context context) {
		prefs =  PreferenceManager.getDefaultSharedPreferences(context);
		setClientVersion(context);	// TODO is a setting, not a preference
	}

	public int getPageSizePreference() {
		Log.d(TAG, "Looking up pageSize preference");
		final String pageSizeString = prefs.getString("pageSize", "10");
		return Integer.parseInt(pageSizeString);
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

	public int getBaseFontSize() {
		Log.d(TAG, "Looking up baseFontSize preference");
		final String baseSizeString = prefs.getString("baseFontSize", "7");
		return Integer.parseInt(baseSizeString);
	}
	
	public boolean useContentApi() {
		Log.d(TAG, "Looking up useContentApi preference");
		return prefs.getBoolean("useContentApi", false);		
	}
	
	public boolean showDateRefinements() {
		Log.d(TAG, "Looking up showDateDefinements preference");
		return prefs.getBoolean("showDateDefinements", false);
	}
	
	public String getPreferedApiHost() {
		if (useContentApi()) {
			return CONTENT_API_URL;
		}
		return GUARDIAN_LITE_PROXY_API_PREFIX;
	}

	public String getApiKey() {
		Log.d(TAG, "Looking up contentApiKey preference");
		return (String) prefs.getString("contentApiKey", null);
	}
	
	public int getClientVersion() {
		return clientVersion;
	}

	public ColourScheme getColourScheme() {
		Log.d(TAG, "Looking up colourScheme preference");
		final String colourSchemePreferences = (String) prefs.getString("colourScheme", "WHITE_ON_BLACK");
		if (colourSchemePreferences.equals("BLACK_ON_WHITE")) {
			return new BlackOnWhiteColourScheme();
		}
		return new WhiteOnBlackColourScheme();
	}
	
	private void setClientVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(APP_PACKAGE, PackageManager.GET_META_DATA);
			this.clientVersion = pInfo.versionCode;
		} catch (Exception e) {
			Log.w(TAG, "Failed to get client version: " + e.getMessage());
		}
	}
	
}
