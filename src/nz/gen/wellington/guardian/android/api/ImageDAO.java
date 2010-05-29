package nz.gen.wellington.guardian.android.api;

import java.util.HashMap;
import java.util.Map;

import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageDAO {

	private static final String TAG = "ImageDAO";
	
	private HttpFetcher httpFetcher;
	private Map<String, Bitmap> cache;
		
	public ImageDAO() {
		httpFetcher = new HttpFetcher();
		cache = new HashMap<String, Bitmap>();
	}

	public Bitmap getImage(String url) {
		return cache.get(url);
	}

	public boolean isAvailableLocally(String url) {
		return cache.containsKey(url);
	}

	public Bitmap fetchLiveImage(String url) {
		Log.i(TAG, "Fetching image: " + url);
		byte[] image = httpFetcher.httpFetchStream(url);
		Log.d(TAG, "Image file bytes: " + Integer.toString(image.length));
		Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		cache.put(url, bitmap);
		return bitmap;
	}
	
}
