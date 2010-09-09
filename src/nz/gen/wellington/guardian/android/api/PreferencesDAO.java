package nz.gen.wellington.guardian.android.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesDAO {

	private Context context;

	public PreferencesDAO(Context context) {
		this.context = context;
	}

	public int getPageSizePreference() {
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
		final String pageSizeString = prefs.getString("pageSize", "10");
		int pageSize = Integer.parseInt(pageSizeString);
		return pageSize;
	}

}
