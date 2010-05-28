package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class Article implements Serializable {

	private static final long serialVersionUID = 4L;
	
	String id;
	String title;
	String standfirst;
	DateTime pubDate;
	String description;
	List<Author> authors;
	List<Keyword> keywords;
	String thumbnailUrl;

	
	public Article() {
		authors = new ArrayList<Author>();
		keywords = new ArrayList<Keyword>();
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

	public List<Author> getAuthors() {
		return authors;
	}
	
	public void addAuthor(Author author) {
		authors.add(author);
	}
	
	public String getAuthorNames() {
		return authors.toString();
	}
	
	public List<Keyword> getKeywords() {
		return keywords;
	}

	public void addKeyword(Keyword keyword) {
		keywords.add(keyword);
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

}
