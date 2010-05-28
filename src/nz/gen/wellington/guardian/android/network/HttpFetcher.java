package nz.gen.wellington.guardian.android.network;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpFetcher {
	
	private static final String TAG = "HttpFetcher";
	
	public String httpFetch(String uri) {
		try {
			Log.i(TAG, "Making http fetch of: " + uri);
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(uri);
		
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			return client.execute(get, responseHandler);
			
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

}
