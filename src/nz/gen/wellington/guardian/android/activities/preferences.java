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
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.contentupdate.alarms.ContentUpdateAlarmSetter;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class preferences extends PreferenceActivity {
		
	private ContentUpdateAlarmSetter alarmSetter;
	private SectionDAO sectionDAO;
	public SettingsDAO settingsDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		ApiKeyPreferenceChangeListener apiKeyPreferenceChangeListener = new ApiKeyPreferenceChangeListener();
		
		findPreference("syncType").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);		
		findPreference("useContentApi").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);		
		findPreference("colourScheme").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("baseFontSize").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("pageSize").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("trailImagesOption").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		findPreference("largeImagesOption").setOnPreferenceChangeListener(apiKeyPreferenceChangeListener);
		
		alarmSetter = new ContentUpdateAlarmSetter(this.getApplicationContext());
		sectionDAO = SingletonFactory.getSectionDAO(this.getApplicationContext());
		settingsDAO = SingletonFactory.getSettingsDAO(this.getApplicationContext());
	}
			
	class ApiKeyPreferenceChangeListener implements OnPreferenceChangeListener {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {			
			Log.i("PreferenceActivity", "Preference has been updated: " + preference.getKey());
			if (preference.getKey().equals("syncType")) {
				alarmSetter.setAlarmFor((String) newValue);
			}
			if (preference.getKey().equals("useContentApi")) {
				sectionDAO.evictSections();
			}
			
			settingsDAO.clearCache();
			return true;
		}
		
	}
	
}