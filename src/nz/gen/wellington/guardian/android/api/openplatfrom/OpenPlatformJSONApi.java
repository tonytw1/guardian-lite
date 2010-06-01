package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.util.List;

import nz.gen.wellington.guardian.android.api.ContentSource;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.network.HttpFetcher;
import android.util.Log;

public class OpenPlatformJSONApi implements ContentSource {
		
	private static final String TAG = "OpenPlatformJSONApi";
	
	private static final String SECTIONS_JSON_URL = "http://content.guardianapis.com/sections?format=json";
	private static final int PAGE_SIZE = 10;
	
	private String apiKey;
	public HttpFetcher httpFetcher;
	protected OpenPlatformJSONParser jsonParser;	
	
	
	public OpenPlatformJSONApi(String apiKey) {
		this.apiKey = apiKey;	// TODO not here - state gets stuck
		Log.d(TAG, "Apikey set from preferences to: " + apiKey);
		httpFetcher = new HttpFetcher();
		jsonParser = new  OpenPlatformJSONParser();
	}

	
	@Override
	public List<Article> getArticles(ArticleSet articleSet) {
		Log.i(TAG, "Fetching articles for: " + articleSet.getName());
		final String json = getJSON(buildContentQueryUrl(articleSet));
		if (json != null) {	
			List<Article> articles = jsonParser.parseArticlesJSON(json);

			// TODO key level checking - only do if your key has this.
			for (Article article : articles) {
				String itemApiUrl = buildContentItemQueryUrl(article.getId());
				final String itemJson = getJSON(itemApiUrl);
				if (itemJson != null) {
					article.setMainImageUrl(jsonParser.parseArticleJSONForMainPictureUrl(itemJson));
					Log.i(TAG, "Found article main picture: " + article.getMainImageUrl());
				}
			}
			return articles;
		}
		return null;
	}

	@Override
	public List<Section> getSections() {
		Log.i(TAG, "Fetching section list from Open Platform api");
		String jsonString = getJSON(SECTIONS_JSON_URL);
		if (jsonString != null) {
			return jsonParser.parseSectionsJSON(jsonString);
		}
		return null;
	}
	

	private String buildContentItemQueryUrl(String id) {
		StringBuilder url = new StringBuilder("http://content.guardianapis.com/"); // TODO should use the apiUrl Field
		url.append(id);
		url.append("?show-media=all");
		url.append("&api-key=" + apiKey);
		url.append("&format=json");
		return url.toString();
	}
	
	
	private String buildContentQueryUrl(ArticleSet articleSet) {		
		StringBuilder url = new StringBuilder(articleSet.getApiUrl());
		url.append("?show-fields=all");
		url.append("&show-tags=all");
		url.append("&api-key=" + apiKey);
		url.append("&page-size=" + PAGE_SIZE);
		url.append("&tag=type%2Farticle");
		url.append("&format=json");
		return url.toString();
	}
	
	private String getJSON(String url) {
		return httpFetcher.httpFetch(url);		
	}

}
