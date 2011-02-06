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

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.api.ImageDownloadDecisionService;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Picture;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class picture extends AbstractFontResizingActivity {

	private ImageDownloadDecisionService imageDownloadDecisionService;
	private MainImageLoader mainImageLoader;
	private MainImageUpdateHandler mainImageUpdateHandler;

	private ImageDAO imageDAO;
	private Picture picture;
	private Bitmap image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.imageDownloadDecisionService = SingletonFactory.getImageDownloadDecisionService(this.getApplicationContext());
		this.imageDAO = SingletonFactory.getImageDao(this.getApplicationContext());
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.picture);

		this.picture = (Picture) this.getIntent().getExtras().get("picture");
		if (picture != null && (imageDAO.isAvailableLocally(picture.getFile()) || imageDownloadDecisionService.isOkToDownloadMainImages())) {
			populateMainImage();
			mainImageUpdateHandler = new MainImageUpdateHandler();
    	
			final String mainImageUrl = picture.getFile();
			if (mainImageUrl != null) {
				mainImageLoader = new MainImageLoader(imageDAO, picture.getFile());
				Thread loader = new Thread(mainImageLoader);
				loader.start();
			}
			
		} else {
        	Toast.makeText(this, "Could not load picture", Toast.LENGTH_SHORT).show();
		}
	}

	protected void populateMainImage() {
		ImageView imageView = (ImageView) findViewById(R.id.ArticleImage);
		imageView.setVisibility(View.VISIBLE);
		imageView.setScaleType(ScaleType.FIT_CENTER);
		imageView.setImageBitmap(image);		
		populateCaption(picture.getCaption());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setFontSize();	
	}
	
	
	@Override
	public void setFontSize() {
		super.setFontSize();
		TextView caption = (TextView) findViewById(R.id.Caption);
		caption.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize -2);		
		caption.setTextColor(colourScheme.getBodytext());
	}
	
	
	// TODO duplication
	private void populateCaption(String caption) {
		if (caption != null && !caption.trim().equals("")) {
			TextView captionView = (TextView) findViewById(R.id.Caption);
			captionView.setVisibility(View.VISIBLE);
			captionView.setText(caption);
		}
	}
	
	
	class MainImageLoader implements Runnable {		

		private ImageDAO imageDAO;
		private String mainImageUrl;
		
		public MainImageLoader(ImageDAO imageDAO, String mainImageUrl) {
			this.imageDAO = imageDAO;
			this.mainImageUrl = mainImageUrl;
		}
		
		@Override
		public void run() {
			image = imageDAO.getImage(mainImageUrl);
			if (image != null) {
				sendMainImageAvailableMessage(mainImageUrl);
			}
			return;
		}

		private void sendMainImageAvailableMessage(String mainImageUrl) {
			Message msg = new Message();
			msg.what = MainImageUpdateHandler.MAIN_IMAGE_AVAILABLE;
			mainImageUpdateHandler.sendMessage(msg);
		}		
	}
	
	class MainImageUpdateHandler extends Handler {
		
		private static final int MAIN_IMAGE_AVAILABLE = 1;

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {	   
			    case MAIN_IMAGE_AVAILABLE:
			    populateMainImage();
			}
		}
	}
	
}
