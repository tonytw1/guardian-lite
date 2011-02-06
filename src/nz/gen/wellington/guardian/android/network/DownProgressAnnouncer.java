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

package nz.gen.wellington.guardian.android.network;

import android.content.Context;
import android.content.Intent;

public class DownProgressAnnouncer {
	
	private Context context;
	
	public DownProgressAnnouncer(Context context) {
		this.context = context;
	}

	public void announceDownloadStarted(String label) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_STARTED);
		intent.putExtra("url", label);	// TODO overloading of the url field is bad
		context.sendBroadcast(intent);	
	}
	
	public void announceProgress(String url, int totalRead, long contentLength) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_UPDATE);
		intent.putExtra("url", url);
		intent.putExtra("bytes_received", totalRead);
		intent.putExtra("bytes_expected", contentLength);
		context.sendBroadcast(intent);
	}
	
	public void announceDownloadCompleted(String url) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_COMPLETED);
		intent.putExtra("url", url);
		context.sendBroadcast(intent);
	}
	
	public void announceDownloadFailed(String url) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_FAILED);
		intent.putExtra("url", url);
		context.sendBroadcast(intent);
	}
	
}
