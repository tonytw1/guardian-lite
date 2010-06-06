package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.api.ArticleBodyCleaner;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionColourMap;
import nz.gen.wellington.guardian.android.model.Tag;

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

	public String parseArticleJSONForMainPictureUrl(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			if (!isResponseOk(json)) {
				return null;				
			}
			
			JSONObject response = json.getJSONObject("response");
			JSONObject content = response.getJSONObject("content");
			
			if (content.has("mediaAssets")) {				
				JSONArray mediaAssets = content.getJSONArray("mediaAssets");
			
				// TODO better targeting.
				if (mediaAssets.length() > 0) {
					JSONObject first = mediaAssets.getJSONObject(0);
					if (first.has("file") && first.has("type")) {						
						if (first.getString("type").equals("picture")) {
							return first.getString("file");							
						}
					}
				}
				
			}
			return null;
						
		} catch (JSONException e) {
			Log.e(TAG, "JSONException while parsing article: " + e.getMessage());
			return null;
		}
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
				article.setByline(
						ArticleBodyCleaner.stripHtml(getJsonFields(fields, "byline")));
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
					Tag author = new Tag(
						getJsonFields(tag, "webTitle"), 
						getJsonFields(tag, "id"));
					article.addAuthor(author);
					
				} else if (type.equals(KEYWORD)) {
					Tag keyword = new Tag(
							getJsonFields(tag, "webTitle"), 
							getJsonFields(tag, "id"));
					article.addKeyword(keyword);
				}
			}
			
			
			if (tags.length() > 0) {
				JSONObject tag = tags.getJSONObject(0);
				article.setSectionId(getJsonFields(tag, "sectionId"));					
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
				final String id =  section.getString("id");
				sections.add(new Section(sectionName, id, SectionColourMap.getColourForSection(id)));
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
	
	public String getUserTier(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString); // TODO wasteful duplicate parsing
			if (!isResponseOk(json)) {
				return null;				
			}
			JSONObject response = json.getJSONObject("response");
			if (response.has("userTier")) {
				return response.getString("userTier");
			}
			return null;
		} catch (JSONException e) {
			return null;
		}		
	}
	
	private String getJsonFields(JSONObject jsonObject, String field) throws JSONException {
		if (jsonObject.has(field)) {
			return jsonObject.getString(field);
		}
		return null;
	}

	
}
