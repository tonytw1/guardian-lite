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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import nz.gen.wellington.guardian.android.factories.SingletonFactory;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class HttpFetcher {
	
	private static final String TAG = "HttpFetcher";
	
	private static final int HTTP_TIMEOUT = 15000;
    public static final String DOWNLOAD_PROGRESS = "nz.gen.wellington.guardian.android.network.DOWNLOAD_PROGRESS";
    
    private HttpGet activeGet;
	private HttpClient client;
	private DownProgressAnnouncer downProgressAnnouncer;
	private Context context;
		
	public static final int DOWNLOAD_STARTED = 1;
	public static final int DOWNLOAD_UPDATE = 2;
	public static final int DOWNLOAD_COMPLETED = 3;
	public static final int DOWNLOAD_FAILED = 4;

	
	public HttpFetcher(Context context) {
		this.context = context;
		this.downProgressAnnouncer = SingletonFactory.getDownloadProgressAnnouncer(context);
		
		client = new DefaultHttpClient();
		
		((AbstractHttpClient) client)
		.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(final HttpRequest request,
					final HttpContext context)
					throws HttpException, IOException {
				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
			}
		});
    			  
		((AbstractHttpClient) client)
		.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(final HttpResponse response,
					final HttpContext context)
					throws HttpException, IOException {
				HttpEntity entity = response.getEntity();
				Header ceheader = entity.getContentEncoding();
				if (ceheader != null) {
					HeaderElement[] codecs = ceheader.getElements();
					for (int i = 0; i < codecs.length; i++) {
						if (codecs[i].getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new GzipDecompressingEntity(
									response.getEntity()));
							return;
						}
					}
				}
			}
		});
		
		client.getParams().setParameter("http.socket.timeout", new Integer(HTTP_TIMEOUT));
		client.getParams().setParameter("http.connection.timeout", new Integer(HTTP_TIMEOUT));
	}
	
	
	@Deprecated
	public LoggingBufferedInputStream httpFetch(String sourceUrl) {
		return httpFetch(sourceUrl, null);
	}
	
	
	public LoggingBufferedInputStream httpFetch(String uri, String label) {
		try {
			Log.i(TAG, "Making http fetch of: " + uri);						
			HttpGet get = new HttpGet(uri);	
			
			get.addHeader(new BasicHeader("User-agent", "gzip"));
			get.addHeader(new BasicHeader("Accept-Encoding", "gzip"));
			
			if (label != null) {
				downProgressAnnouncer.announceDownloadStarted(label);
			}
			HttpResponse execute = executeGet(get);
			final int statusCode = execute.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				
				String etag = null;
				Header[] etagHeader = execute.getHeaders("Etag");
				if (etagHeader != null && etagHeader.length == 1) {
					etag = etagHeader[0].getValue();
				}
				long contentLength = execute.getEntity().getContentLength();
				LoggingBufferedInputStream is = new LoggingBufferedInputStream(execute.getEntity().getContent(), 1024, context, contentLength, etag);				
				return is;
			}
			
			Log.w(TAG, "Fetch of '" + uri + "' failed: " + statusCode);
			downProgressAnnouncer.announceDownloadFailed(uri);
			return null;
			
		} catch (Exception e) {
			Log.e(TAG, "Http exception: " + e.getMessage());
			downProgressAnnouncer.announceDownloadFailed(uri);
		}
		return null;
	}


	private HttpResponse executeGet(HttpGet get) throws IOException, ClientProtocolException {
		activeGet = get;
		return client.execute(get);
	}

	
	public byte[] httpFetchStream(String uri) {
		try {
			Log.i(TAG, "Making http fetch of image: " + uri);
			HttpGet get = new HttpGet(uri);		
			return EntityUtils.toByteArray(executeGet(get).getEntity());
									
		} catch (Exception e) {
			Log.e(TAG, "Http exception: " + e.getMessage());
		}
		return null;
	}
	
	
	@Deprecated
	public String httpEtag(String contentApiUrl) {
		return httpEtag(contentApiUrl, null);
	}
	
	
	public String httpEtag(String url, String label) {
		try {
			Log.i(TAG, "Making http etag head fetch of url: " + url);
			HttpGet get = new HttpGet(url);
			if (label != null) {
				downProgressAnnouncer.announceDownloadStarted(label);
			}
			HttpResponse execute = executeGet(get);
			if (execute.getStatusLine().getStatusCode() == 200) {
				Header[] etags = execute.getHeaders("Etag");
				if (etags.length == 1) {
					final String result = etags[0].getValue();
					downProgressAnnouncer.announceDownloadCompleted(url);
					return result;
				}
			}
			
		} catch (Exception e) {
			Log.e(TAG, "Http exception: " + e.getMessage());
		}
		
		downProgressAnnouncer.announceDownloadFailed(url);
		return null;
	}
	
	public void stopLoading() {
		Log.i(TAG, "Stopping loading");
		if (activeGet != null) {
			activeGet.abort();
		}
	}
	
	
	static class GzipDecompressingEntity extends HttpEntityWrapper {

        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }
    
        @Override
        public InputStream getContent()
            throws IOException, IllegalStateException {

            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            return this.wrappedEntity.getContentLength();
        }
    }
	
}
