package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageDAO {

	private static final String TAG = "ImageDAO";
		
	private HttpFetcher httpFetcher;
	
	public ImageDAO() {
		httpFetcher = new HttpFetcher();	
	}

	// TODO caching - any caching!
	public Bitmap getImage(String url) {
		return fetchLiveImage(url);
	}


	private Bitmap fetchLiveImage(String url) {
		byte[] image = httpFetcher.httpFetchStream(url);
		Log.d(TAG, "Image file bytes: " + Integer.toString(image.length));
		Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		return bitmap;
	}
	
}
