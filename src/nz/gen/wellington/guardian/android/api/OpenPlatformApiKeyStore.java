package nz.gen.wellington.guardian.android.api;

import android.util.Log;

public class OpenPlatformApiKeyStore {

	private static final String TAG = "OpenPlatformApiKeyStore";

	private String apiKey;
	
	public OpenPlatformApiKeyStore(String apiKey) {
		Log.i(TAG, "API key set to: " + apiKey);
		this.apiKey = apiKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		Log.i(TAG, "API key set to: " + apiKey);
		this.apiKey = apiKey;
	}
		
}
