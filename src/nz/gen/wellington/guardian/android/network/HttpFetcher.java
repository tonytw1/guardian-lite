package nz.gen.wellington.guardian.android.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpFetcher {
	
	private static final String TAG = "HttpFetcher";
	
	public String httpFetch(String uri) {
		try {
			Log.i(TAG, "Making http fetch of: " + uri);
			HttpClient client = new DefaultHttpClient();
			
			((AbstractHttpClient) client)
					.addRequestInterceptor(new HttpRequestInterceptor() {
						public void process(final HttpRequest request,
								final HttpContext context)
								throws HttpException, IOException {
							if (!request.containsHeader("Accept-Encoding")) {
								request.addHeader("Accept-Encoding", "gzip");
								Log.i(TAG, "Added gzip header");
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
										Log.i(TAG, "Got gzip response");
										response.setEntity(new GzipDecompressingEntity(
												response.getEntity()));
										return;
									}
								}
							}
						}
					});
			
			HttpGet get = new HttpGet(uri);		
			HttpResponse response = client.execute(get);
			Log.i(TAG, "Content length was: " + response.getLastHeader("Content-Length"));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			return responseHandler.handleResponse(response); 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return null;
	}
	
	
	
	public byte[] httpFetchStream(String uri) {
		try {
			Log.i(TAG, "Making http fetch of image: " + uri);
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(uri);
		
			HttpResponse response = client.execute(get);
			
			byte[] byteArray = EntityUtils.toByteArray(response.getEntity());
			return byteArray;
			
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return null;
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
	            // length of ungzipped content is not known
	            return -1;
	        }

	    } 


}
