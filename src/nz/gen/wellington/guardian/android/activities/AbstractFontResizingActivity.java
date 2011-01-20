package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public abstract class AbstractFontResizingActivity extends Activity implements FontResizingActivity {

	protected ColourScheme colourScheme;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferencesDAO preferencesDAO = SingletonFactory.getPreferencesDAO(this.getApplicationContext());
		colourScheme = preferencesDAO.getColourScheme();
	}

	@Override
	protected void onResume() {
		super.onResume();
		PreferencesDAO preferencesDAO = SingletonFactory.getPreferencesDAO(this.getApplicationContext());
		colourScheme = preferencesDAO.getColourScheme();		
	}
	
	@Override
	public void setFontSize(int baseSize) {
		setBackgroundColour();				
	}

	private void setBackgroundColour() {		
		View view =  findViewById(R.id.Main);
		if (view != null && colourScheme.getBackground() != null) {
			view.setBackgroundColor(colourScheme.getBackground());
		}
	}

}
