package nz.gen.wellington.guardian.android.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import nz.gen.wellington.guardian.android.factories.SingletonFactory;

import android.content.Context;

public class LoggingBufferedInputStream extends BufferedInputStream {
	
	private DownProgressAnnouncer downProgressAnnouncer;
	private int totalRead;
	private long contentLength;
	private String etag;
	
	public LoggingBufferedInputStream(InputStream in, Context context) {
		super(in);
		this.downProgressAnnouncer = SingletonFactory.getDownloadProgressAnnouncer(context);
		totalRead = 0;
	}

	public LoggingBufferedInputStream(InputStream in, int size, Context context, long contentLength, String etag, String label) {
		super(in, size);
		this.downProgressAnnouncer = SingletonFactory.getDownloadProgressAnnouncer(context);	
		totalRead = 0;
		this.contentLength = contentLength;
		this.etag = etag;
		if (label != null) {
			downProgressAnnouncer.announceDownloadStarted(label);
		}
	}

	@Override
	public synchronized int read(byte[] buffer, int offset, int length) throws IOException {
		int read = super.read(buffer, offset, length);
		totalRead = totalRead + read;
		downProgressAnnouncer.announceProgress("", totalRead, contentLength);
		return read;
	}
	
	@Override
	public int read(byte[] buffer) throws IOException {
		int read =  super.read(buffer);
		totalRead = totalRead + read;
		downProgressAnnouncer.announceProgress("", totalRead, contentLength);
		return read;
	}
		
	@Override
	public synchronized void close() throws IOException {
		super.close();
		downProgressAnnouncer.announceDownloadCompleted("");
	}
	
	public String getEtag() {
		return etag;
	}
		
}
