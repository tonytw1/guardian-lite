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

import java.util.Date;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.sync;
import nz.gen.wellington.guardian.android.contentupdate.ContentUpdateService;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.utils.DateTimeHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ContentUpdateAlarmReceiver extends BroadcastReceiver {
	
	private NetworkStatusService networkStatusService;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		networkStatusService = SingletonFactory.getNetworkStatusService(context);
		if (networkStatusService.isBackgroundDataAvailable()) {
			Intent serviceIntent = new Intent(context, ContentUpdateService.class);
			serviceIntent.setAction("RUN");
			sendNotification(context);
			context.startService(serviceIntent);
		}
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
