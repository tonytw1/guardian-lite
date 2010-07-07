package nz.gen.wellington.guardian.android.api.caching;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import nz.gen.wellington.guardian.android.dates.DateTimeHelper;

import org.joda.time.DateTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class FileService {
	
	private static final String TAG = "FileService";

	public static final int INTERNAL_CACHE = 1;
	public static final int SDCARD = 2;
	public static final int EXTERNAL_SDCARD_SAMSUNG_I7500 = 3;
	private static final String VERSION_SUFFIX = "v2";
	
	
	public static FileOutputStream getFileOutputStream(Context context, String url) throws FileNotFoundException {
		final String filepath = FileService.getLocalFilename(url);
		File file = new File(getCacheDir(context) + "/" + filepath);
		Log.i(TAG, "Opening output stream to: " + file.getAbsolutePath());
		return new FileOutputStream(file);
	}
		
	public static FileInputStream getFileInputStream(Context context, String url) throws FileNotFoundException {
		final String filepath = FileService.getLocalFilename(url);
		File file = new File(getCacheDir(context) + "/" + filepath);
		//Log.i(TAG, "Opening input stream to: " + file.getAbsolutePath());
		return new FileInputStream(file);
	}

	public static boolean isLocallyCached(Context context, String apiUrl) {
		File localFile = new File(getCacheDir(context), getLocalFilename(apiUrl));
		//Log.i(TAG, "Checking for local cache file at: " + localFile.getAbsolutePath());
		return localFile.exists() && localFile.canRead();
	}
	
	public static DateTime getModificationTime(Context context, String apiUrl) {
		File localFile = new File(getCacheDir(context), getLocalFilename(apiUrl));
		//Log.i(TAG, "Checking mod time for file at: " + localFile.getAbsolutePath());
		if (localFile.exists()) {
			return calculateFileModTime(localFile);
		}
		return null;
	}
	
	public static void touchFile(Context context, String apiUrl) {
		File localFile = new File(getCacheDir(context), getLocalFilename(apiUrl));
		if (localFile.exists()) {
			Log.i(TAG, "Toching mod time for file at: " + localFile.getAbsolutePath());
			touchFileModTime(localFile);
		}
	}
	
	public static String getLocalFilename(String url) {
		return url.replaceAll("/", "").replaceAll(":", "") + VERSION_SUFFIX;
	}

	public static void clear(Context context, String apiUrl) {
		File localFile = new File(getCacheDir(context), getLocalFilename(apiUrl));
		Log.i(TAG, "Clearing: " + localFile.getAbsolutePath());
		localFile.delete();
	}

	
	public static void clearAll(Context context) {		
		Log.i(TAG, "Clearing all cache files");				
		FileFilter allFilesFilter = new FileFilter() {				
			@Override
			public boolean accept(File file) {
				return true;
			}
		};
		deleteFiles(context, allFilesFilter);
	}
	
	
	public static void clearAllArticleSets(Context context) {		
		Log.i(TAG, "Clearing all article set cache files");
				
		FileFilter jsonFilesFilter = new FileFilter() {				
			@Override
			public boolean accept(File file) {
				return file.getPath().endsWith("json") && !file.getPath().endsWith("sections.json");
			}
		};		
		deleteFiles(context, jsonFilesFilter);
	}

	
	public static void clearExpiredCacheFiles(Context context) {
		Log.i(TAG, "Clearing all article set cache files more than 24 hours old");
		FileFilter jsonFilesFilter = new FileFilter() {				
			@Override
			public boolean accept(File file) {
				return calculateFileModTime(file).isBefore(DateTimeHelper.now().minusDays(1));
			}
		};
		deleteFiles(context, jsonFilesFilter);
	}
	
	
	
	private static void deleteFiles(Context context, FileFilter jsonFilesFilter) {		
		File cacheDir = getCacheDir(context);
		if (cacheDir == null) {
			Log.i(TAG, "No cache folder found");
			return;
		}
		Log.i(TAG, "Cache dir path is: " + cacheDir.getPath());
		Log.i(TAG, "Cache dir absolute path is: " + cacheDir.getAbsolutePath());
				
		
		File[] listFiles = cacheDir.listFiles(jsonFilesFilter);
		Log.i(TAG, "Cache dir file count: " + listFiles.length);
		for (int i = 0; i < listFiles.length; i++) {
			File cacheFile = listFiles[i];
			Log.i(TAG, "Found cache file: " + cacheFile.getAbsolutePath());
			if (cacheFile.delete()) {
				Log.i(TAG, "Deleted cache file: " + cacheFile.getAbsolutePath());				
			}		
		}
	}
	
	
	protected static File getCacheDir(Context context) {				
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
		final int cacheLocation = Integer.parseInt(prefs.getString("cacheLocation", Integer.toString(INTERNAL_CACHE)));		
		switch (cacheLocation) {
		case INTERNAL_CACHE:
			return context.getCacheDir();
		case SDCARD:
			return getExternalSDCardCacheFolder("/guardian-lite/");
		case EXTERNAL_SDCARD_SAMSUNG_I7500:
			return getExternalSDCardCacheFolder("/sd/guardian-lite/");

		default:
			return context.getCacheDir();
		}
	}
	
	
	private static DateTime calculateFileModTime(File localFile) {
		DateTime modTime = new DateTime(localFile.lastModified());
		Log.i(TAG, "Mod time is: " + modTime.toString() + " for: " + localFile.getAbsolutePath());
		return modTime;
	}
	
	
	private static void touchFileModTime(File localFile) {
		DateTime modTime = DateTimeHelper.now();
		localFile.setLastModified(modTime.toDate().getTime());
	}
	
	private static File getExternalSDCardCacheFolder(String folderPath) {
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + folderPath);
		if ( folder.exists()) {
			return folder;			
		} else if (folder.mkdir()) {
			return folder;
		}
		return null;
	}
	
}
