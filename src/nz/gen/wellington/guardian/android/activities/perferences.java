package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class perferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Preference customPref = (Preference) findPreference("apikey");
		customPref.setOnPreferenceClickListener(new ApiKeyPreferenceClickListener());
		customPref.setOnPreferenceChangeListener(new ApiKeyPreferenceChangeListener());
	}
	
	
	class ApiKeyPreferenceClickListener implements OnPreferenceClickListener {		
		public boolean onPreferenceClick(Preference preference) {
			SharedPreferences customSharedPreferences = getSharedPreferences("GuardianPrefsFile", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = customSharedPreferences.edit();
			editor.putString("apikey", "The preference has been clicked");
			editor.commit();
			return true;
		}
	}
	
	
	class ApiKeyPreferenceChangeListener implements OnPreferenceChangeListener {
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			Log.i("PreferenceActivity", "Preference has been updated: " + preference.getKey());
			if (preference.getKey().equals("apikey")) {
				final String newApiKey = (String) newValue;
				ArticleDAOFactory.getOpenPlatformApiKeyStore(preference.getContext().getApplicationContext()).setApiKey(newApiKey);		// TODO is this still required?						
			}
			return true;
		}
		
	}
	
	
}