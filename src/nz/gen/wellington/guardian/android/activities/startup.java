package nz.gen.wellington.guardian.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class startup extends Activity {

	private static final String TAG = "startup";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Starting up");
		Intent intent = new Intent(this, main.class);
		this.finish();
		
		// TODO To remain in complicance with Guardian API Ts and Cs,
		// we should preform a cache files flush here, as well as on sync.
		
		this.startActivity(intent);
		Log.i(TAG, "Finished startup");
	}

}
