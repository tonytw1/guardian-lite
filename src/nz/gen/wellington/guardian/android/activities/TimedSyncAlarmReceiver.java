package nz.gen.wellington.guardian.android.activities;

import java.util.Date;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.contentupdate.ContentUpdateService;
import nz.gen.wellington.guardian.android.dates.DateTimeHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimedSyncAlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "TimedSyncAlarmReceiver";

	ContentUpdateService contentUpdateService;
	
	@Override
	public void onReceive(Context context, Intent intent) {		
        Intent serviceIntent = new Intent(context, ContentUpdateService.class);
        serviceIntent.setAction("RUN");
        sendNotification(context);
        context.startService(serviceIntent);
	}
	
	private void sendNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = R.drawable.notification_icon;
		CharSequence tickerText = "Content update starting";			
		Date now = DateTimeHelper.now();
		
		Notification notification = new Notification(icon, tickerText, now.getTime());
		
		CharSequence contentTitle = "Content update starting";
		CharSequence contentText = "Autosync update started";
		
		Intent notificationIntent = new Intent(context, sync.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(ContentUpdateService.UPDATE_COMPLETE_NOTIFICATION_ID, notification);
	}
}
