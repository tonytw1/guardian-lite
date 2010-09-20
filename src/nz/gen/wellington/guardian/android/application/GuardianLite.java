package nz.gen.wellington.guardian.android.application;

import nz.gen.wellington.guardian.android.contentupdate.alarms.ContentUpdateAlarmSetter;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.app.Application;

public class GuardianLite extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ContentUpdateAlarmSetter alarmSetter = new ContentUpdateAlarmSetter(this.getApplicationContext());
		PreferencesDAO preferencesDAO = new PreferencesDAO(this.getApplicationContext());		
		alarmSetter.setAlarmFor(preferencesDAO.getSyncPreference());
	}
	
}
