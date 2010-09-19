package nz.gen.wellington.guardian.android.contentupdate.alarms;

import java.util.Calendar;
import java.util.Date;

import nz.gen.wellington.guardian.android.activities.TimedSyncAlarmReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ContentUpdateAlarmSetter {
	
	private static final long ONE_DAY = 60000 * 60 * 24;
	private static final String TAG = "ContentUpdateAlarmSetter";
	
	private Context context;
	
	public ContentUpdateAlarmSetter(Context context) {
		this.context = context;
	}

	public void setContentUpdateAlarm() {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = makeContentUpdatePendingIntent();
		
		final long timeInMillis = getNextAutoSyncTime();
		Log.i(TAG, "Setting sync alarm for: " + new Date(timeInMillis).toLocaleString());
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, ONE_DAY, pi);
	}

	public void cancelAlarm() {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);		
		alarmManager.cancel(makeContentUpdatePendingIntent());		
	}
	
	private PendingIntent makeContentUpdatePendingIntent() {
		Intent i=new Intent(context, TimedSyncAlarmReceiver.class);
		PendingIntent pi= PendingIntent.getBroadcast(context, 0, i, 0);
		return pi;
	}
	
	private long getNextAutoSyncTime() {
		Calendar time = Calendar.getInstance();			
		boolean isToday = time.get(Calendar.HOUR_OF_DAY) < 6;
		time.set(Calendar.HOUR_OF_DAY, 6);
		time.set(Calendar.MINUTE, 0);
		long timeInMillis = time.getTimeInMillis();
		if (!isToday) {
			timeInMillis = timeInMillis + ONE_DAY;
		}
		return timeInMillis;
	}
	
}
