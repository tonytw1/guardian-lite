package nz.gen.wellington.guardian.android.api.caching;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

public class FileBasedImageCache {

	private Context context;

	public FileBasedImageCache(Context context) {
		this.context = context;
	}

	public boolean isAvailableLocally(String url) {
		return FileService.existsLocally(context, FileCacheLocalFilenameService.getLocalFilenameFor(url));
	}
	
	public byte[] getCachedImage(String url) {
		try {
			InputStream fis = FileService.getFileInputStream(context, FileCacheLocalFilenameService.getLocalFilenameFor(url));
			ObjectInputStream in = new ObjectInputStream(fis);
			byte[] image = (byte[]) in.readObject();
			in.close();
			return image;
			
		} catch (IOException ex) {
			//Log.e(TAG, "IO Exception while writing article set: " + url + ex.getMessage());
		} catch (ClassNotFoundException ex) {
			//Log.e(TAG, "Exception while writing article set: " + url + ex.getMessage());
		}
		return null;
	}
	
	public void saveImageToFile(String url, byte[] image) {
		ObjectOutputStream out = null;
		try {		
			FileOutputStream fos = FileService.getFileOutputStream(context, FileCacheLocalFilenameService.getLocalFilenameFor(url));
			out = new ObjectOutputStream(fos);
			out.writeObject(image);
			out.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
