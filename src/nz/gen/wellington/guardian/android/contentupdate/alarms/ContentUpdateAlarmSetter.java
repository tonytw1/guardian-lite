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

package nz.gen.wellington.guardian.android.contentupdate.alarms;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ContentUpdateAlarmSetter {
	

	private static final String TAG = "ContentUpdateAlarmSetter";
	
	private static final long ONE_MINUTE = 60000;
	private static final long ONE_HOUR = ONE_MINUTE * 60;
	private static final long ONE_DAY = ONE_HOUR * 24;
	
	private static final long SIX_AM_SYNC_SPREAD = ONE_MINUTE * 10;
	
	private Context context;
	
	public ContentUpdateAlarmSetter(Context context) {
		this.context = context;
	}
		
	public void setAlarmFor(String syncType) {
		Log.i(TAG, "Setting sync alarm for preference: " + syncType);
		if (syncType.equals("DAILY")) {
			setDailyContentUpdateAlarm();					
		} else if (syncType.equals("HOURLY")) {
			setHourlyContentUpdateAlarm();					
		} else {
			cancelAlarm();
		}
	}
	
	private void setDailyContentUpdateAlarm() {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = makeContentUpdatePendingIntent();
		
		final long timeInMillis = getNextDailyAutoSyncTime();
		Log.i(TAG, "Setting daily sync alarm for: " + new Date(timeInMillis).toLocaleString());
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, ONE_DAY, pi);
	}
	
	private void setHourlyContentUpdateAlarm() {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = makeContentUpdatePendingIntent();
		
		final long timeInMillis = getNextHourlyAutoSyncTime();
		Log.i(TAG, "Setting hourly sync alarm for: " + new Date(timeInMillis).toLocaleString());
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, ONE_HOUR, pi);
	}
	
	private void cancelAlarm() {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);		
		alarmManager.cancel(makeContentUpdatePendingIntent());		
	}
	
	private PendingIntent makeContentUpdatePendingIntent() {
		Intent i=new Intent(context, ContentUpdateAlarmReceiver.class);
		PendingIntent pi= PendingIntent.getBroadcast(context, 0, i, 0);
		return pi;
	}
	
	private long getNextHourlyAutoSyncTime() {
		Calendar time = Calendar.getInstance();					
		long timeInMillis = time.getTimeInMillis() + ONE_HOUR;		
		return timeInMillis;
	}
	
	private long getNextDailyAutoSyncTime() {
		Calendar time = Calendar.getInstance();			
		boolean isToday = time.get(Calendar.HOUR_OF_DAY) < 6;
		time.set(Calendar.HOUR_OF_DAY, 6);
		time.set(Calendar.MINUTE, 0);
		long timeInMillis = time.getTimeInMillis();
		if (!isToday) {
			timeInMillis = timeInMillis + ONE_DAY;
		}
		
		timeInMillis = timeInMillis - generateRandomOffsetToSpendLoad();		
		return timeInMillis;
	}
	
	private long generateRandomOffsetToSpendLoad() {
		return new Double(Math.random() * SIX_AM_SYNC_SPREAD).longValue();
	}
	
}