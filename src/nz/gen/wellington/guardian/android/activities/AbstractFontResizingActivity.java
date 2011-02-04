package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public abstract class AbstractFontResizingActivity extends Activity implements FontResizingActivity {

	protected ColourScheme colourScheme;
	protected int baseFontSize;
	protected SettingsDAO settingsDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settingsDAO = SingletonFactory.getSettingsDAO(this.getApplicationContext());
		populateSettings();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		populateSettings();
	}
	
	@Override
	public void setFontSize() {
		setBackgroundColour();
		setStatusColour();
	}
	
	
	protected boolean isLandScrapeOrientation() {
		return getWindowManager().getDefaultDisplay().getOrientation() == 1;	// TODO needs to deal with 180 and 270 degree orientations		
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

	private void populateSettings() {
		colourScheme = settingsDAO.getColourScheme();
		baseFontSize = settingsDAO.getBaseFontSize();
	}
	
}
