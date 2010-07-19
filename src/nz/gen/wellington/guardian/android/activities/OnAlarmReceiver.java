package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.services.ContentUpdateService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnAlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "OnAlarmReceiver";

	ContentUpdateService contentUpdateService;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Recevied wakeup");
		
        Intent serviceIntent = new Intent(context, ContentUpdateService.class);
        serviceIntent.setAction("RUN");
        context.startService(serviceIntent);
	}
	
}
