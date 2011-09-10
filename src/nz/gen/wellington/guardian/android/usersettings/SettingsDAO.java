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

import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleApi;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ContentTags;
import nz.gen.wellington.guardian.android.model.colourscheme.BlackOnWhiteColourScheme;
import nz.gen.wellington.guardian.android.model.colourscheme.ColourScheme;
import nz.gen.wellington.guardian.android.model.colourscheme.WhiteOnBlackColourScheme;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.model.Tag;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class SettingsDAO {

	private static final String ALWAYS = "ALWAYS";
	private static final String TRAIL_IMAGES_DOWNLOAD_DEFAULT = ALWAYS;
	private static final String DEFAULT_LARGE_IMAGES_DOWNLOAD_SETTING = ALWAYS;
	private static final String DEFAULT_PAGE_SIZE = "15";
	private static final String DEFAULT_COLOUR_SCHEME = "WHITE_ON_BLACK";
	private static final String DEFAULT_BASE_FONT_SIZE = "9";

	private static final String TAG = "SettingsDAO";
		
	private static final String APP_PACKAGE = "nz.gen.wellington.guardian.android";

	private static final String GUARDIAN_LITE_PROXY_API_PREFIX = "http://5.guardian-lite.appspot.com";
	private static final String CONTENT_API_URL = "http://content.guardianapis.com";
	
	private static List<String> API_USER_TIERS_WHICH_ALLOW_SHOW_MEDIA = Arrays.asList("partner", "internal");
	
	private static List<Tag> supportedContentTypes = Arrays.asList(ContentTags.articleContentType, ContentTags.galleryContentType);
	
	private PreferencesDAO preferencesDAO;
	private NetworkStatusService networkStatusService;
	
	private int clientVersion = 0;
	private Map<String, String> cache;
	private Context context;
	
	public SettingsDAO(Context context) {
		super();
		preferencesDAO = SingletonFactory.getPreferencesDAO(context);
		networkStatusService = SingletonFactory.getNetworkStatusService(context);
		setClientVersion(context);
		this.context = context;
		cache = new HashMap<String, String>();
	}

	public int getClientVersion() {
		return clientVersion;
	}
	
	public String getGuardianLiteProxyHost() {
		return GUARDIAN_LITE_PROXY_API_PREFIX;
	}
	
	public String getPreferedApiHost() {
		if (isUsingContentApi()) {
			return CONTENT_API_URL;
		}
		return GUARDIAN_LITE_PROXY_API_PREFIX;
	}
	
	public String getApiKey() {
		if (isUsingContentApi()) {
			return getPreferenceThroughCache("contentApiKey", null);
		}
		return null;
	}
	
	public int getBaseFontSize() {		
		return Integer.parseInt(getPreferenceThroughCache("baseFontSize", DEFAULT_BASE_FONT_SIZE));
	}
	
	public ColourScheme getColourScheme() {		
		final String colourSchemePreferences = getPreferenceThroughCache("colourScheme", DEFAULT_COLOUR_SCHEME);
		if (colourSchemePreferences.equals(DEFAULT_COLOUR_SCHEME)) {
			return new BlackOnWhiteColourScheme();
		}
		return new WhiteOnBlackColourScheme();		
	}
	
	public boolean isAlwaysDownloadLargePicturesSet() {
		return getPreferenceThroughCache("largeImagesOption", DEFAULT_LARGE_IMAGES_DOWNLOAD_SETTING).equals(ALWAYS);
	}

	public int getPageSizePreference() {
		return Integer.parseInt(getPreferenceThroughCache("pageSize", DEFAULT_PAGE_SIZE));
	}
	
	public String getSyncPreference() {
		return getPreferenceThroughCache("syncType", "NEVER");
	}

	public boolean isAlwaysDownloadTrailImagesSet() {
		return getPreferenceThroughCache("trailImagesOption", TRAIL_IMAGES_DOWNLOAD_DEFAULT).equals(ALWAYS);
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
	
	private String getPreferenceThroughCache(String key, String defaultValue) {
		if (cache.containsKey(key)) {
			return cache.get(key);
		}
		cache.put(key, preferencesDAO.getPreference(key, defaultValue));
		return cache.get(key);
	}
	
	private boolean isUsingContentApi() {
		return getPreferenceThroughCache("useContentApi", "false").equals("true");
	}
	
	public boolean isUsingExternalStorage() {
		return getPreferenceThroughCache("storageOption", "INTERNAL").equals("EXTERNAL");
	}

	public boolean shouldShowMedia() {
		return !this.isUsingContentApi() || isUsingApiKeyWithUserTierWhichSupportsShowMedia();
	}

	private boolean isUsingApiKeyWithUserTierWhichSupportsShowMedia() {
		if (!cache.containsKey("userTier")) {
			updateApiKeyUserTier();
		}
		return cache.get("userTier") != null && API_USER_TIERS_WHICH_ALLOW_SHOW_MEDIA.contains(cache.get("userTier"));		
	}

	// TODO this is a thread blocker
	private void updateApiKeyUserTier() {
		Log.i(TAG, "Updating api key");
		if (networkStatusService.isConnectionAvailable()) {			
			ContentSource contentApi = new ContentApiStyleApi(this.context, getClientVersion(), CONTENT_API_URL, getApiKey(), this.getSupportedContentTypes());			
			final String userTier = contentApi.getUserTierForKey();
			Log.i(TAG, "Key use tier is: " + userTier);
			cache.put("userTier",  userTier);
		}		
	}

}
