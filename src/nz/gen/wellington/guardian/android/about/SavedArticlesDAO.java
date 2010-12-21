package nz.gen.wellington.guardian.android.about;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.openplatfrom.ContentApiStyleXmlParser;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SavedArticlesArticleSet;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SavedArticlesDAO implements ArticleSource {
	
	static final String TAG = "SavedArticlesDAO";
	static final String ENDPOINT_URL = "http://guardian-lite.appspot.com/saved";
	
	private Context context;
	HttpFetcher httpFetcher;
	ContentApiStyleXmlParser contentXmlParser;
		
	public SavedArticlesDAO(Context context) {
		this.context = context;
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
					URLEncoder.encode(",");
				}				
			}
		}
		return url.toString();
	}
	
	public ArticleBundle getArticles(ArticleSet articleSet, ArticleCallback articleCallback) {
		Log.i(TAG, "Fetching saved articles");		
		//announceDownloadStarted("Saved articles");
		//LoggingBufferedInputStream input = httpFetcher.httpFetch(articleSet.getSourceUrl());
		//if (input != null) {
		//	ArticleBundle results = contentXmlParser.parseArticlesXml(input, articleCallback);
		//	if (results != null && !results.getArticles().isEmpty()) {
		//		String checksum = input.getEtag();
		//		results.setChecksum(checksum);
		//		return results;
		//	}
		//}
		
		List<Article> mockArticles = new ArrayList<Article>();
		for (String articleId : ((SavedArticlesArticleSet) articleSet).getArticlesIds()) {
			Article mockArticle = new Article();
			mockArticle.setId(articleId);
			mockArticle.setTitle(articleId);
			mockArticle.setStandfirst(articleId);
			mockArticle.setDescription(articleId);
			mockArticles.add(mockArticle);
		}
		return new ArticleBundle(mockArticles, null, null, null);
	}
	
	// TODO duplication with content api dao
	void announceDownloadStarted(String downloadName) {
		Intent intent = new Intent(HttpFetcher.DOWNLOAD_PROGRESS);
		intent.putExtra("type", HttpFetcher.DOWNLOAD_STARTED);
		intent.putExtra("url", downloadName);
		context.sendBroadcast(intent);
	}
	
}
