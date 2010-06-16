package nz.gen.wellington.guardian.android.api;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nz.gen.wellington.guardian.android.api.caching.FileService;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageDAO {

	private static final String TAG = "ImageDAO";
	
	private HttpFetcher httpFetcher;
	private Context context;
		
	public ImageDAO(Context context) {
		httpFetcher = new HttpFetcher(context);
		this.context = context;
	}

	public boolean isAvailableLocally(String url) {
		return FileService.isLocallyCached(context, url);
	}
	
	
	public Bitmap getImage(String url) {
		Log.i(TAG, "Reading from disk: " + url);
		try {
			FileInputStream fis = FileService.getFileInputStream(context, url);
			ObjectInputStream in = new ObjectInputStream(fis);
			byte[] image = (byte[]) in.readObject();
			in.close();
			
			return decodeImage(image);
			
		} catch (IOException ex) {
			Log.e(TAG, "IO Exception while writing article set: " + url + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			Log.e(TAG, "Exception while writing article set: " + url + ex.getMessage());
		}
		return null;
	}


	public Bitmap fetchLiveImage(String url) {
		Log.i(TAG, "Fetching image: " + url);
		byte[] image = httpFetcher.httpFetchStream(url);
		Log.d(TAG, "Image file bytes: " + Integer.toString(image.length));

		
		Log.i(TAG, "Writing image to disk: " + url);
		ObjectOutputStream out = null;
		try {		
			FileOutputStream fos = FileService.getFileOutputStream(context, url);
			out = new ObjectOutputStream(fos);
			out.writeObject(image);
			out.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
				
		return decodeImage(image);
	}

	
	private Bitmap decodeImage(byte[] image) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		return bitmap;
	}
	
}
