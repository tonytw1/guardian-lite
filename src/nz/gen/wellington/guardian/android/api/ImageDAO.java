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
