package nz.gen.wellington.guardian.android.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStatusService {
	
	private static final String TAG = "NetworkStatusService";
	private Context context;
	
	public NetworkStatusService(Context context) {
		this.context = context;
	}


	public boolean isConnectionAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			final boolean available = activeNetworkInfo.isAvailable();
			return available;
		}
		return false;
	}
	
	
	public boolean isWifiConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {
			return false;
		}		
		if (activeNetworkInfo.getTypeName().equals("WIFI")) {
			return true;
		}
		Log.i(TAG, "Active connection is of type: " + activeNetworkInfo.getTypeName());
		return false;
	}

}
