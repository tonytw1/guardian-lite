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

package nz.gen.wellington.guardian.android.activities.ui;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageStretchingService {

	private static final String TAG = "ImageStretchingService";

	public Bitmap stretchImageToFillView (Bitmap bitmap, int desiredWidth) {
		Log.d(TAG, "Asked to scale from " + bitmap.getWidth() + " to " + desiredWidth);
		final boolean isImageThinnerThanView = bitmap.getWidth() < desiredWidth;
		Log.d(TAG, "Image is thinner than view: " + isImageThinnerThanView + " (" + bitmap.getWidth() + " vs " + desiredWidth + ")");
		if (isImageThinnerThanView) {
			final boolean isImageLandScaped = bitmap.getWidth() > bitmap.getHeight();
			if (isImageLandScaped) {
				float aspectRatio = new Float(bitmap.getWidth()) / new Float(bitmap.getHeight());
				int scaledHeight = Math.round(desiredWidth / aspectRatio);
				Log.d(TAG, "Scaling to " + desiredWidth + "x" + scaledHeight);
				return Bitmap.createScaledBitmap(bitmap, desiredWidth, scaledHeight, true);
			}
		}
		return bitmap;
	}
	
}
