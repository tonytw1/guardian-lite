package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.contentupdate.alarms.ContentUpdateAlarmSetter;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class preferences extends PreferenceActivity {
		
	private ContentUpdateAlarmSetter alarmSetter;
	private SectionDAO sectionDAO;
	public SettingsDAO settingsDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		ApiKeyPreferenceChangeListener apiKeyPreferenceChangeListener = new ApiKeyPreferenceChangeListener();
		
		findPreference("syncType").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);		
		findPreference("useContentApi").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);		
		findPreference("colourScheme").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("baseFontSize").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("showDateDefinements").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("pageSize").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("trailImagesOption").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("largeImagesOption").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		
		alarmSetter = new ContentUpdateAlarmSetter(this.getApplicationContext());
		sectionDAO = SingletonFactory.getSectionDAO(this.getApplicationContext());
		settingsDAO = SingletonFactory.getSettingsDAO(this.getApplicationContext());
	}
			
	class ApiKeyPreferenceChangeListener implements OnPreferenceChangeListener {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {			
			Log.i("PreferenceActivity", "Preference has been updated: " + preference.getKey());
			if (preference.getKey().equals("syncType")) {
				alarmSetter.setAlarmFor((String) newValue);
			}
			if (preference.getKey().equals("useContentApi")) {
				sectionDAO.evictSections();
			}
			
			settingsDAO.clearCache();
			return true;
		}
		
	}
	
}