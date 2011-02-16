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

package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.PictureClicker;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.ImageDownloadDecisionService;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Picture;
import nz.gen.wellington.guardian.model.MediaElement;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class gallery extends ContentRenderingActivity {
	
	private static final int THUMBNAILS_PER_ROW = 3;
	
	private GalleryImageUpdateHandler galleryImageUpdateHandler;
	private ImageDownloadDecisionService imageDownloadDecisionService;
	private TableRow currentRow;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		galleryImageUpdateHandler = new GalleryImageUpdateHandler();
		imageDownloadDecisionService = SingletonFactory.getImageDownloadDecisionService(this.getApplicationContext());
	}
	
	@Override
	protected int getLayout() {
		return R.layout.gallery;
	}
	
	
	@Override
	public void populateContent(Article article, int bodytextColour, int headlineColour) {
		super.populateContent(article, bodytextColour, headlineColour);
		if (!article.getMediaElements().isEmpty()) {			
			GalleryImageLoader galleryImageLoader = new GalleryImageLoader(imageDAO, article.getMediaElements());
			Thread loader = new Thread(galleryImageLoader);
			loader.start();
		}	
	}

		
	private void populateGalleryPicture(Picture picture) {
		ImageView imageView = new ImageView(this);
		Bitmap image = images.get(picture.getThumbnail());
		imageView.setImageBitmap(image);
		imageView.setPadding(5, 5, 5, 5);
		
		final boolean isFullImageAvailable = imageDAO.isAvailableLocally(picture.getFile()) || imageDownloadDecisionService.isOkToDownloadMainImages();
		if (isFullImageAvailable) {
			imageView.setOnClickListener(new PictureClicker(picture));
		}
		
		TableLayout thumbnails = (TableLayout) findViewById(R.id.GalleryThumbnails);
		if (currentRow == null || currentRow.getChildCount() >= THUMBNAILS_PER_ROW) {
			currentRow =  new TableRow(this.getApplicationContext());
			thumbnails.addView(currentRow);
		}		
		currentRow.addView(imageView);
	}
	
	
	class GalleryImageLoader implements Runnable {

		private static final String TAG = "GalleryImageLoader"
			;
		private ImageDAO imageDAO;
		private List<MediaElement> mediaElements;
		
		public GalleryImageLoader(ImageDAO imageDAO, List<MediaElement> mediaElements) {
			this.imageDAO = imageDAO;
			this.mediaElements = mediaElements;
		}
		
		@Override
		public void run() {
			Log.i(TAG, "Running gallery image loader with " + mediaElements + " media elements");
			for (MediaElement mediaElement : mediaElements) {
				
				if (mediaElement != null && mediaElement.isPicture() && mediaElement.getThumbnail() != null) {					
					Picture picture = new Picture(mediaElement.getThumbnail(), mediaElement.getFile(), mediaElement.getCaption());
					Bitmap image = imageDAO.getImage(picture.getThumbnail());
					if (image != null) {
						images.put(picture.getThumbnail(), image);
						sendThumbnailAvailableForPictureMessage(picture);
					}					
				}
			}
		}	

		private void sendThumbnailAvailableForPictureMessage(Picture picture) {
			Message msg = new Message();
			msg.what = GalleryImageUpdateHandler.GALLERY_IMAGE_AVAILABLE;
			
			Bundle bundle = new Bundle();
			bundle.putSerializable("picture", picture);			
			msg.setData(bundle);	
			galleryImageUpdateHandler.sendMessage(msg);
		}
		
	}
	
	
	class GalleryImageUpdateHandler extends Handler {
		
		public static final int GALLERY_IMAGE_AVAILABLE = 1;

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {	   
			    case GALLERY_IMAGE_AVAILABLE:			    	
			    final Picture picture = (Picture) msg.getData().get("picture");
			    populateGalleryPicture(picture);
			}
		}
	}
	
}
