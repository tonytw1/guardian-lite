package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ArticleCallback;
import nz.gen.wellington.guardian.android.api.filtering.HtmlCleaner;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;
import nz.gen.wellington.guardian.android.utils.DateTimeHelper;

import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;

public class ContentResultsHandler extends HandlerBase {

	public List<Article> articles;
	public Map<String, List<ArticleSet>> refinements;
	public String checksum;
	public String description;
	public StringBuilder currentElementContents;
	public Article currentArticle;
	public String currentField;
	public String currentRefinementGroupType;
	public ArticleCallback articleCallback;
	public List<Section> sections;
	private HtmlCleaner htmlCleaner;

	private boolean running = true;

	public ContentResultsHandler(ArticleCallback articleCallback, List<Section> sections, HtmlCleaner htmlCleaner) {
		this.articleCallback = articleCallback;
		this.sections = sections;
		this.htmlCleaner = htmlCleaner;
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
			throw new SAXException("Parser has been stopped");
		}

		if (name.equals("content")) {
			currentArticle = new Article();
			currentElementContents = new StringBuilder();
			currentArticle.setId(attributes.getValue("id"));

			final String sectionId = attributes.getValue("section-id");
			Section section = getSectionById(sectionId);
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

			if (attributes.getValue("type").equals("keyword")) {
				Section tagSection = getSectionById(attributes.getValue("section-id"));
				Tag tag = new Tag(attributes.getValue("web-title"), attributes.getValue("id"), tagSection);
				currentArticle.addKeyword(tag);
			}

			if (attributes.getValue("type").equals("contributor")) {
				Tag tag = new Tag(attributes.getValue("web-title"), attributes.getValue("id"), null);
				currentArticle.addAuthor(tag);
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

				boolean isTagRefinement = true; // TODO limit to tag typed - ie
				// not date
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
			if (currentArticle.getMainImageUrl() == null && attributes.getValue("type").equals("picture")) {
				currentArticle.setMainImageUrl(attributes.getValue("file"));
			}
		}
	}

	@Override
	public void endElement(String name) throws SAXException {
		super.endElement(name);

		if (currentField != null) {

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

			if (currentField.equals("body")) {
				currentArticle
						.setDescription(htmlCleaner.stripHtml(currentElementContents.toString()));
			}

			if (currentField.equals("caption")) {
				currentArticle.setCaption(htmlCleaner.stripHtml(currentElementContents.toString()));
			}

			currentField = null;
			currentElementContents = new StringBuilder();
		}

		if (name.equals("content")) {
			// boolean isArticleValid = article.getSection() != null || articles
			// if (isArticleValid) {

			// TODO article cleaning occurs here.

			articles.add(currentArticle);
			if (articleCallback != null) {
				articleCallback.articleReady(currentArticle);
			}
			// } else {
			// Log.w(TAG, "Invalid article: " + article.getId());
			// }
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

	// TODO this should be a section dao call
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
	
}
