package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.caching.FileBasedImageCache;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageDAO {

	private static final String TAG = "ImageDAO";
	
	private HttpFetcher activeHttpFetcher;
	private FileBasedImageCache imageCache;
	private Context context;
	
	public ImageDAO(Context context) {
		this.imageCache = new FileBasedImageCache(context);
	}

	public boolean isAvailableLocally(String url) {
		return imageCache.isAvailableLocally(url);
	}
		
	public Bitmap getImage(String url) {
		Log.d(TAG, "Getting image: " + url);
		if (isAvailableLocally(url)) {						
			byte[] cachedImage = imageCache.getCachedImage(url);
			if (cachedImage != null) {
				return decodeImage(cachedImage);
			}
		}
		return fetchLiveImage(url);
	}
	
	public void stopLoading() {
		if (activeHttpFetcher != null) {
			activeHttpFetcher.stopLoading();
		}
	}
	
	private synchronized Bitmap fetchLiveImage(String url) {
		activeHttpFetcher = new HttpFetcher(context);
		byte[] image = activeHttpFetcher.httpFetchStream(url);
		activeHttpFetcher = null;
		
		if (image == null) {
			Log.i(TAG, "Could not fetch image: " + url);
			return null;
		}

		Log.i(TAG, "Writing image to disk: " + url);
		imageCache.saveImageToFile(url, image);
		return decodeImage(image);
	}
	
	private Bitmap decodeImage(byte[] image) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		return bitmap;
	}
	
}
