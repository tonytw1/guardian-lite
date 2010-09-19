package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.contentupdate.alarms.ContentUpdateAlarmSetter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class perferences extends PreferenceActivity {
		
	private ContentUpdateAlarmSetter alarmSetter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Preference autoSyncPref = (Preference) findPreference("syncType");
		autoSyncPref.setOnPreferenceChangeListener(new ApiKeyPreferenceChangeListener());
		alarmSetter = new ContentUpdateAlarmSetter(this.getApplicationContext());
	}
	
		
	class ApiKeyPreferenceChangeListener implements OnPreferenceChangeListener {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {			
			Log.i("PreferenceActivity", "Preference has been updated: " + preference.getKey());
			if (preference.getKey().equals("syncType")) {				
				String syncType = (String) newValue;
				if (syncType.equals("DAILY")) {
					alarmSetter.setDailyContentUpdateAlarm();					
				} else if (syncType.equals("HOURLY")) {
					alarmSetter.setHourlyContentUpdateAlarm();					
				} else {
					alarmSetter.cancelAlarm();
				}
			}
			return true;
		}
		
	}
	
}