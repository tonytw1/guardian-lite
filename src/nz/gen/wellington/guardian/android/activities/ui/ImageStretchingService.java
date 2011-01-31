package nz.gen.wellington.guardian.android.activities.ui;

import android.graphics.Bitmap;
import android.view.View;

public class ImageStretchingService {

	public Bitmap stretchImageToFillView (Bitmap bitmap, View imageView) {
		final boolean isImageThinnerThanView = bitmap.getWidth() < imageView.getWidth();
		if (isImageThinnerThanView) {
			final boolean isImageLandScaped = bitmap.getWidth() > bitmap.getHeight();
			if (isImageLandScaped) {
				int scaledWidth = imageView.getWidth();
				float aspectRatio = new Float(bitmap.getWidth()) / new Float(bitmap.getHeight());
				int scaledHeight = Math.round(scaledWidth / aspectRatio);
				return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
			}
		}
		return bitmap;
	}
	
}
