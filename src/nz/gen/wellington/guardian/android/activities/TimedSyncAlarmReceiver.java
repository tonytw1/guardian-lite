package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.services.ContentUpdateService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimedSyncAlarmReceiver extends BroadcastReceiver {

	ContentUpdateService contentUpdateService;
	
	@Override
	public void onReceive(Context context, Intent intent) {		
        Intent serviceIntent = new Intent(context, ContentUpdateService.class);
        serviceIntent.setAction("RUN");
        context.startService(serviceIntent);
	}
	
}
