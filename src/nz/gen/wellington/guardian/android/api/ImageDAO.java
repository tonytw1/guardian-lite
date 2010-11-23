package nz.gen.wellington.guardian.android.api;

import nz.gen.wellington.guardian.android.api.caching.FileBasedImageCache;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageDAO {

	//private static final String TAG = "ImageDAO";
	
	private HttpFetcher httpFetcher;
	private FileBasedImageCache imageCache;
		
	public ImageDAO(Context context) {
		this.imageCache = new FileBasedImageCache(context);
		httpFetcher = new HttpFetcher(context);
	}

	public boolean isAvailableLocally(String url) {
		return imageCache.isAvailableLocally(url);
	}
		
	public Bitmap getImage(String url) {
		if (isAvailableLocally(url)) {						
			byte[] cachedImage = imageCache.getCachedImage(url);
			if (cachedImage != null) {
				return decodeImage(cachedImage);
			}
		}
		return null;
	}
	
	// TODO this method needs to fall in behind getImage.
	// There is probably caching code in the frontend classes which can be pulled into this DAO.
	public Bitmap fetchLiveImage(String url) {
		byte[] image = httpFetcher.httpFetchStream(url);		
		if (image == null) {
			//Log.i(TAG, "Could not fetch image: " + url);
			return null;
		}

		//Log.i(TAG, "Writing image to disk: " + url);
		imageCache.saveImageToFile(url, image);				
		return decodeImage(image);
	}
		
	private Bitmap decodeImage(byte[] image) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		return bitmap;
	}
	
}
