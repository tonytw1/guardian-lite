package nz.gen.wellington.guardian.android.activities;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.MediaElement;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class gallery extends ContentRenderingActivity {
	
	private static final String TAG = "gallery";

	private GalleryImageUpdateHandler galleryImageUpdateHandler;
    private ImageAdapter imageAdapter;
	private GridView thumbnails;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	galleryImageUpdateHandler = new GalleryImageUpdateHandler();
	}

	
	@Override
	protected int getLayout() {
		return R.layout.gallery;
	}
	
	
	@Override
	public void populateContent(Article article, int bodytextColour, int headlineColour) {
		super.populateContent(article, bodytextColour, headlineColour);
		
    	thumbnails = (GridView) findViewById(R.id.GalleryThumbnails);
		imageAdapter = new ImageAdapter();
		thumbnails.setAdapter(imageAdapter);
		
		if (!article.getMediaElements().isEmpty()) {			
			GalleryImageLoader galleryImageLoader = new GalleryImageLoader(imageDAO, article.getMediaElements());
			Thread loader = new Thread(galleryImageLoader);
			loader.start();
		}	
	}

		
	private void populateGalleryImage(String imageUrl) {
		ImageView imageView = new ImageView(this.getApplicationContext());
		Bitmap image = images.get(imageUrl);
		imageView.setImageBitmap(image);
		imageView.setPadding(5, 5, 5, 5);
		
		Log.d(TAG, "Adding view to gridview");
		imageAdapter.add(imageView);
		thumbnails.invalidateViews();
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
					final String imageUrl = mediaElement.getThumbnail();
					Bitmap image = imageDAO.getImage(imageUrl);
					if (image != null) {
						images.put(imageUrl, image);
						sendGalleryImageAvailableMessage(imageUrl);
					}					
				}
			}
		}	

		private void sendGalleryImageAvailableMessage(String imageUrl) {
			Message msg = new Message();
			msg.what = GalleryImageUpdateHandler.GALLERY_IMAGE_AVAILABLE;
			msg.getData().putString("imageUrl", imageUrl);
			galleryImageUpdateHandler.sendMessage(msg);
		}
		
	}
	
	
	class GalleryImageUpdateHandler extends Handler {
		
		public static final int GALLERY_IMAGE_AVAILABLE = 1;

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {	   
			    case GALLERY_IMAGE_AVAILABLE:
			    final String mainImageUrl = msg.getData().getString("imageUrl");
			    populateGalleryImage(mainImageUrl);
			}
		}
	}
	
	
	
	public class ImageAdapter extends BaseAdapter {

		private List<View> views;

		public ImageAdapter() {
			views = new ArrayList<View>();
		}

		public int getCount() {
			return views.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {  
				return views.get(position);
			} else {
				convertView = views.get(position);
				return convertView;
			}
		}

		public void add(View view) {
			views.add(view);
		}
	}
}
