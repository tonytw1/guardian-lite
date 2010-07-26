package nz.gen.wellington.guardian.android.activities;

import java.util.Calendar;

import nz.gen.wellington.guardian.android.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class perferences extends PreferenceActivity {
	
	
	private static final int TIMED_DOWNLOAD_REPEAT_INTERVAL = 60000 * 60 * 24;
	private static final String TAG = "perferences";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		Preference autoSyncPref = (Preference) findPreference("autoSync");
		autoSyncPref.setOnPreferenceChangeListener(new ApiKeyPreferenceChangeListener());
	}
	
		
	class ApiKeyPreferenceChangeListener implements OnPreferenceChangeListener {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {			
			Log.i("PreferenceActivity", "Preference has been updated: " + preference.getKey());
			if (preference.getKey().equals("autoSync")) {
				
				boolean autoSyncEnabled = (Boolean) newValue;
				if (autoSyncEnabled) {
					Calendar time = Calendar.getInstance();
					boolean isToday = time.get(Calendar.HOUR_OF_DAY) < 6;					
					time.set(Calendar.HOUR_OF_DAY, 6);
					time.set(Calendar.MINUTE, 0);
					long timeInMillis = time.getTimeInMillis() + (60 * 1000);
					if (!isToday) {
						timeInMillis = timeInMillis + TIMED_DOWNLOAD_REPEAT_INTERVAL;
					}
					setAlarm(timeInMillis);		
				} else {
					cancelAlarm();
				}
			}			
			return true;
		}
		
	}

	
	private void setAlarm(long timeInMillis) {
		Log.i(TAG, "Setting sync alarm for: " + timeInMillis);
		AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = makePendingIntent();		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, TIMED_DOWNLOAD_REPEAT_INTERVAL, pi);
	}


	private PendingIntent makePendingIntent() {
		Intent i=new Intent(this.getApplicationContext(), TimedSyncAlarmReceiver.class);
		PendingIntent pi= PendingIntent.getBroadcast(this.getApplicationContext(), 0, i, 0);
		return pi;
	}
	
	
	private void cancelAlarm() {
		AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);		
		Log.i("SYNC", "Clearing alarm");
		alarmManager.cancel(makePendingIntent());		
	}
	
}