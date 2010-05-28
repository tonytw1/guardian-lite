package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleBodyCleaner;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Author;
import nz.gen.wellington.guardian.android.model.Keyword;
import nz.gen.wellington.guardian.android.model.Section;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class OpenPlatformJSONParser {
			
	private static final String TAG = "OpenPlatformJSONParser";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	private static final String CONTRIBUTOR = "contributor";
	private static final String KEYWORD = "keyword";

	
	public List<Article> parseArticlesJSON(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			if (!isResponseOk(json)) {
				return null;
				
			}
			JSONObject response = json.getJSONObject("response");
			JSONArray results = response.getJSONArray("results");
				
			List<Article> articles = new ArrayList<Article>();
			for (int i=0; i < results.length(); i++) {				
				JSONObject result = results.getJSONObject(i);						
				articles.add(extractArticle(result));
			}				
			return articles;
			
		} catch (JSONException e) {
			Log.e(TAG, "JSONException while parsing articles: " + e.getMessage());
		}
		return null;		
	}

	
	
	private Article extractArticle(JSONObject result) throws JSONException {		
		Article article = new Article();
		
		final String guid = result.getString("id");
		article.setId(guid);
		
		if (result.has("webPublicationDate")) {
			final String dateString = getJsonFields(result, "webPublicationDate");
			try {
				DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_TIME_FORMAT);			
				article.setPubDate(fmt.parseDateTime(dateString));
			} catch (Exception e) {
				Log.e(TAG, "Failed to parse date '" + dateString +  "': " + e.getMessage());
			}
		}
		
		if (result.has("fields")) {
			JSONObject fields = result.getJSONObject("fields");
			if (fields != null) {
				article.setTitle(getJsonFields(fields, "headline"));
				article.setStandfirst(
						ArticleBodyCleaner.stripHtml(getJsonFields(fields, "standfirst")));
				article.setDescription(
						ArticleBodyCleaner.stripHtml(getJsonFields(fields, "body")));
				
				String thumbnail = getJsonFields(fields, "thumbnail");
				article.setThumbnailUrl(thumbnail);				
			}
		}
		
		if (result.has("tags")) {
			processTags(result, article);
		}
		return article;
	}
	
	
	
	private void processTags(JSONObject result, Article article) throws JSONException {
		JSONArray tags = result.getJSONArray("tags");
		if (tags != null) {
			for (int j=0; j < tags.length(); j++) {														
				JSONObject tag = tags.getJSONObject(j);				
				final String type = tag.getString("type");
								
				if (type.equals(CONTRIBUTOR)) {
					Author author = new Author(
						getJsonFields(tag, "webTitle"), 
						getJsonFields(tag, "apiUrl"));
					article.addAuthor(author);
					
				} else if (type.equals(KEYWORD)) {
					Keyword keyword = new Keyword(
							getJsonFields(tag, "webTitle"), 
							getJsonFields(tag, "apiUrl"));
					article.addKeyword(keyword);
				}				
			}						
		}
		return;
	}
	
	
	
	public List<Section> parseSectionsJSON(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			if (!isResponseOk(json)) {
				return null;				
			}
			JSONObject response = json.getJSONObject("response");
			JSONArray results = response.getJSONArray("results");
				
			List<Section> sections = new ArrayList<Section>();
			for (int i=0; i < results.length(); i++) {				
				JSONObject section = results.getJSONObject(i);				
				final String sectionName = StringEscapeUtils.unescapeHtml(section.getString("webTitle"));
				final String apiUrl =  section.getString("apiUrl");
				sections.add(new Section(sectionName, apiUrl));
			}				
			return sections;			
			
		} catch (JSONException e) {
			Log.e(TAG, "JSONException while parsing articles: " + e.getMessage());
		}
		return null;
	}
	
	
	public boolean isResponseOk(JSONObject json) {
		try {
			JSONObject response = json.getJSONObject("response");
			String status = response.getString("status");
			return status != null && status.equals("ok");
		} catch (JSONException e) {
			return false;
		}
	}
	
	
	private String getJsonFields(JSONObject jsonObject, String field) throws JSONException {
		if (jsonObject.has(field)) {
			return jsonObject.getString(field);
		}
		return null;
	}
	
}
