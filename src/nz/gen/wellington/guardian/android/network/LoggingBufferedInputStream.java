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

	public LoggingBufferedInputStream(InputStream in, int size, Context context, long contentLength, String etag) {
		super(in, size);
		this.downProgressAnnouncer = SingletonFactory.getDownloadProgressAnnouncer(context);	
		totalRead = 0;
		this.contentLength = contentLength;
		this.etag = etag;		
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
