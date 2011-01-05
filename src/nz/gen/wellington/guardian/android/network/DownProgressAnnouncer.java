package nz.gen.wellington.guardian.android.network;

import android.content.Context;
import android.content.Intent;

public class DownProgressAnnouncer {
	
	private Context context;
	
	public DownProgressAnnouncer(Context context) {
		super();
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
