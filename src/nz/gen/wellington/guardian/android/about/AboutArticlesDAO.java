package nz.gen.wellington.guardian.android.about;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleXmlParser;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.network.LoggingBufferedInputStream;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AboutArticlesDAO implements ArticleSource {
	
	static final String TAG = "AboutArticlesDAO";
	static final String ABOUT_ENDPOINT_URL = "http://guardian-lite.appspot.com/about";
	
	private Context context;
	HttpFetcher httpFetcher;
	ContentApiStyleXmlParser contentXmlParser;
	
	
	public AboutArticlesDAO(Context context) {
		this.context = context;
		this.contentXmlParser = new ContentApiStyleXmlParser(context);
		this.httpFetcher = new HttpFetcher(context);
	}
	
	public ArticleBundle getArticles(ArticleSet articleSet, ArticleCallback articleCallback) {
		Log.i(TAG, "Fetching about articles");
		
		announceDownloadStarted("About");
		LoggingBufferedInputStream input = httpFetcher.httpFetch(ABOUT_ENDPOINT_URL);
		if (input != null) {
			ArticleBundle results = contentXmlParser.parseArticlesXml(input, articleCallback);
			if (results != null && !results.getArticles().isEmpty()) {
				String checksum = input.getEtag();
				results.setChecksum(checksum);
				return results;			
			}
		}
		return null;
	}
	
	// TODO duplication with content api dao
	void announceDownloadStarted(String downloadName) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_STARTED);
		intent.putExtra("url", downloadName);
		context.sendBroadcast(intent);
	}

}
