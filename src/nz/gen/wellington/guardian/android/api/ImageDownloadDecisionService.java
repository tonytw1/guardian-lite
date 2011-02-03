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
