/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.usersettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.colourscheme.BlackOnWhiteColourScheme;
import nz.gen.wellington.guardian.android.model.colourscheme.ColourScheme;
import nz.gen.wellington.guardian.android.model.colourscheme.WhiteOnBlackColourScheme;
import nz.gen.wellington.guardian.model.Tag;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class SettingsDAO {

	private static final String TRAIL_IMAGES_DOWNLOAD_DEFAULT = "ALWAYS";
	private static final String DEFAULT_LARGE_IMAGES_DOWNLOAD_SETTING = "ALWAYS";
	private static final String DEFAULT_PAGE_SIZE = "15";
	private static final String DEFAULT_COLOUR_SCHEME = "WHITE_ON_BLACK";
	private static final String DEFAULT_BASE_FONT_SIZE = "7";

	private static final String TAG = "SettingsDAO";
		
	private static final String APP_PACKAGE = "nz.gen.wellington.guardian.android";

	private static final String GUARDIAN_LITE_PROXY_API_PREFIX = "http://3.guardian-lite.appspot.com";
	private static final String CONTENT_API_URL = "http://content.guardianapis.com";
	
	private static Tag articleContentType = new Tag("Article content type", "type/article", null, "type");
	private static Tag galleryContentType = new Tag("Gallery content type", "type/gallery", null, "type");
	private static List<Tag> supportedContentTypes = Arrays.asList(articleContentType, galleryContentType);
	
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
		if (isUsingContentApi()) {
			return CONTENT_API_URL;
		}
		return GUARDIAN_LITE_PROXY_API_PREFIX;
	}
	
	public String getApiKey() {
		if (isUsingContentApi()) {
			return getPreference("contentApiKey", null);
		}
		return null;
	}
	
	public int getBaseFontSize() {		
		return Integer.parseInt(getPreference("baseFontSize", DEFAULT_BASE_FONT_SIZE));
	}
	
	public ColourScheme getColourScheme() {		
		final String colourSchemePreferences = getPreference("colourScheme", DEFAULT_COLOUR_SCHEME);
		if (colourSchemePreferences.equals(DEFAULT_COLOUR_SCHEME)) {
			return new BlackOnWhiteColourScheme();
		}
		return new WhiteOnBlackColourScheme();		
	}
	
	public String getLargePicturesPreference() {
		return getPreference("largeImagesOption", DEFAULT_LARGE_IMAGES_DOWNLOAD_SETTING);
	}

	public int getPageSizePreference() {
		return Integer.parseInt(getPreference("pageSize", DEFAULT_PAGE_SIZE));
	}
	
	public String getSyncPreference() {
		return getPreference("syncType", "NEVER");
	}

	public String getTrailPicturesPreference() {
		return getPreference("trailImagesOption", TRAIL_IMAGES_DOWNLOAD_DEFAULT);
	}
	
	public void clearCache() {
		Log.i(TAG, "Clearing cache");		
		cache.clear();
	}
	
	public List<Tag> getSupportedContentTypes() {
		return supportedContentTypes;	// TODO immuntable
	}
	
	private void setClientVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(APP_PACKAGE, PackageManager.GET_META_DATA);
			this.clientVersion = pInfo.versionCode;
		} catch (Exception e) {
			Log.w(TAG, "Failed to get client version: " + e.getMessage());
		}
	}
	
	private String getPreference(String key, String defaultValue) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		cache.put(key, preferencesDAO.getPreference(key, defaultValue));
		return cache.get(key);
	}
	
	private boolean isUsingContentApi() {
		return getPreference("useContentApi", "false").equals("true");
	}

}
