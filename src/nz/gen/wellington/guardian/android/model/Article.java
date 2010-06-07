package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class Article implements Serializable {

	private static final long serialVersionUID = 5L;
	
	String id;
	String title;
	String byline;
	DateTime pubDate;
	String standfirst;
	String description;
	
	Section section;
	List<Tag> authors;
	List<Tag> keywords;
	String thumbnailUrl;
	String mainImageUrl;

	
	public Article() {
		authors = new ArrayList<Tag>();
		keywords = new ArrayList<Tag>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getStandfirst() {
		return standfirst;
	}

	public void setStandfirst(String standfirst) {
		this.standfirst = standfirst;
	}

	public DateTime getPubDate() {
		return pubDate;
	}

	public void setPubDate(DateTime dateTime) {
		this.pubDate = dateTime;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Tag> getAuthors() {
		return authors;
	}
	
	public void addAuthor(Tag author) {
		authors.add(author);
	}
	
	public String getAuthorNames() {
		return authors.toString();
	}
	
	public List<Tag> getKeywords() {
		return keywords;
	}

	public void addKeyword(Tag keyword) {
		keywords.add(keyword);
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getMainImageUrl() {
		return mainImageUrl;
	}

	public void setMainImageUrl(String mainImageUrl) {
		this.mainImageUrl = mainImageUrl;
	}

	public String getPubDateString() {
		if (this.pubDate != null) {
			return pubDate.toString("EEEE d MMMM yyyy HH.mm");			
		}
		return null;
	}

	public String getByline() {
		return byline;
	}

	public void setByline(String byline) {
		this.byline = byline;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}
	
}
