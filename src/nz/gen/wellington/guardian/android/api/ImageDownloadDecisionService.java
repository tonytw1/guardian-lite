package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import nz.gen.wellington.guardian.android.usersettings.PreferencesDAO;
import android.content.Context;

public class ImageDownloadDecisionService {
	
	private static final String ALWAYS = "ALWAYS";	// TODO can this be obtained from the preferences file
	
	private PreferencesDAO preferencesDAO;
	private NetworkStatusService networkStatusService;
	
	public ImageDownloadDecisionService(Context context) {
		this.preferencesDAO = SingletonFactory.getPreferencesDAO(context);
		this.networkStatusService = SingletonFactory.getNetworkStatusService(context);
	}

	public boolean isOkToDownloadTrailImages() {
		return preferencesDAO.getTrailPicturesPreference().equals(ALWAYS) || networkStatusService.isWifiConnection();
	}

	public boolean isOkToDownloadMainImages() {
		return preferencesDAO.getLargePicturesPreference().equals(ALWAYS) || networkStatusService.isWifiConnection();
	}
	
}
