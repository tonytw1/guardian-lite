package nz.gen.wellington.guardian.android.usersettings;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.BlackOnWhiteColourScheme;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.model.WhiteOnBlackColourScheme;

public class SettingsDAO {

	private static final String TAG = "SettingsDAO";
		
	private static final String APP_PACKAGE = "nz.gen.wellington.guardian.android";

	private static final String GUARDIAN_LITE_PROXY_API_PREFIX = "http://2.guardian-lite.appspot.com";
	private static final String CONTENT_API_URL = "http://content.guardianapis.com";

	PreferencesDAO preferencesDAO;
	private int clientVersion = 0;
	
	public SettingsDAO(Context context) {
		super();
		preferencesDAO = SingletonFactory.getPreferencesDAO(context);
		setClientVersion(context);	// TODO is a setting, not a preference
	}

	public int getClientVersion() {
		return clientVersion;
	}
	
	public String getPreferedApiHost() {
		if (preferencesDAO.useContentApi()) {
			return CONTENT_API_URL;
		}
		return GUARDIAN_LITE_PROXY_API_PREFIX;
	}	

	public String getApiKey() {
		return preferencesDAO.getApiKey();
	}

	public int getBaseFontSize() {		
		return Integer.parseInt(preferencesDAO.getBaseFontSize());
	}
	
	public ColourScheme getColourScheme() {		
		Log.d(TAG, "Looking up colourScheme preference");
		final String colourSchemePreferences = preferencesDAO.getColourScheme();
		if (colourSchemePreferences.equals("BLACK_ON_WHITE")) {
			return new BlackOnWhiteColourScheme();
		}
		return new WhiteOnBlackColourScheme();		
	}

	public String getLargePicturesPreference() {
		return preferencesDAO.getLargePicturesPreference();
	}

	public int getPageSizePreference() {
		return Integer.parseInt(preferencesDAO.getPageSizePreference());
	}
	
	public String getSyncPreference() {
		return preferencesDAO.getSyncPreference();
	}

	public String getTrailPicturesPreference() {
		return preferencesDAO.getTrailPicturesPreference();
	}

	public boolean showDateRefinements() {
		return preferencesDAO.showDateRefinements();
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
