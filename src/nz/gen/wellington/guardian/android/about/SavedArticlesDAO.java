package nz.gen.wellington.guardian.android.about;

import java.net.URLEncoder;
import java.util.Iterator;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleXmlParser;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import nz.gen.wellington.guardian.android.network.LoggingBufferedInputStream;
import android.content.Context;
import android.util.Log;

public class SavedArticlesDAO implements ArticleSource {
	
	private static final String TAG = "SavedArticlesDAO";
	private static final String ENDPOINT_URL = "http://guardian-lite.appspot.com/saved";
	private static final String URL_ENCODED_COMMA = URLEncoder.encode(",");	
	
	HttpFetcher httpFetcher;
	ContentApiStyleXmlParser contentXmlParser;
		
	public SavedArticlesDAO(Context context) {
		this.contentXmlParser = new ContentApiStyleXmlParser(context);
		this.httpFetcher = new HttpFetcher(context);
	}
	
	public String getArticleSetUrl(SavedArticlesArticleSet articleSet) {
		StringBuilder url = new StringBuilder(ENDPOINT_URL);
		if (!articleSet.getArticlesIds().isEmpty()) {
			url.append("?content=");
			for (Iterator<String> iterator = articleSet.getArticlesIds().iterator(); iterator.hasNext();) {
				String articleId = (String) iterator.next();
				url.append(URLEncoder.encode(articleId));
				if (iterator.hasNext()) {
					url.append(URL_ENCODED_COMMA);
				}
			}
		}
		return url.toString();
	}
	
	public ArticleBundle getArticles(ArticleSet articleSet, ArticleCallback articleCallback) {
		Log.i(TAG, "Fetching saved articles");		
		LoggingBufferedInputStream input = httpFetcher.httpFetch(articleSet.getSourceUrl(), "Saved articles");
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
	
}