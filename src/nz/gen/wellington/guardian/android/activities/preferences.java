package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.contentupdate.alarms.ContentUpdateAlarmSetter;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class preferences extends PreferenceActivity {
		
	private ContentUpdateAlarmSetter alarmSetter;
	private SectionDAO sectionDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Preference autoSyncPref = (Preference) findPreference("syncType");
		autoSyncPref.setOnPreferenceChangeListener(new ApiKeyPreferenceChangeListener());
		
		Preference useContentApi = (Preference) findPreference("useContentApi");
		useContentApi.setOnPreferenceChangeListener(new ApiKeyPreferenceChangeListener());
		
		alarmSetter = new ContentUpdateAlarmSetter(this.getApplicationContext());
		sectionDAO = SingletonFactory.getSectionDAO(this.getApplicationContext());
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
			return true;
		}
		
	}
	
}