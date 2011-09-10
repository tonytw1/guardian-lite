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

package nz.gen.wellington.guardian.android.api.caching;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.utils.DateTimeHelper;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileService {
	
	private static final String TAG = "FileService";

	public static final int INTERNAL_CACHE = 1;
	public static final int SDCARD = 2;
	public static final int EXTERNAL_SDCARD_SAMSUNG_I7500 = 3;
		
	static FileOutputStream getFileOutputStream(Context context, String filename) throws FileNotFoundException {
		Log.i(TAG, "Opening output stream to: " + filename);
		File file = new File(getCacheDir(context) + "/" + filename);
		return new FileOutputStream(file);
	}
		
	static FileInputStream getFileInputStream(Context context, String filename) throws FileNotFoundException {
		File file = new File(getCacheDir(context) + "/" + filename);
		Log.i(TAG, "Opening input stream to: " + file.getAbsolutePath());
		return new FileInputStream(file);
	}

	static boolean existsLocally(Context context, String filename) {
		File localFile = new File(getCacheDir(context), filename);
		boolean result = localFile.exists() && localFile.canRead();
		Log.i(TAG, "Checking for local cache file at '" + localFile.getAbsolutePath() + "': " + result);
		return result;
	}
	
	static Date getModificationTime(Context context, String filename) {
		Log.i(TAG, "Checking mod time for file at: " + filename);
		File localFile = new File(getCacheDir(context), filename);
		if (localFile.exists()) {
			return calculateFileModTime(localFile);
		}
		return null;
	}
	
	static void touchFile(Context context, String filename, Date modTime) {
		File localFile = new File(getCacheDir(context), filename);
		if (localFile.exists()) {
			Log.i(TAG, "Touching mod time for file at: " + localFile.getAbsolutePath());
			touchFileModTime(localFile, modTime);
		}
	}
	
	static void clear(Context context, String filename) {
		File localFile = new File(getCacheDir(context), filename);
		Log.i(TAG, "Clearing: " + localFile.getAbsolutePath());
		localFile.delete();
	}
	
	static void clearAllArticleSets(Context context) {				
		FileFilter jsonFilesFilter = new FileFilter() {		
			@Override
			public boolean accept(File file) {
				return file.getPath().endsWith("json") && !file.getPath().endsWith("sections.json");
			}
		};		
		deleteFiles(context, jsonFilesFilter);
	}

	// TODO is this in the right class - should be in a cache?
	static void deleteFilesOlderThanTwentyFourHours(Context context) {
		Log.i(TAG, "Clearing all article set cache files more than 24 hours old");
		FileFilter jsonFilesFilter = new FileFilter() {				
			@Override
			public boolean accept(File file) {
				return calculateFileModTime(file).before(DateTimeHelper.yesterday());
			}
		};
		deleteFiles(context, jsonFilesFilter);
	}
	
	private static void deleteFiles(Context context, FileFilter fileFilter) {		
		File cacheDir = getCacheDir(context);
		if (cacheDir == null) {
			Log.w(TAG, "No cache folder found");
			return;
		}
		
		File[] listFiles = cacheDir.listFiles(fileFilter);
		if (listFiles == null) {
			Log.w(TAG, "listFiles was null - indicates cache dir is a file not a folder?");
			return;
		}
		
		for (int i = 0; i < listFiles.length; i++) {
			File cacheFile = listFiles[i];
			if (!cacheFile.delete()) {
				Log.w(TAG, "Failed to clear cache file: " + cacheFile.getName());
			}
		}
	}
		
	private static File getCacheDir(Context context) {
		if (SingletonFactory.getSettingsDAO(context).isUsingExternalStorage()) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {	    	
				File externalCacheFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/guardian-lite");
				if (!externalCacheFolder.exists()) {
					Log.i(TAG, "Creating external cache folder");
					if (!externalCacheFolder.mkdir()) {
						Log.e(TAG, "Failed to create external cache folder");
						return null;
					}
				}
				return externalCacheFolder;
			}
			return null;
		}
		
		return context.getCacheDir();
	}
		
	private static Date calculateFileModTime(File localFile) {
		Date modTime = new Date(localFile.lastModified());
		return modTime;
	}
		
	private static void touchFileModTime(File localFile, Date modTime) {
		localFile.setLastModified(modTime.getTime());
	}
	
}
