/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

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
