package nz.gen.wellington.guardian.android.application;

import nz.gen.wellington.guardian.android.contentupdate.alarms.ContentUpdateAlarmSetter;
import nz.gen.wellington.guardian.android.contentupdate.tasks.ContentUpdateTaskRunnable;
import nz.gen.wellington.guardian.android.contentupdate.tasks.PurgeExpiredContentTask;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.app.Application;
import android.util.Log;

public class GuardianLite extends Application {

	private static final String TAG = "GuardianLite";

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i(TAG, "Reseting sync alarm");
		ContentUpdateAlarmSetter alarmSetter = new ContentUpdateAlarmSetter(this.getApplicationContext());
		SettingsDAO settingsDAO = new SettingsDAO(this.getApplicationContext());		
		alarmSetter.setAlarmFor(settingsDAO.getSyncPreference());
	
		Log.i(TAG, "Purging expired cache files");
		ContentUpdateTaskRunnable purge = new PurgeExpiredContentTask(this.getApplicationContext());
		purge.run();
	}
	
}
