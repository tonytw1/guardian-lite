package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.dates.DateTimeHelper;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;

import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;

import android.content.Context;

public class ContentApiStyleXmlParser {
	
	public static final String ARTICLE_AVAILABLE = "nz.gen.wellington.guardian.android.api.ARTICLE_AVAILABLE";

	private boolean running;
	ResultsHandler hb;
	
	public ContentApiStyleXmlParser(Context context) {
		running = true;
	}


	public List<Article> parseArticlesXml(InputStream inputStream, List<Section> sections, ArticleCallback articleCallback) {
		running = true;
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			hb = new ResultsHandler(articleCallback, sections);
			saxParser.parse(inputStream, hb);
			inputStream.close();
			return hb.getArticles();

		} catch (SAXException e) {
			//Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			//Log.e(TAG, e.getMessage());
		} catch (ParserConfigurationException e) {
			//Log.e(TAG, e.getMessage());
		}
		return null;
	}
	
	public Map<String, List<ArticleSet>> getRefinements() {
		if (hb != null) {
			return hb.getRefinements();
		}
		return new HashMap<String, List<ArticleSet>>();
	}

	

	public String getChecksum() {
		if (hb != null) {
			return hb.getChecksum();
		}
		return null;
	}
	
	
	public String getDescription() {
		if (hb != null) {
			return hb.getDescription();
		}
		return null;
	}

	 class ResultsHandler extends HandlerBase {
         
		 List<Article> articles;
		 Map<String, List<ArticleSet>> refinements;
         Article article;
         String checksum;
         String description;
         
         StringBuilder sb = new StringBuilder();
         String currentField;
         String currentRefinementGroupType;
         ArticleCallback articleCallback;
         private List<Section> sections;
         
         public ResultsHandler(ArticleCallback articleCallback, List<Section> sections) {
        	 this.articleCallback =articleCallback;
        	 this.sections = sections;
         }
                  
         public Map<String, List<ArticleSet>> getRefinements() {
			return refinements;
         }
         
         public String getChecksum() {        	 
        	 return checksum;
         }

         public String getDescription() {
			return description;
         }

         private Section getSectionById(String sectionId) {
        	 // TODO sections null check should be at a much higher level.
        	 if (sections == null || sectionId == null) {
        		 return null;
        	 }
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
			refinements = new HashMap<String, List<ArticleSet>>();
		}

         @Override
         public void startElement(String name, AttributeList attributes) throws SAXException {        	 
        	 super.startElement(name, attributes);
        	 if (!running) {
        		 throw new SAXException("Parse has been stopped");        		 
        	 }
        	 
        	 if (name.equals("content")) {
        		 article = new Article();
        		 sb = new StringBuilder();
        		 	 
        		 article.setId(attributes.getValue("id"));
        		 
        		 final String sectionId =  attributes.getValue("section-id");
        		 Section section = getSectionById(sectionId);
        		 article.setSection(section);
        		 
        		 
        		 final String dateString = attributes.getValue("web-publication-date");
        		 article.setPubDate(DateTimeHelper.parseDate(dateString));        		 
        	 }
                 
        	 if (name.equals("field")) {                             
        		 String fieldname = attributes.getValue("name");
        		 if (!fieldname.equals(currentField)) {
        			 currentField = fieldname;
        		 }
        	 }
        	 
        	 if (name.equals("results")) {
        		 checksum = attributes.getValue("checksum");
        		 description = attributes.getValue("description");
        		 if (articleCallback != null) {
        			 articleCallback.descriptionReady(description);
        		 }
        	 }
        	 
        	 if (name.equals("tag")) {
        		 
        		 if (attributes.getValue("type").equals("keyword")) {
        			 Section tagSection = getSectionById(attributes.getValue("section-id"));        		 
        			 Tag tag = new Tag(attributes.getValue("web-title"),
        				 attributes.getValue("id"), tagSection);        		 
        			 article.addKeyword(tag);
        		 }
        		 
        		 if (attributes.getValue("type").equals("contributor")) {
        			 Tag tag = new Tag(attributes.getValue("web-title"),
            				 attributes.getValue("id"), null);        
        			 article.addAuthor(tag);
        		 }
        	 }
        	 
        	 if (name.equals("refinement-group")) {
        		 currentRefinementGroupType = attributes.getValue("type");
        	 }
        	 
        	 if (name.equals("refinement")) {
        		 if (currentRefinementGroupType != null) {
        			 List<ArticleSet> refinementGroup = refinements.get(currentRefinementGroupType);
        			 if (refinementGroup == null) {
        				 refinementGroup = new ArrayList<ArticleSet>();
        				 refinements.put(currentRefinementGroupType, refinementGroup);
        			 }
        			 
        			 boolean isTagRefinement = true;	// TODO limit to tag typed - ie not date
        			 if (isTagRefinement) {
        				 final String tagId = attributes.getValue("id");        			 
        				 final String sectionId = tagId.split("/")[0];        			 
        				 Section section = getSectionById(sectionId);        			 
        				 final Tag refinementTag = new Tag(attributes.getValue("display-name"), tagId, section);        			 
        				 refinementGroup.add(ArticleSetFactory.getArticleSetForTag(refinementTag));
        			 }
        		 }
        	 }
        	         	 
        	 if (name.equals("asset")) {
        		 if (article.getMainImageUrl() == null && attributes.getValue("type").equals("picture")) {
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
					article.setByline(sb.toString());
				}
				
				if (currentField.equals("standfirst")) {
					article.setStandfirst(sb.toString());
				}
				
				if (currentField.equals("thumbnail")) {
					article.setThumbnailUrl(sb.toString());
				}
				
				if (currentField.equals("body")) {
					article.setDescription(sb.toString());
				}
				
				if (currentField.equals("caption")) {
					article.setCaption(sb.toString());
				}
				
				currentField = null;
				sb = new StringBuilder();
			}

			if (name.equals("content")) {
				//boolean isArticleValid = article.getSection() != null || articles
				//if (isArticleValid) {
					articles.add(article);
					if (articleCallback !=  null) {
						articleCallback.articleReady(article);
					}
				//} else {
				//	Log.w(TAG, "Invalid article: " + article.getId());
				//}
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
		
	 }
	 
	 
	 public void stop() {
		 running = false;		
	 }
	
}
