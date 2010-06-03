package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.openplatfrom.OpenPlatformJSONApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ApiFactory {
	
	public static ContentSource getOpenPlatformApi(Context context) {
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
		final String apiKey = prefs.getString("apikey", null);
		return new OpenPlatformJSONApi(context, apiKey);
	}
		
}
