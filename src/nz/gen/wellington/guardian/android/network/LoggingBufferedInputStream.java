package nz.gen.wellington.guardian.android.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LoggingBufferedInputStream extends BufferedInputStream {
	
	private static final String TAG = "LoggingBufferedInputStream";

	Context context;
	int totalRead;
	long contentLength;
	
	public LoggingBufferedInputStream(InputStream in, Context context) {
		super(in);
		this.context = context;
		totalRead = 0;
	}

	public LoggingBufferedInputStream(InputStream in, int size, Context context, long contentLength) {
		super(in, size);
		this.context = context;
		totalRead = 0;
		this.contentLength = contentLength;
	}

	@Override
	public synchronized int read(byte[] buffer, int offset, int length) throws IOException {
		int read = super.read(buffer, offset, length);
		totalRead = totalRead + read;
		announceProgress("dsjd", totalRead);
		return read;	
	}
	
	@Override
	public int read(byte[] buffer) throws IOException {
		int read =  super.read(buffer);
		totalRead = totalRead + read;
		announceProgress("dsjd", totalRead);
		return read;
	}
	
	
	@Override
	public synchronized void close() throws IOException {
		super.close();
		announceDownloadCompleted("");
	}
	

	private void announceProgress(String url, int totalRead) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_UPDATE);
		intent.putExtra("url", url);
		intent.putExtra("bytes_received", totalRead);
		intent.putExtra("bytes_expected", contentLength);
		context.sendBroadcast(intent);
	}

	
	
	private void announceDownloadCompleted(String url) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_COMPLETED);
		intent.putExtra("url", url);
		context.sendBroadcast(intent);
	}
	
}
