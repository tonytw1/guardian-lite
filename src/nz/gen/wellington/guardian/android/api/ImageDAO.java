package nz.gen.wellington.guardian.android.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
		httpFetcher = new HttpFetcher();
		this.context = context;
	}

	public Bitmap getImage(String url) {
		
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			File file = new File(context.getCacheDir() + "/" + getLocalFilename(url));
			Log.i(TAG, "Reading from disk: " + file.getAbsolutePath());
			fis = new FileInputStream(file);	// TODO null check
			in = new ObjectInputStream(fis);
			byte[] image = (byte[]) in.readObject();
			in.close();
			
			Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
			return bitmap;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public boolean isAvailableLocally(String url) {
		return isLocallyCached(url);
	}

	public Bitmap fetchLiveImage(String url) {
		Log.i(TAG, "Fetching image: " + url);
		byte[] image = httpFetcher.httpFetchStream(url);
		Log.d(TAG, "Image file bytes: " + Integer.toString(image.length));

		
		Log.i(TAG, "Writing image to disk: " + url);
		ObjectOutputStream out = null;
		try {
			File file = new File(context.getCacheDir() + "/" + getLocalFilename(url));
			Log.i(TAG, "Writing to disk: " + file.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(image);
			out.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
				
		Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		return bitmap;
	}
	
	
	
	private boolean isLocallyCached(String url) {
		File localFile = new File(context.getCacheDir(), getLocalFilename(url));
		Log.i(TAG, "Checking for local cache file at: " + localFile.getAbsolutePath());
		return localFile.exists() && localFile.canRead();
	}
	
		
	protected String getLocalFilename(String apiUrl) {
		return apiUrl.replaceAll("/", "").replaceAll(":", "");
	}
	
	
}
