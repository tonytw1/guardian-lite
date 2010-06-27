package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class perferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	
}