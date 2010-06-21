package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.BufferedReader;
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

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
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
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;

import android.content.Context;
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
	ArticleCallback articleCallback;
	
	
	public OpenPlatformJSONParser(Context context, ArticleCallback articleCallback) {
		this.context = context;
		consumedContent = new StringBuilder();
		this.articleCallback = articleCallback;
		running = true;
	}


	public List<Article> parseArticlesXml(InputStream inputStream, List<Section> sections) {		
		try {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser =  factory.newSAXParser();
            ResultsHandler hb = new ResultsHandler(articleCallback, sections);
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
         ArticleCallback articleCallback;
         private List<Section> sections;
         
         public ResultsHandler(ArticleCallback articleCallback, List<Section> sections) {
        	 this.articleCallback =articleCallback;
        	 this.sections = sections;
         }
         
         
         private Section getSectionById(String sectionId) {
 			for (Section section : sections) {
 				if (section.getId().equals(sectionId)) {
 					return section;
 				}
 			}
 			return null;			
 		}

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
        		 sb = new StringBuilder();
        		 	 
        		 article.setId(attributes.getValue("id"));
        		 
        		 final String sectionId =  attributes.getValue("section-id");
        		 Section section = getSectionById(sectionId);
        		 article.setSection(section);
        		 
        		 
        		 final String dateString = attributes.getValue("web-publication-date");
        		 try {
        			 DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
        			 article.setPubDate(fmt.parseDateTime(dateString));
        		 } catch (Exception e) {
        				Log.e(TAG, "Failed to parse date '" + dateString +  "': " + e.getMessage());
        		 }
        	 }
                 
        	 if (name.equals("field")) {                             
        		 String fieldname = attributes.getValue("name");
        		 if (!fieldname.equals(currentField)) {
        			 currentField = fieldname;
        		 }
        	 }
        	 
        	 if (name.equals("tag")) {
        		 Tag tag = new Tag(attributes.getValue("web-title"),
        				 attributes.getValue("id"),        				 
        				 null);
        		 article.addKeyword(tag);        		 
        	 }
        	 
        	 if (name.equals("assert")) {
        		 if (article.getMainImageUrl() == null && attributes.getType("type").equals("picture")) {
        			 article.setMainImageUrl(attributes.getValue("file"));        			 
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
					article.setByline(ArticleBodyCleaner.stripHtml(sb.toString()));
				}
				
				if (currentField.equals("standfirst")) {
					article.setStandfirst(ArticleBodyCleaner.stripHtml(sb.toString()));
				}
				
				if (currentField.equals("thumbnail")) {
					article.setThumbnailUrl(ArticleBodyCleaner.stripHtml(sb.toString()));
				}
				
				if (currentField.equals("body")) {
					article.setDescription(ArticleBodyCleaner.stripHtml(sb.toString()));
				}
				
				currentField = null;
				sb = new StringBuilder();

			}

			if (name.equals("content")) {
				articles.add(article);
				if (articleCallback !=  null) {
					articleCallback.articleReady(article);
				}
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
			
			StringBuilder content = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String str;
			while ((str = in.readLine()) != null) {
				content.append(str);
				content.append("\n");
			}
			in.close();
			
			JSONObject json = new JSONObject(content.toString());
			if (!isResponseOk(json)) {
				return null;				
			}
			JSONObject response = json.getJSONObject("response");
			JSONArray results = response.getJSONArray("results");
				
			List<Section> sections = new LinkedList<Section>();
			for (int i=0; i < results.length(); i++) {		
				JSONObject section = results.getJSONObject(i);				
				 final String sectionName = StringEscapeUtils.unescapeHtml(section.getString("webTitle"));
				 final String id = section.getString("id");
				 sections.add(new Section(id, sectionName, SectionColourMap.getColourForSection(id)));
			}
			
			return sections;			
			
		} catch (JSONException e) {
			Log.e(TAG, "JSONException while parsing articles: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
