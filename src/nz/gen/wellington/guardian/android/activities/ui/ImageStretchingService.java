package nz.gen.wellington.guardian.android.activities.ui;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageStretchingService {

	private static final String TAG = "ImageStretchingService";

	public Bitmap stretchImageToFillView (Bitmap bitmap, int desiredWidth) {	
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
