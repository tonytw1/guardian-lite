package nz.gen.wellington.guardian.android.api.caching;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.util.Log;

public class FileService {
	
	private static final String TAG = "FileService";
	
	
	public static FileOutputStream getFileOutputStream(Context context, String url) throws FileNotFoundException {
		final String filepath = FileService.getLocalFilename(url);
		File file = new File(context.getCacheDir() + "/" + filepath);
		Log.i(TAG, "Opening output stream to: " + file.getAbsolutePath());
		return new FileOutputStream(file);
	}
		
	public static FileInputStream getFileInputStream(Context context, String url) throws FileNotFoundException {
		final String filepath = FileService.getLocalFilename(url);
		File file = new File(context.getCacheDir() + "/" + filepath);
		Log.i(TAG, "Opening input stream to: " + file.getAbsolutePath());
		return new FileInputStream(file);
	}

	public static boolean isLocallyCached(Context context, String apiUrl) {
		File localFile = new File(context.getCacheDir(), getLocalFilename(apiUrl));
		Log.i(TAG, "Checking for local cache file at: " + localFile.getAbsolutePath());
		return localFile.exists() && localFile.canRead();
	}
	
	public static String getLocalFilename(String url) {
		return url.replaceAll("/", "").replaceAll(":", "");
	}

	public static void clear(Context context, String apiUrl) {
		File localFile = new File(context.getCacheDir(), getLocalFilename(apiUrl));
		Log.i(TAG, "Clearing: " + localFile.getAbsolutePath());
		localFile.delete();
	}
	
}
