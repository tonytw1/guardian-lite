package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nz.gen.wellington.guardian.android.api.ArticleBodyCleaner;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionColourMap;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.network.LoggingBufferedInputStream;

import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OpenPlatformJSONParser {
			
	private static final String TAG = "OpenPlatformJSONParser";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	private static final String CONTRIBUTOR = "contributor";
	private static final String KEYWORD = "keyword";
	public static final String ARTICLE_AVAILABLE = "nz.gen.wellington.guardian.android.api.ARTICLE_AVAILABLE";

	private Context context;
	private StringBuilder consumedContent;
	private boolean running;	
	
	
	public OpenPlatformJSONParser(Context context) {
		this.context = context;
		consumedContent = new StringBuilder();
		running = true;
	}


	public List<Article> parseArticlesXml(InputStream inputStream, List<Section> sections) {
		
		try {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser =  factory.newSAXParser();
            ResultsHandler hb = new ResultsHandler();
				saxParser.parse(inputStream, hb);
				inputStream.close();
				
				consumedContent= null;		
				return hb.getArticles();
				
				
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		return null;		
	}
	
	
	

	 class ResultsHandler extends HandlerBase {
         
		 List<Article> articles;
         Article article;
         
         StringBuilder sb = new StringBuilder();
         String currentField;
         
         public List<Article> getArticles() {
        	 return articles;
         }

         @Override
         public void startDocument() throws SAXException {
                 super.startDocument();
                 articles = new LinkedList<Article>();
         }

         @Override
         public void startElement(String name, AttributeList attributes) throws SAXException {
        	 super.startElement(name, attributes);
        	 if (name.equals("content")) {
        		 article = new Article();
        		 article.setTitle("TEST");
        		 sb = new StringBuilder();
        	 }
                 
        	 if (name.equals("field")) {                             
        		 String fieldname = attributes.getValue("name");
        		 //System.out.println("Starting field: " + fieldname);
        		 if (!fieldname.equals(currentField)) {
        			 currentField = fieldname;
        		 }
        	 }
         }

		
		@Override
		public void endElement(String name) throws SAXException {
			super.endElement(name);

			if (currentField != null) {
				
				if (currentField.equals("headline")) {
					article.setTitle(sb.toString());
				}
				
				if (currentField.equals("byline")) {
					Log.d(TAG, sb.toString());
					article.setByline(sb.toString());
				}
				
				//if (currentField.equals("standfirst")) {
				//	article.setStandfirst(sb.toString());
				//}
				
				if (currentField.equals("body")) {
					article.setDescription(sb.toString());
				}
				currentField = null;
			}

			if (name.equals("content")) {
				articles.add(article);
				announceArticleExtracted(article);
			}
		}

         
         @Override
         public void characters(char[] ch, int start, int length) throws SAXException {
                 super.characters(ch, start, length);
                 if (currentField != null) {
                         for (int i = start; i < start + length; i++) {
                                 sb.append(ch[i]);
                         }
                 }
                 
         }

		public StringBuilder getConsumedContent() {
			Log.d(TAG, consumedContent.toString());
			return consumedContent;
		}
         
	 }


	
	
	
	


	private void announceArticleExtracted(Article article) {
		Intent intent = new Intent(ARTICLE_AVAILABLE);
		intent.putExtra("article", article);
		context.sendBroadcast(intent);
	}
	
	
	private String readInputStreamToString(InputStream inputStream) {
		StringBuilder content = new StringBuilder();
		try {
			Reader reader;
			reader = new InputStreamReader(inputStream, "UTF-8");
			int read;
			final char[] buffer = new char[1024];
			do {
				read = reader.read(buffer, 0, buffer.length);
				if (read > 0 && running) {				 									
					content.append(buffer, 0, read);
				}
			} while (read >= 0 && running);
			reader.close();
			return content.toString();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	private Article extractArticle(JSONObject result, List<Section> sections) throws JSONException {		
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
			processTags(result, article, sections);
		}
		
		if (result.has("mediaAssets")) {
			JSONArray mediaAssets = result.getJSONArray("mediaAssets");
			parseArticleJSONForMainPicture(mediaAssets, article);
		}
		return article;
	}
	
	
	private void processTags(JSONObject result, Article article, List<Section> sections) throws JSONException {
		JSONArray tags = result.getJSONArray("tags");
		if (tags != null) {
			for (int j=0; j < tags.length(); j++) {														
				JSONObject tag = tags.getJSONObject(j);				
				final String type = tag.getString("type");
								
				if (type.equals(CONTRIBUTOR)) {
					Tag author = new Tag(
						getJsonFields(tag, "webTitle"), 
						getJsonFields(tag, "id"), null);
					article.addAuthor(author);
					
				} else if (type.equals(KEYWORD)) {
					
					Section tagSection = null;
					final String sectionId = getJsonFields(tag, "sectionId");
					for (Section section : sections) {
						if (section.getId().equals(sectionId)) {
							tagSection = section;
						}
					}
					
					
					Tag keyword = new Tag(
							getJsonFields(tag, "webTitle"), 
							getJsonFields(tag, "id"),
							tagSection);
					article.addKeyword(keyword);
				}
			}
						
			if (tags.length() > 0) {
				JSONObject tag = tags.getJSONObject(0);
				final String sectionId = getJsonFields(tag, "sectionId");
				for (Section section : sections) {
					if (section.getId().equals(sectionId)) {
						article.setSection(section);
					}
				}
			}
		}
		
		return;
	}
	
	
	private void parseArticleJSONForMainPicture(JSONArray mediaAssets, Article article) {
		try {
			// TODO better targeting.
			if (mediaAssets.length() > 0) {
				JSONObject first = mediaAssets.getJSONObject(0);
				if (first.has("file") && first.has("type")) {
					
					if (first.getString("type").equals("picture")) {
						final String mainImageUrl = (String) first.getString("file");
						article.setMainImageUrl(mainImageUrl);
						Log.i(TAG, "Found main picture: " + mainImageUrl);

						if (first.has("fields")) {
							JSONObject fields = first.getJSONObject("fields");
							if (fields.has("caption")) {
								article.setCaption(fields.getString("caption"));
							}
						}
					}
				}
			}
			return;
			
		} catch (JSONException e) {
			Log.e(TAG, "JSONException while parsing media elements: " + e.getMessage());
			return;
		}
	}
	
	
	public List<Section> parseSectionsJSON(InputStream input) {
		try {
			
			final String content = readInputStreamToString(input);
			if (content == null || content.length() == 0) {
				return null;
			}
			
			JSONObject json = new JSONObject(content);
			if (!isResponseOk(json)) {
				return null;				
			}
			JSONObject response = json.getJSONObject("response");
			JSONArray results = response.getJSONArray("results");
				
			List<Section> sections = new LinkedList<Section>();
			for (int i=0; i < results.length(); i++) {		
				JSONObject section = results.getJSONObject(i);				
				final String sectionName = StringEscapeUtils.unescapeHtml(section.getString("webTitle"));
				final String id =  section.getString("id");
				sections.add(new Section(id, sectionName, SectionColourMap.getColourForSection(id)));
			}
			
			consumedContent.append(content);
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
	
	
	public String getConsumedContent() {
		Log.d(TAG, consumedContent.toString());
		return consumedContent.toString();
	}
	
}
