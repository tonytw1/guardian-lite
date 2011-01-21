package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public abstract class AbstractFontResizingActivity extends Activity implements FontResizingActivity {

	protected ColourScheme colourScheme;
	protected int baseFontSize;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		populatePreferences();
	}


	@Override
	protected void onResume() {
		super.onResume();
		populatePreferences();
	}
	
	@Override
	public void setFontSize(int baseSize) {
		setBackgroundColour();
		setStatusColour();
	}

	private void setStatusColour() {
		TextView view = (TextView) findViewById(R.id.DownloadProgress);
		if (view != null) {
	        view.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize - 1);
			if (colourScheme.getStatus() != null) {
				view.setTextColor(colourScheme.getStatus());
			}
		}
	}

	private void setBackgroundColour() {		
		View view =  findViewById(R.id.Main);
		if (view != null && colourScheme.getBackground() != null) {
			view.setBackgroundColor(colourScheme.getBackground());
		}
	}

	private void populatePreferences() {
		PreferencesDAO preferencesDAO = SingletonFactory.getPreferencesDAO(this.getApplicationContext());
		colourScheme = preferencesDAO.getColourScheme();
		baseFontSize = preferencesDAO.getBaseFontSize();
	}
	
}
