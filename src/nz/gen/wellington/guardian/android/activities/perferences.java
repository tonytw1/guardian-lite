package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

public class perferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Preference customPref = (Preference) findPreference("apikey");
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences customSharedPreference = getSharedPreferences("GuardianPrefsFile", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = customSharedPreference.edit();
				editor.putString("apikey", "The preference has been clicked");
				editor.commit();
				return true;
			}
		});
	}
}
