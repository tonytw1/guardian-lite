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

package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.colourscheme.ColourScheme;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Surface;
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
		int orientation = getWindowManager().getDefaultDisplay().getOrientation();
		return orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270;		
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
