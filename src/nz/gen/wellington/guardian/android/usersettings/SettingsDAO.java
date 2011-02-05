package nz.gen.wellington.guardian.android.usersettings;

import java.util.HashMap;
import java.util.Map;

import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.BlackOnWhiteColourScheme;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.model.WhiteOnBlackColourScheme;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class SettingsDAO {

	private static final String TAG = "SettingsDAO";
		
	private static final String APP_PACKAGE = "nz.gen.wellington.guardian.android";

	private static final String GUARDIAN_LITE_PROXY_API_PREFIX = "http://2.guardian-lite.appspot.com";
	private static final String CONTENT_API_URL = "http://content.guardianapis.com";

	private PreferencesDAO preferencesDAO;
	private int clientVersion = 0;
	private Map<String, String> cache;
	
	public SettingsDAO(Context context) {
		super();
		preferencesDAO = SingletonFactory.getPreferencesDAO(context);
		setClientVersion(context);
		cache = new HashMap<String, String>();
	}

	public int getClientVersion() {
		return clientVersion;
	}
	
	public String getPreferedApiHost() {
		if (getPreference("useContentApi", "false").equals("true")) {
			return CONTENT_API_URL;
		}
		return GUARDIAN_LITE_PROXY_API_PREFIX;
	}

	public String getApiKey() {
		return getPreference("contentApiKey", null);
	}

	public int getBaseFontSize() {		
		return Integer.parseInt(getPreference("baseFontSize", "7"));
	}
	
	public ColourScheme getColourScheme() {		
		final String colourSchemePreferences = getPreference("colourScheme", "BLACK_ON_WHITE");
		if (colourSchemePreferences.equals("BLACK_ON_WHITE")) {
			return new BlackOnWhiteColourScheme();
		}
		return new WhiteOnBlackColourScheme();		
	}
	
	public String getLargePicturesPreference() {
		return getPreference("largeImagesOption", "WIFI_ONLY");
	}

	public int getPageSizePreference() {
		return Integer.parseInt(getPreference("pageSize", "10"));
	}
	
	public String getSyncPreference() {
		return getPreference("syncType", "NEVER");
	}

	public String getTrailPicturesPreference() {
		return getPreference("trailImagesOption", "ALWAYS");
	}

	public boolean showDateRefinements() {
		return new Boolean(getPreference("showDateDefinements", "true"));
	}
		
	private void setClientVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(APP_PACKAGE, PackageManager.GET_META_DATA);
			this.clientVersion = pInfo.versionCode;
		} catch (Exception e) {
			Log.w(TAG, "Failed to get client version: " + e.getMessage());
		}
	}

	public void clearCache() {
		Log.i(TAG, "Clearing cache");		
		cache.clear();
	}
	
	private String getPreference(String key, String defaultValue) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		cache.put(key, preferencesDAO.getPreference(key, defaultValue));
		return cache.get(key);
	}

}
