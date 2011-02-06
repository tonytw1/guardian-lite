package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.api.filtering.HtmlCleaner;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.MediaElement;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.utils.DateTimeHelper;

import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

public class ContentResultsHandler extends HandlerBase {

	private static final String TAG = "ContentResultsHandler";
	private static final String NO_REDISTRIBUTION_RIGHTS_BODY_TEXT = "<!-- Redistribution rights for this field are unavailable -->";
	private static final List<String> TAG_TYPES_TO_TAKE = Arrays.asList("keyword", "type");
	
	public List<Article> articles;
	public Map<String, List<Refinement>> refinements;
	public String checksum;
	public String description;
	public StringBuilder currentElementContents;
	private MediaElement currentMediaElement;
	public Article currentArticle;
	public String currentField;
	public String currentRefinementGroupType;
	public ArticleCallback articleCallback;
	private HtmlCleaner htmlCleaner;
	private ArticleSetFactory articleSetFactory;

	private boolean running = true;
	private Context context;
	

	public ContentResultsHandler(Context context, HtmlCleaner htmlCleaner) {
		this.htmlCleaner = htmlCleaner;
		this.context = context;
		this.articleSetFactory = SingletonFactory.getArticleSetFactory(context);
	}
	
	public void setArticleCallback(ArticleCallback articleCallback) {
		this.articleCallback = articleCallback;
	}
	
	public ArticleBundle getResult() {
		if (!articles.isEmpty()) {
			return new ArticleBundle(articles, refinements, checksum, description);
		}
		return null;		
	}
	
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		articles = new LinkedList<Article>();
		refinements = new HashMap<String, List<Refinement>>();
	}

	@Override
	public void startElement(String name, AttributeList attributes) throws SAXException {
		SectionDAO sectionDAO = SingletonFactory.getSectionDAO(context);
		
		super.startElement(name, attributes);
		if (!running) {
			throw new SAXException("Parser has been stopped");
		}

		String tagSectionId = attributes.getValue("section-id");
		if (name.equals("content")) {
			currentArticle = new Article();
			currentElementContents = new StringBuilder();
			currentArticle.setId(attributes.getValue("id"));
			currentArticle.setWebUrl(attributes.getValue("web-url"));
			
			final String sectionId = tagSectionId;
			Section section = sectionDAO.getSectionById(sectionId);
			currentArticle.setSection(section);

			final String dateString = attributes.getValue("web-publication-date");
			currentArticle.setPubDate(DateTimeHelper.parseDate(dateString));
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
			if (TAG_TYPES_TO_TAKE.contains(attributes.getValue("type"))) {
				Section tagSection = null;
				if (tagSectionId != null) {
					tagSection = sectionDAO.getSectionById(tagSectionId);
				}
				Tag tag = new Tag(attributes.getValue("web-title"), attributes.getValue("id"), tagSection);
				currentArticle.addTag(tag);
			}
			
			if (attributes.getValue("type").equals("contributor")) {
				Tag tag = new Tag(attributes.getValue("web-title"), attributes.getValue("id"), null);
				currentArticle.addAuthor(tag);	// TODO depricate and put into tags
			}
			
		}

		if (name.equals("refinement-group")) {
			currentRefinementGroupType = attributes.getValue("type");
		}

		if (name.equals("refinement")) {
			if (currentRefinementGroupType != null) {
				processRefinement(attributes, sectionDAO);
			}
		}

		if (name.equals("asset")) {
			currentMediaElement = new MediaElement(attributes.getValue("type"), attributes.getValue("file"));			
		}
	}

	@Override
	public void endElement(String name) throws SAXException {
		super.endElement(name);
		
		if (currentField != null && currentMediaElement == null) {

			if (currentField.equals("headline")) {
				currentArticle.setTitle(htmlCleaner.stripHtml(currentElementContents.toString()));
			}

			if (currentField.equals("byline")) {
				currentArticle.setByline(htmlCleaner.stripHtml(currentElementContents.toString()));
			}

			if (currentField.equals("standfirst")) {
				currentArticle.setStandfirst(htmlCleaner.stripHtml(currentElementContents.toString()));
			}

			if (currentField.equals("thumbnail")) {
				currentArticle.setThumbnailUrl(currentElementContents.toString());
			}
			
			if (currentField.equals("short-url")) {			
				currentArticle.setShortUrl(currentElementContents.toString());
			}
			
			if (currentField.equals("body")) {
				final String rawBodyText = currentElementContents.toString();
				currentArticle.setRedistributionAllowed(!NO_REDISTRIBUTION_RIGHTS_BODY_TEXT.equals(rawBodyText));
				currentArticle.setDescription(htmlCleaner.stripHtml(rawBodyText));
			}
			
			currentField = null;
			currentElementContents = new StringBuilder();
		}
		
		if (currentMediaElement != null) {
			if (currentField != null && currentField.equals("thumbnail")) {
				currentMediaElement.setThumbnail(currentElementContents.toString());
			}
			
			if (currentField != null && currentField.equals("caption")) {
				currentMediaElement.setCaption(htmlCleaner.stripHtml(currentElementContents.toString()));
			}
			
			if (currentField != null && currentField.equals("width")) {
				currentMediaElement.setWidth(Integer.parseInt(currentElementContents.toString()));
			}
			
			currentField = null;
			currentElementContents = new StringBuilder();
		}
				
		if (name.equals("asset")) {
			currentArticle.addMediaElement(new MediaElement(currentMediaElement));
			currentMediaElement = null;
		}

		if (name.equals("content")) {
			boolean isArticleValid = true; // currentArticle.getId() != null; TODO this needs to be higher up - about and content api have different rules
			if (isArticleValid) {
				articles.add(currentArticle);
				if (articleCallback != null) {
					articleCallback.articleReady(currentArticle);
				}
				
			} else {
				Log.w(TAG, "Ignoring invalid article: " + currentArticle.getTitle());
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (currentField != null) {
			for (int i = start; i < start + length; i++) {
				currentElementContents.append(ch[i]);
			}
		}
	}

	public void stop() {
		this.running = false;
	}
	
		
	private void processRefinement(AttributeList attributes, SectionDAO sectionDAO) {		
		final String displayName = attributes.getValue("display-name");
		
		List<String> tagRefinementTypes = Arrays.asList("blog", "contributor", "keyword", "series");		
		boolean isTagRefinement = tagRefinementTypes.contains(currentRefinementGroupType);
		if (isTagRefinement) {
			final String tagId = attributes.getValue("id");
			final String sectionId = tagId.split("/")[0];
			Section section = sectionDAO.getSectionById(sectionId);
			final Tag refinementTag = new Tag(displayName, tagId, section);
						
			List<Refinement> refinementGroup = getRefinementGroup();		
			if (!refinementTag.isSectionKeyword()) {
				Log.d(TAG, "Adding refinement for tag: " + refinementTag.getName());
				refinementGroup.add(articleSetFactory.getRefinementForTag(refinementTag));
			} else {
				refinementGroup.add(articleSetFactory.getRefinementForSection(refinementTag.getSection()));
			}
		}
		
		boolean isContentTypeRefinement = currentRefinementGroupType.equals("type");
		if (isContentTypeRefinement) {
			final String tagId = attributes.getValue("id");
			Log.d(TAG, "Adding content type: " + tagId);
			List<Refinement> refinementGroup = getRefinementGroup();
			final Tag refinementTag = new Tag(displayName, tagId, null);
			refinementGroup.add(articleSetFactory.getRefinementForTag(refinementTag));			
		}
		
		boolean isSectionRefinement = currentRefinementGroupType.equals("section");
		if (isSectionRefinement) {
			final String sectionId = attributes.getValue("id");
			Section section = sectionDAO.getSectionById(sectionId);
			if (section != null) {
				Log.d(TAG, "Adding refinement for section: " + section.getName());
				List<Refinement> refinementGroup = getRefinementGroup();
				refinementGroup.add(articleSetFactory.getRefinementForSection(section));
			}
		}
		
		
		boolean isDateRefinement = currentRefinementGroupType.equals("date");
		if (isDateRefinement) {
			Log.d(TAG, "Adding date refinement: " + displayName);
			
			String refinedUrl = attributes.getValue("refined-url");
			final String fromDate = refinedUrl.split("from-date=")[1].substring(0, 10);			
			final String toDate = refinedUrl.split("to-date=")[1].substring(0, 10);
			
			List<Refinement> refinementGroup = getRefinementGroup();
			refinementGroup.add(articleSetFactory.getRefinementForDate(displayName, fromDate, toDate));
		}
		
	}
	
	
	private List<Refinement> getRefinementGroup() {
		List<Refinement> refinementGroup = refinements.get(currentRefinementGroupType);
		if (refinementGroup == null) {
			refinementGroup = new ArrayList<Refinement>();
			refinements.put(currentRefinementGroupType, refinementGroup);
		}
		return refinementGroup;
	}
	
}
