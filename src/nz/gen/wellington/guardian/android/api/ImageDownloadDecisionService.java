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

package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.SettingsDAO;
import android.content.Context;

public class ImageDownloadDecisionService {
	
	private static final String ALWAYS = "ALWAYS";	// TODO can this be obtained from the preferences file
	
	private SettingsDAO settingsDAO;
	private NetworkStatusService networkStatusService;

	
	public ImageDownloadDecisionService(Context context) {
		this.settingsDAO = SingletonFactory.getSettingsDAO(context);
		this.networkStatusService = SingletonFactory.getNetworkStatusService(context);
	}

	public boolean isOkToDownloadTrailImages() {
		return settingsDAO.getTrailPicturesPreference().equals(ALWAYS) || networkStatusService.isWifiConnection();
	}

	public boolean isOkToDownloadMainImages() {
		return settingsDAO.getLargePicturesPreference().equals(ALWAYS) || networkStatusService.isWifiConnection();
	}
	
}
